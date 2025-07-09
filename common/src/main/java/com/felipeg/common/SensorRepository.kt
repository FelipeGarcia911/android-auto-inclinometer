package com.felipeg.common

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.Surface
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
    private val linearAccelerationSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // A dedicated scope for managing sensor flows
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // StateFlow to hold calibration data, making it reactive
    private val calibrationData: StateFlow<Angle>
    private val calibrationDataUpdater: MutableStateFlow<Angle>

    // Cache latest raw readings for calibration
    private var lastRawAngle: Angle = Angle(0f, 0f, 0f)

    /**
     * Holds the current device rotation. Must be updated by the UI layer.
     * @Volatile ensures that writes to this property are immediately visible to other threads.
     */
    @Volatile
    var deviceRotation: Int = Surface.ROTATION_0

    /**
     * The UI layer (Activity/Fragment/Composable) must call this method to provide
     * the current display rotation, especially on configuration changes.
     * @param rotation The current display rotation (e.g., from `display.rotation`).
     */
    fun updateDeviceRotation(rotation: Int) {
        deviceRotation = when (rotation) {
            Surface.ROTATION_0, Surface.ROTATION_90, Surface.ROTATION_180, Surface.ROTATION_270 -> deviceRotation
            else -> Surface.ROTATION_0 // fallback
        }
    }

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
     * This flow now remaps the coordinate system to provide consistent pitch/roll
     * regardless of screen orientation (portrait/landscape).
     */
    private val rawAngleFlow: SharedFlow<Angle> = callbackFlow {
        val listener = object : SensorEventListener {
            private val rotationMatrix = FloatArray(9)
            private val remappedRotationMatrix = FloatArray(9)
            private val orientationAngles = FloatArray(3)

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return

                // Get the rotation matrix from the sensor event
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                // Remap the coordinate system based on the device's current rotation,
                // which is updated from the UI layer.
                val (axisX, axisY) = remapAxes(deviceRotation)

                SensorManager.remapCoordinateSystem(rotationMatrix, axisX, axisY, remappedRotationMatrix)

                // Get the orientation from the remapped matrix
                SensorManager.getOrientation(remappedRotationMatrix, orientationAngles)

                val yaw = toDegrees(orientationAngles[0].toDouble()).toFloat()
                val pitch = toDegrees(orientationAngles[1].toDouble()).toFloat()
                val roll = toDegrees(orientationAngles[2].toDouble()).toFloat()

                // Check for NaN values and skip if any are found
                if (roll.isNaN() || pitch.isNaN() || yaw.isNaN()) {
                    return // Skip this event if it contains NaN values
                }

                val angle = Angle(roll, pitch, yaw)
                Log.d("SensorRepository", "Raw angles → Pitch: $pitch, Roll: $roll, Yaw: $yaw, Rotation: $deviceRotation")

                lastRawAngle = angle // Cache the latest raw value
                trySend(angle).isSuccess
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        rotationSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        awaitClose { sensorManager.unregisterListener(listener) }
    }.shareIn(coroutineScope, SharingStarted.WhileSubscribed(5000), 1)

    val gForceFlow: SharedFlow<GForce> = callbackFlow<GForce> {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                    val x = event.values[0]
                    val y = event.values[1]
                    // z is not used for a 2D G-force meter

                    trySend(GForce(x, y)).isSuccess
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        linearAccelerationSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        awaitClose { sensorManager.unregisterListener(listener) }
    }.shareIn(coroutineScope, SharingStarted.WhileSubscribed(5000), 1)


    private fun remapAxes(rotation: Int): Pair<Int, Int> = when (rotation) {
        Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
        Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
        Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
        else -> SensorManager.AXIS_X to SensorManager.AXIS_Y // ROTATION_0
    }

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

    fun calibrateReset() {
        val newOffset = lastRawAngle
        calibrationDataUpdater.value = newOffset
        prefs.edit {
            putFloat(KEY_OFFSET_ROLL, 0.0F)
            putFloat(KEY_OFFSET_PITCH, 0.0F)
            putFloat(KEY_OFFSET_YAW, 0.0F)
        }
    }

    private companion object {
        const val PREFS_NAME = "sensor_prefs"
        const val KEY_OFFSET_ROLL = "offset_roll"
        const val KEY_OFFSET_PITCH = "offset_pitch"
        const val KEY_OFFSET_YAW = "offset_yaw"
    }
}
