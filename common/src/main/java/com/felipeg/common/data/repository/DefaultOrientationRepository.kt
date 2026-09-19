package com.felipeg.common.data.repository

import com.felipeg.common.data.filter.OrientationOutputFilterFactory
import com.felipeg.common.data.model.RawRotation
import com.felipeg.common.data.source.OrientationSensorDataSource
import com.felipeg.common.domain.engine.VehicleOrientationEngine
import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.common.domain.model.Orientation
import com.felipeg.common.domain.model.OrientationFilterPreset
import com.felipeg.common.domain.model.OrientationReading
import com.felipeg.common.domain.model.Quaternion
import com.felipeg.common.domain.repository.CalibrationRepository
import com.felipeg.common.domain.repository.OrientationRepository
import com.felipeg.common.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Singleton
class DefaultOrientationRepository @Inject constructor(
    private val dataSource: OrientationSensorDataSource,
    calibrationRepository: CalibrationRepository,
    settingsRepository: SettingsRepository,
    private val engine: VehicleOrientationEngine,
    private val filterFactory: OrientationOutputFilterFactory
) : OrientationRepository {
    private val deviceRotation = MutableStateFlow(DeviceRotation.ROTATION_0)

    private val pipeline = combine(
        dataSource.rotations,
        calibrationRepository.referenceRotation,
        deviceRotation,
        settingsRepository.settings
    ) { rawRotation, reference, rotation, settings ->
        PipelineInput(rawRotation, reference, rotation, settings.orientationFilter)
    }

    override val readings: Flow<OrientationReading> = flow {
        var previousReference: Quaternion? = null
        var previousRotation: DeviceRotation? = null
        var previousPreset: OrientationFilterPreset? = null
        var outputFilter = filterFactory.create(OrientationFilterPreset.BALANCED)

        pipeline.collect { input ->
            if (
                input.reference != previousReference ||
                input.deviceRotation != previousRotation ||
                input.filterPreset != previousPreset
            ) {
                outputFilter = filterFactory.create(input.filterPreset)
                previousReference = input.reference
                previousRotation = input.deviceRotation
                previousPreset = input.filterPreset
            }

            val result = engine.calculate(
                current = input.rawRotation.quaternion,
                reference = input.reference,
                deviceRotation = input.deviceRotation
            )
            val filtered = outputFilter.filter(result.calibratedOrientation)
            emit(
                OrientationReading(
                    sensorType = input.rawRotation.sensorType,
                    samplingRateHz = input.rawRotation.samplingRateHz,
                    currentRotation = input.rawRotation.quaternion,
                    referenceRotation = input.reference,
                    relativeRotation = result.relativeRotation,
                    rawOrientation = result.rawOrientation,
                    calibratedOrientation = result.calibratedOrientation,
                    filteredOrientation = filtered,
                    deviceRotation = input.deviceRotation
                )
            )
        }
    }

    override val orientations: Flow<Orientation> = readings.map { it.filteredOrientation }

    override fun start() = dataSource.start()

    override fun stop() = dataSource.stop()

    override fun updateDeviceRotation(rotation: DeviceRotation) {
        deviceRotation.value = rotation
    }

    private data class PipelineInput(
        val rawRotation: RawRotation,
        val reference: Quaternion?,
        val deviceRotation: DeviceRotation,
        val filterPreset: OrientationFilterPreset
    )
}
