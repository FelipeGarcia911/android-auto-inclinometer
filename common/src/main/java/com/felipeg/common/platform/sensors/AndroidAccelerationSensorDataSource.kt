package com.felipeg.common.platform.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.felipeg.common.data.model.RawAcceleration
import com.felipeg.common.data.source.AccelerationSensorDataSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAccelerationSensorDataSource @Inject constructor(
    private val sensorManager: SensorManager
) : AccelerationSensorDataSource {
    override val accelerations: Flow<RawAcceleration> = callbackFlow {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        if (sensor == null) {
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                    trySend(
                        RawAcceleration(
                            xMetersPerSecondSquared = event.values.getOrElse(0) { 0f },
                            yMetersPerSecondSquared = event.values.getOrElse(1) { 0f }
                        )
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        awaitClose { sensorManager.unregisterListener(listener) }
    }
}
