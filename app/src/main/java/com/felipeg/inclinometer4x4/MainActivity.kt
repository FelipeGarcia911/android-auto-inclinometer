package com.felipeg.inclinometer4x4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.felipeg.common.SensorRepository
import com.felipeg.common.Angle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AngleDisplay()
                }
            }
        }
    }
}

@Composable
fun AngleDisplay() {
    val context = LocalContext.current
    val sensorRepo = remember { SensorRepository(context) }
    var currentAngle by remember { mutableStateOf(Angle(0f, 0f, 0f)) }
    var justCalibrated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        sensorRepo.angleFlow.collect { angle ->
            currentAngle = angle
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Roll: ${currentAngle.roll.toInt()}°",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pitch: ${currentAngle.pitch.toInt()}°",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Yaw: ${currentAngle.yaw.toInt()}°",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { sensorRepo.calibrateZero(currentAngle) }) {
            Text(text = "Calibrar cero")
        }

        if (justCalibrated) {
            Text(
                "Calibrado!",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
