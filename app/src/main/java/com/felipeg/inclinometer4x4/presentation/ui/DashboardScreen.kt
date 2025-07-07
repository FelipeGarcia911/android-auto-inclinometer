package com.felipeg.inclinometer4x4.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.felipeg.inclinometer4x4.R
import com.felipeg.inclinometer4x4.presentation.ui.component.CombinedInclinometer
import com.felipeg.inclinometer4x4.presentation.ui.component.GForceMeter
import com.felipeg.inclinometer4x4.presentation.viewmodel.SensorViewModel

@Composable
fun DashboardScreen(
    viewModel: SensorViewModel = hiltViewModel()
) {
    val angle by viewModel.angleState.collectAsState()
    val gForce by viewModel.gForceState.collectAsState()
    val maxGForce by viewModel.maxGForceState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_portrait),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillHeight,
        )
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
                modifier = Modifier.size(300.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Roll: ${angle.roll.toInt()}°, Pitch: ${angle.pitch.toInt()}°", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(32.dp))
            GForceMeter(
                gForceX = gForce.x,
                gForceY = gForce.y,
                maxGForce = maxGForce,
                modifier = Modifier.size(300.dp)
            )
        }
    }
}
