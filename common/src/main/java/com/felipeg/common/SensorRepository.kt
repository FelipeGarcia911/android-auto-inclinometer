package com.felipeg.common

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class Angle(
    val roll: Float,
    val pitch: Float,
    val yaw: Float
)

class SensorRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var rollOffset: Float = prefs.getFloat(KEY_OFFSET_ROLL, 0f)
    private var pitchOffset: Float = prefs.getFloat(KEY_OFFSET_PITCH, 0f)
    private var yawOffset: Float = prefs.getFloat(KEY_OFFSET_YAW, 0f)

    fun calibrateZero(angle: Angle) {
        rollOffset = angle.roll
        pitchOffset = angle.pitch
        yawOffset = angle.yaw
        prefs.edit {
            putFloat(KEY_OFFSET_ROLL, rollOffset)
                .putFloat(KEY_OFFSET_PITCH, pitchOffset)
                .putFloat(KEY_OFFSET_YAW, yawOffset)
        }
    }

    val angleFlow: Flow<Angle> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)

                val rawYaw = Math.toDegrees(orientation[0].toDouble()).toFloat()
                val rawPitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
                val rawRoll = Math.toDegrees(orientation[2].toDouble()).toFloat()

                val yaw   = rawYaw - yawOffset
                val pitch = rawPitch - pitchOffset
                val roll  = rawRoll - rollOffset

                trySend(Angle(roll, pitch, yaw)).isSuccess
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // No implementado
            }
        }

        rotationSensor?.also {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        awaitClose { sensorManager.unregisterListener(listener) }
    }.distinctUntilChanged()

    companion object {
        private const val PREFS_NAME = "sensor_prefs"
        private const val KEY_OFFSET_ROLL = "offset_roll"
        private const val KEY_OFFSET_PITCH = "offset_pitch"
        private const val KEY_OFFSET_YAW = "offset_yaw"
    }
}
