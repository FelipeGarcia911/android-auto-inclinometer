package com.felipeg.inclinometer4x4.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.felipeg.common.Angle
import com.felipeg.inclinometer4x4.presentation.ui.component.AngleDial
import com.felipeg.inclinometer4x4.presentation.viewmodel.AngleViewModel

@Composable
fun AngleScreen(
    viewModel: AngleViewModel = hiltViewModel()
) {
    val angle by viewModel.angleState.collectAsState()
    var justCalibrated by remember { mutableStateOf(false) }
    val onCalibrate: () -> Unit = {
        viewModel.onCalibrate()
        justCalibrated = true
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DialsSection(angle)
            Spacer(modifier = Modifier.width(32.dp))
            CalibrationSection(onCalibrate, justCalibrated)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DialsSection(angle)
            Spacer(modifier = Modifier.height(24.dp))
            CalibrationSection(onCalibrate, justCalibrated)
        }
    }
}

@Composable
private fun DialsSection(angle: Angle) {
    DialColumn(label = "Roll", value = angle.roll, minAngle = -45f, maxAngle = 45f)
    DialColumn(label = "Pitch", value = angle.pitch, minAngle = -45f, maxAngle = 45f)
}

@Composable
private fun CalibrationSection(
    onCalibrate: () -> Unit,
    justCalibrated: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onCalibrate) {
            Text("Calibrar cero")
        }
        if (justCalibrated) {
            Text(
                text = "¡Calibrado!",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun DialColumn(
    label: String,
    value: Float,
    minAngle: Float,
    maxAngle: Float,
    majorStep: Float = (maxAngle - minAngle) / 6,
    minorStep: Float = majorStep / 3
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        AngleDial(
            angle = value,
            modifier = Modifier.size(200.dp),
            radius = 100.dp,
            minAngle = minAngle,
            maxAngle = maxAngle,
            majorStep = majorStep,
            minorStep = minorStep
        )
        Text("${value.toInt()}°", style = MaterialTheme.typography.bodySmall)
    }
}
