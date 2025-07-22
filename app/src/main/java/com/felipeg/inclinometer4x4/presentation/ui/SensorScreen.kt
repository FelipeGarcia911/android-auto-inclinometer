package com.felipeg.inclinometer4x4.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipeg.common.FSensorViewModel
import com.felipeg.inclinometer4x4.presentation.ui.component.CombinedInclinometer
import com.tracqi.fsensor.sensor.FSensorEvent
import com.tracqi.fsensor.sensor.FSensorEventListener
import kotlin.math.PI

@Composable
fun SensorScreen() {
    // 1. Get an instance of the ViewModel from the "common" module
    val viewModel: FSensorViewModel = viewModel()

    // 2. State to hold pitch and roll in degrees
    var anglesInDegrees by remember { mutableStateOf(Pair(0f, 0f)) } // Pair(pitch, roll)

    // 3. Create the listener to receive and convert sensor data
    val listener = remember {
        object : FSensorEventListener {
            override fun onSensorChanged(event: FSensorEvent) {
                val pitchRadians = event.values[1]
                val rollRadians = event.values[2]

                // Convert radians to degrees and update state
                anglesInDegrees = Pair(
                    first = pitchToDegrees(pitchRadians),
                    second = radiansToDegrees(rollRadians)
                )
            }
        }
    }

    // 4. Manage the lifecycle to register/unregister the listener
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.registerRotationSensorListener(listener)
                Lifecycle.Event.ON_PAUSE -> viewModel.unregisterRotationSensorListener(listener)
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 5. UI with the CombinedInclinometer and text values
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CombinedInclinometer(
                pitch = anglesInDegrees.first,
                roll = anglesInDegrees.second,
                modifier = Modifier.size(300.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Pitch: %.1f°".format(anglesInDegrees.first),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Roll: %.1f°".format(anglesInDegrees.second),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Converts an angle from radians to degrees.
 * @param radians The angle in radians.
 * @return The angle in degrees.
 */
private fun radiansToDegrees(radians: Float): Float {
    return (radians * 180 / PI).toFloat()
}

private fun pitchToDegrees(pitch: Float): Float {
    // Adjust pitch to match the expected range
    var sign = if (pitch > 0) -1 else 1
    return -radiansToDegrees(pitch + (sign * PI / 2).toFloat()) // Adjust pitch to match the expected range
}

