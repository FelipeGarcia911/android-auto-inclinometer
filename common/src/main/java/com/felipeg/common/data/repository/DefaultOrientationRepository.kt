package com.felipeg.common.data.repository

import com.felipeg.common.data.mapper.OrientationMapper
import com.felipeg.common.data.source.OrientationSensorDataSource
import com.felipeg.common.domain.calculator.CalibrationCalculator
import com.felipeg.common.domain.calculator.RotationTransformer
import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.common.domain.model.Orientation
import com.felipeg.common.domain.repository.CalibrationRepository
import com.felipeg.common.domain.repository.OrientationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultOrientationRepository @Inject constructor(
    dataSource: OrientationSensorDataSource,
    calibrationRepository: CalibrationRepository,
    private val mapper: OrientationMapper,
    private val rotationTransformer: RotationTransformer,
    private val calibrationCalculator: CalibrationCalculator
) : OrientationRepository {
    private val source = dataSource
    private val deviceRotation = MutableStateFlow(DeviceRotation.ROTATION_0)

    override val orientations: Flow<Orientation> = combine(
        source.orientations,
        deviceRotation,
        calibrationRepository.offset
    ) { rawOrientation, rotation, offset ->
        val orientation = mapper.map(rawOrientation)
        val rotated = rotationTransformer.transform(orientation, rotation)
        calibrationCalculator.apply(rotated, offset)
    }

    override fun start() = source.start()

    override fun stop() = source.stop()

    override fun updateDeviceRotation(rotation: DeviceRotation) {
        deviceRotation.value = rotation
    }
}
