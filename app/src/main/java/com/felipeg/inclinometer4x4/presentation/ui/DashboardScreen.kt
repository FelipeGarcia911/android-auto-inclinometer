package com.felipeg.inclinometer4x4.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
        BoxWithConstraints {
            val isLandscape = maxWidth > maxHeight

            val inclinometer = @Composable {
                CombinedInclinometer(
                    roll = angle.roll,
                    pitch = angle.pitch,
                    modifier = Modifier.size(300.dp)
                )
            }

            val gForceMeter = @Composable {
                GForceMeter(
                    gForceX = gForce.x,
                    gForceY = gForce.y,
                    maxGForce = maxGForce,
                    modifier = Modifier.size(300.dp)
                )
            }

            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    inclinometer()
                    Spacer(modifier = Modifier.width(32.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        gForceMeter()
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    inclinometer()
                    Spacer(modifier = Modifier.height(32.dp))
                    gForceMeter()
                }
            }
        }
    }
}

