package com.felipeg.common

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.lang.StrictMath.toDegrees
import javax.inject.Inject
import javax.inject.Singleton

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
 * Uses the rotation vector sensor for roll, pitch, and yaw.
 */
@Singleton
class SensorRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // A dedicated scope for managing sensor flows
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // StateFlow to hold calibration data, making it reactive
    private val calibrationData: StateFlow<Angle>
    private val calibrationDataUpdater: MutableStateFlow<Angle>

    // Cache latest raw readings for calibration
    private var lastRawAngle: Angle = Angle(0f, 0f, 0f)

    init {
        val initialOffset = Angle(
            roll = prefs.getFloat(KEY_OFFSET_ROLL, 0f),
            pitch = prefs.getFloat(KEY_OFFSET_PITCH, 0f),
            yaw = prefs.getFloat(KEY_OFFSET_YAW, 0f)
        )
        calibrationDataUpdater = MutableStateFlow(initialOffset)
        calibrationData = calibrationDataUpdater.asStateFlow()
    }

    /**
     * A hot flow of raw angles from the rotation vector sensor, shared across collectors.
     */
    private val rawAngleFlow: SharedFlow<Angle> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotMat = FloatArray(9)
                    val orientationValues = FloatArray(3)
                    SensorManager.getRotationMatrixFromVector(rotMat, event.values)
                    SensorManager.getOrientation(rotMat, orientationValues)

                    val yaw = toDegrees(orientationValues[0].toDouble()).toFloat()
                    val pitch = toDegrees(orientationValues[1].toDouble()).toFloat()
                    val roll = toDegrees(orientationValues[2].toDouble()).toFloat()

                    val angle = Angle(roll, pitch, yaw)
                    lastRawAngle = angle // Cache the latest raw value
                    trySend(angle).isSuccess
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        rotationSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        awaitClose { sensorManager.unregisterListener(listener) }
    }.shareIn(coroutineScope, SharingStarted.WhileSubscribed(5000), 1)


    /**
     * Flow emitting calibrated angles, produced by combining raw data and calibration offsets.
     * This is now fully reactive.
     */
    val angleFlow: Flow<Angle> = rawAngleFlow
        .combine(calibrationData) { raw, offset ->
            Angle(
                roll = raw.roll - offset.roll,
                pitch = raw.pitch - offset.pitch,
                yaw = raw.yaw - offset.yaw
            )
        }
        .distinctUntilChanged()

    /**
     * Sets current raw angles as the zero reference by updating the calibration StateFlow.
     */
    fun calibrateZero() {
        val newOffset = lastRawAngle
        calibrationDataUpdater.value = newOffset
        prefs.edit {
            putFloat(KEY_OFFSET_ROLL, newOffset.roll)
            putFloat(KEY_OFFSET_PITCH, newOffset.pitch)
            putFloat(KEY_OFFSET_YAW, newOffset.yaw)
        }
    }

    private companion object {
        const val PREFS_NAME = "sensor_prefs"
        const val KEY_OFFSET_ROLL = "offset_roll"
        const val KEY_OFFSET_PITCH = "offset_pitch"
        const val KEY_OFFSET_YAW = "offset_yaw"
    }
}
