package com.felipeg.inclinometer4x4.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.felipeg.common.domain.model.Orientation
import com.felipeg.common.domain.model.Quaternion
import com.felipeg.inclinometer4x4.platform.rotation.currentDeviceRotation
import com.felipeg.inclinometer4x4.presentation.model.SensorDiagnosticsUiState
import com.felipeg.inclinometer4x4.presentation.viewmodel.SensorViewModel
import java.util.Locale

@Composable
fun SensorDiagnosticsScreen(
    viewModel: SensorViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.startSensors()
                Lifecycle.Event.ON_PAUSE -> viewModel.stopSensors()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val deviceRotation = currentDeviceRotation()
    LaunchedEffect(deviceRotation) { viewModel.onRotationChanged(deviceRotation) }

    DiagnosticsContent(
        diagnostics = uiState.diagnostics,
        onBack = onBack
    )
}

@Composable
private fun DiagnosticsContent(
    diagnostics: SensorDiagnosticsUiState,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Sensor Diagnostics", style = MaterialTheme.typography.headlineSmall)
        }

        DiagnosticValue("Sensor active", diagnostics.sensorActive.toString())
        DiagnosticValue("Sensor", diagnostics.sensorType?.name ?: "Waiting for sensor")
        DiagnosticValue("Sampling rate", "${diagnostics.samplingRateHz.format(1)} Hz")
        DiagnosticValue("Device rotation", diagnostics.deviceRotation.name)
        QuaternionValues("Current quaternion", diagnostics.currentRotation)
        QuaternionValues("Reference quaternion", diagnostics.referenceRotation)
        OrientationValues("Raw orientation", diagnostics.rawOrientation)
        OrientationValues("Relative / calibrated orientation", diagnostics.calibratedOrientation)
        OrientationValues("Filtered orientation", diagnostics.filteredOrientation)
    }
}

@Composable
private fun QuaternionValues(title: String, quaternion: Quaternion?) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    DiagnosticValue("x", quaternion?.x?.format() ?: "—")
    DiagnosticValue("y", quaternion?.y?.format() ?: "—")
    DiagnosticValue("z", quaternion?.z?.format() ?: "—")
    DiagnosticValue("w", quaternion?.w?.format() ?: "—")
}

@Composable
private fun OrientationValues(title: String, orientation: Orientation) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    DiagnosticValue("Pitch", "${orientation.pitch.format(2)}°")
    DiagnosticValue("Roll", "${orientation.roll.format(2)}°")
}

@Composable
private fun DiagnosticValue(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value)
    }
}

private fun Float.format(decimals: Int = 5): String =
    String.format(Locale.US, "%.${decimals}f", this)
