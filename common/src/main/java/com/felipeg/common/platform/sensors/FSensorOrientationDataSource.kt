package com.felipeg.common.platform.sensors

import android.hardware.SensorManager
import com.felipeg.common.data.model.RawOrientation
import com.felipeg.common.data.source.OrientationSensorDataSource
import com.felipeg.common.domain.model.SensorSamplingPeriod
import com.felipeg.common.domain.repository.SettingsRepository
import com.felipeg.common.platform.filters.OrientationFilterFactory
import com.tracqi.fsensor.sensor.FSensorEventListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class FSensorOrientationDataSource @Inject constructor(
    private val sensorManager: SensorManager,
    private val filterFactory: OrientationFilterFactory,
    settingsRepository: SettingsRepository
) : OrientationSensorDataSource {
    private val active = MutableStateFlow(false)

    override val orientations: Flow<RawOrientation> = active.flatMapLatest { isActive ->
        if (!isActive) {
            emptyFlow()
        } else {
            settingsRepository.settings.flatMapLatest { settings ->
                callbackFlow {
                    val sensor = filterFactory.create(settings.orientationFilter, sensorManager)
                    val listener = FSensorEventListener { event ->
                        trySend(
                            RawOrientation(
                                azimuthRadians = event.values.getOrElse(0) { 0f },
                                pitchRadians = event.values.getOrElse(1) { 0f },
                                rollRadians = event.values.getOrElse(2) { 0f }
                            )
                        )
                    }
                    sensor.registerListener(listener, settings.samplingPeriod.toSensorDelay())
                    awaitClose { sensor.unregisterListener(listener) }
                }
            }
        }
    }

    override fun start() {
        active.value = true
    }

    override fun stop() {
        active.value = false
    }

    private fun SensorSamplingPeriod.toSensorDelay(): Int = when (this) {
        SensorSamplingPeriod.FASTEST -> SensorManager.SENSOR_DELAY_FASTEST
        SensorSamplingPeriod.GAME -> SensorManager.SENSOR_DELAY_GAME
        SensorSamplingPeriod.UI -> SensorManager.SENSOR_DELAY_UI
        SensorSamplingPeriod.NORMAL -> SensorManager.SENSOR_DELAY_NORMAL
    }
}
