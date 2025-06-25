package com.felipeg.inclinometer4x4.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.felipeg.inclinometer4x4.presentation.viewmodel.AngleViewModel

@Composable
fun AngleScreen(viewModel: AngleViewModel = hiltViewModel()) {
    val angle by viewModel.angleState.collectAsState()
    var justCalibrated by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        listOf(
            "Roll"  to angle.roll,
            "Pitch" to angle.pitch,
            "Yaw"   to angle.yaw
        ).forEach { (label, value) ->
            Text("$label: ${value.toInt()}°", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            viewModel.onCalibrate()
            justCalibrated = true
        }) {
            Text("Calibrar cero")
        }

        if (justCalibrated) {
            Text("¡Calibrado!", color = MaterialTheme.colorScheme.primary)
        }
    }
}
