package com.felipeg.common

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.lang.StrictMath.toDegrees
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Data class representing device orientation angles in degrees.
 * roll: side-to-side tilt; pitch: front-to-back tilt; yaw: heading.
 */
data class Angle(
    val roll: Float,
    val pitch: Float,
    val yaw: Float
)

/**
 * Repository providing a Flow of raw and calibrated Angle values.
 * Uses the accelerometer for roll/pitch and rotation vector for yaw.
 */
class SensorRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val rotationSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var rollOffset: Float = prefs.getFloat(KEY_OFFSET_ROLL, 0f)
    private var pitchOffset: Float = prefs.getFloat(KEY_OFFSET_PITCH, 0f)
    private var yawOffset: Float = prefs.getFloat(KEY_OFFSET_YAW, 0f)

    // Cache latest raw readings for calibration
    private var lastRawAngle: Angle = Angle(0f, 0f, 0f)

    /**
     * Flow emitting raw angles: roll/pitch from accelerometer, yaw from rotation vector.
     */
    private val rawAngleFlow: Flow<Angle> = callbackFlow {
        val listener = object : SensorEventListener {
            private var accelValues = FloatArray(3)
            private var orientationValues = FloatArray(3)

            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accelValues = event.values.clone()
                    }
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        // Compute orientation for yaw
                        val rotMat = FloatArray(9)
                        SensorManager.getRotationMatrixFromVector(rotMat, event.values)
                        SensorManager.getOrientation(rotMat, orientationValues)
                    }
                }
                // Only emit when both accel and orientation have data
                if (accelValues.any { it != 0f } && orientationValues.any { it != 0f }) {
                    val rawRoll = toDegrees(
                        atan2(
                            accelValues[0].toDouble(),  // X-axis side tilt
                            accelValues[2].toDouble()   // Z-axis vertical
                        )
                    ).toFloat()
                    val rawPitch = toDegrees(
                        atan2(
                            accelValues[1].toDouble(),
                            sqrt(
                                (accelValues[0] * accelValues[0] + accelValues[2] * accelValues[2]).toDouble()
                            )
                        )
                    ).toFloat()
                    val rawYaw = toDegrees(orientationValues[0].toDouble()).toFloat()

                    val angle = Angle(rawRoll, rawPitch, rawYaw)
                    lastRawAngle = angle
                    trySend(angle).isSuccess
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME) }
        rotationSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME) }

        awaitClose { sensorManager.unregisterListener(listener) }
    }.distinctUntilChanged()

    /**
     * Flow emitting calibrated angles (raw minus stored offsets).
     */
    val angleFlow: Flow<Angle>
        get() = rawAngleFlow.map { raw ->
            Angle(
                roll = raw.roll - rollOffset,
                pitch = raw.pitch - pitchOffset,
                yaw = raw.yaw - yawOffset
            )
        }.distinctUntilChanged()

    /**
     * Sets current raw angles as zero reference.
     * Call with device in the desired zero orientation.
     */
    fun calibrateZero() {
        rollOffset = lastRawAngle.roll
        pitchOffset = lastRawAngle.pitch
        yawOffset = lastRawAngle.yaw
        prefs.edit {
            putFloat(KEY_OFFSET_ROLL, rollOffset)
            putFloat(KEY_OFFSET_PITCH, pitchOffset)
            putFloat(KEY_OFFSET_YAW, yawOffset)
        }
    }

    private companion object {
        const val PREFS_NAME = "sensor_prefs"
        const val KEY_OFFSET_ROLL = "offset_roll"
        const val KEY_OFFSET_PITCH = "offset_pitch"
        const val KEY_OFFSET_YAW = "offset_yaw"
    }
}
