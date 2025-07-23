package com.felipeg.inclinometer4x4.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.hardware.SensorManager
import com.felipeg.common.Preferences
import com.felipeg.common.model.Angle
import com.felipeg.inclinometer4x4.domain.repository.FSensorRepository
import com.tracqi.fsensor.sensor.FSensor
import com.tracqi.fsensor.sensor.FSensorEventListener
import com.tracqi.fsensor.sensor.orientation.ComplementaryOrientationFSensor
import com.tracqi.fsensor.sensor.orientation.KalmanOrientationFSensor
import com.tracqi.fsensor.sensor.orientation.LowPassOrientationFSensor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI

@Singleton
class FSensorRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FSensorRepository, SharedPreferences.OnSharedPreferenceChangeListener {

    private val _orientationFlow = MutableStateFlow(Angle(0f, 0f, 0f))
    override val orientationFlow: StateFlow<Angle> = _orientationFlow.asStateFlow()

    private var rotationFSensor: FSensor? = null
    private var sensorEventListener: FSensorEventListener? = null

    init {
        initFSensor()
        Preferences.registerPreferenceChangeListener(context, this)
    }

    override fun start() {
        if (sensorEventListener != null) {
            // Already started
            return
        }
        sensorEventListener = FSensorEventListener { event ->
            val pitch = pitchToDegrees(event.values[1])
            val roll = radiansToDegrees(event.values[2])
            _orientationFlow.value = Angle(pitch = pitch, roll = roll, azimuth = 0.0f)
        }
        rotationFSensor?.registerListener(sensorEventListener, Preferences.getSensorFrequencyPrefs(context))
    }

    override fun stop() {
        rotationFSensor?.unregisterListener(sensorEventListener)
        sensorEventListener = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Re-initialize the sensor when preferences change
        stop()
        initFSensor()
        start()
    }

    private fun initFSensor() {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Logic adapted from FSensorViewModel in 'common' module
        rotationFSensor = when {
            Preferences.getPrefFSensorLpfLinearAccelerationEnabled(context) -> {
                LowPassOrientationFSensor(sensorManager)
            }
            Preferences.getPrefFSensorComplimentaryLinearAccelerationEnabled(context) -> {
                ComplementaryOrientationFSensor(sensorManager)
            }
            Preferences.getPrefFSensorKalmanLinearAccelerationEnabled(context) -> {
                KalmanOrientationFSensor(sensorManager)
            }
            else -> null
        }
    }

    // Convert radians to degrees
    private fun radiansToDegrees(radians: Float): Float {
        if (radians.isNaN() || radians.isInfinite()) {
            return 0f // Handle invalid values
        }
        return (radians * 180 / PI).toFloat()
    }

    // Convert pitch from radians to degrees, adjusting for the device's orientation
    private fun pitchToDegrees(pitch: Float): Float {
        if (pitch.isNaN() || pitch.isInfinite()) {
            return 0f // Handle invalid values
        }
        val sign = if (pitch > 0) -1 else 1
        return -radiansToDegrees(pitch + (sign * PI / 2).toFloat())
    }
}
