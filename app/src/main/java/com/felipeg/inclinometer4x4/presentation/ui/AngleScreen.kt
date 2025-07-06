package com.felipeg.inclinometer4x4.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.felipeg.inclinometer4x4.presentation.ui.component.CombinedInclinometer
import com.felipeg.inclinometer4x4.presentation.viewmodel.AngleViewModel

@Composable
fun AngleScreen(
    viewModel: AngleViewModel = hiltViewModel()
) {
    val angle by viewModel.angleState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CombinedInclinometer(
            roll = angle.roll,
            pitch = angle.pitch,
            modifier = Modifier.size(400.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Roll: ${angle.roll.toInt()}°, Pitch: ${angle.pitch.toInt()}°", style = MaterialTheme.typography.headlineSmall)
    }
}
