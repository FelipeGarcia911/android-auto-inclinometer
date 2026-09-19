package com.felipeg.common.platform.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.felipeg.common.data.model.RawRotation
import com.felipeg.common.data.source.OrientationSensorDataSource
import com.felipeg.common.domain.model.Quaternion
import com.felipeg.common.domain.model.RotationSensorType
import com.felipeg.common.domain.model.SensorSamplingPeriod
import com.felipeg.common.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class AndroidRotationVectorDataSource @Inject constructor(
    private val sensorManager: SensorManager,
    settingsRepository: SettingsRepository
) : OrientationSensorDataSource {
    private val active = MutableStateFlow(false)

    override val rotations: Flow<RawRotation> = active.flatMapLatest { isActive ->
        if (!isActive) {
            emptyFlow()
        } else {
            settingsRepository.settings.flatMapLatest { settings ->
                observeRotation(settings.samplingPeriod)
            }
        }
    }

    override fun start() {
        active.value = true
    }

    override fun stop() {
        active.value = false
    }

    private fun observeRotation(samplingPeriod: SensorSamplingPeriod): Flow<RawRotation> =
        callbackFlow {
            val sensor = preferredRotationSensor()
            if (sensor == null) {
                close(IllegalStateException("No rotation vector sensor is available"))
                return@callbackFlow
            }
            val sensorType = sensor.toRotationSensorType()
            var lastTimestampNanos = 0L
            var smoothedRateHz = 0f

            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val quaternionValues = FloatArray(4)
                    SensorManager.getQuaternionFromVector(quaternionValues, event.values)
                    val deltaNanos = event.timestamp - lastTimestampNanos
                    if (lastTimestampNanos != 0L && deltaNanos > 0L) {
                        val currentRate = NANOS_PER_SECOND / deltaNanos.toFloat()
                        smoothedRateHz = if (smoothedRateHz == 0f) {
                            currentRate
                        } else {
                            RATE_SMOOTHING * currentRate + (1f - RATE_SMOOTHING) * smoothedRateHz
                        }
                    }
                    lastTimestampNanos = event.timestamp

                    trySend(
                        RawRotation(
                            quaternion = Quaternion(
                                x = quaternionValues[1],
                                y = quaternionValues[2],
                                z = quaternionValues[3],
                                w = quaternionValues[0]
                            ).normalized(),
                            sensorType = sensorType,
                            samplingRateHz = smoothedRateHz
                        )
                    )
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }

            val registered = sensorManager.registerListener(
                listener,
                sensor,
                samplingPeriod.toSensorDelay()
            )
            if (!registered) {
                close(IllegalStateException("Unable to register rotation vector listener"))
                return@callbackFlow
            }
            awaitClose { sensorManager.unregisterListener(listener) }
        }

    private fun preferredRotationSensor(): Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private fun Sensor.toRotationSensorType(): RotationSensorType = when (type) {
        Sensor.TYPE_GAME_ROTATION_VECTOR -> RotationSensorType.GAME_ROTATION_VECTOR
        else -> RotationSensorType.ROTATION_VECTOR
    }

    private fun SensorSamplingPeriod.toSensorDelay(): Int = when (this) {
        SensorSamplingPeriod.FASTEST -> SensorManager.SENSOR_DELAY_FASTEST
        SensorSamplingPeriod.GAME -> SensorManager.SENSOR_DELAY_GAME
        SensorSamplingPeriod.UI -> SensorManager.SENSOR_DELAY_UI
        SensorSamplingPeriod.NORMAL -> SensorManager.SENSOR_DELAY_NORMAL
    }

    private companion object {
        const val NANOS_PER_SECOND = 1_000_000_000f
        const val RATE_SMOOTHING = 0.15f
    }
}
