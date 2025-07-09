package com.felipeg.inclinometer4x4.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.felipeg.inclinometer4x4.ui.theme.GRBlack
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.felipeg.inclinometer4x4.R
import com.felipeg.inclinometer4x4.ui.theme.GRWhite
import com.felipeg.inclinometer4x4.presentation.ui.component.CombinedInclinometer
import com.felipeg.inclinometer4x4.presentation.ui.component.GForceMeter
import com.felipeg.inclinometer4x4.presentation.viewmodel.SensorViewModel
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.felipeg.inclinometer4x4.Screen

@Composable
fun DashboardScreen(
    viewModel: SensorViewModel = hiltViewModel(),
    onScreenChange: (Screen) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val angle by viewModel.angleState.collectAsState()
    val gForce by viewModel.gForceState.collectAsState()
    val maxGForce by viewModel.maxGForceState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_img),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )
        // Black overlay for GR aesthetic
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GRBlack.copy(alpha = 0.5f))
        )

        // Container for Menu Button and DropdownMenu
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { showMenu = true }
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Menu", tint = GRWhite)
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(GRBlack)
            ) {
                DropdownMenuItem(
                    text = { Text("ABOUT", style = MaterialTheme.typography.labelLarge.copy(color = GRWhite)) },
                    onClick = {
                        onScreenChange(Screen.About)
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("RESET", style = MaterialTheme.typography.labelLarge.copy(color = GRWhite)) },
                    onClick = {
                        viewModel.onReset()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("CALIBRATE", style = MaterialTheme.typography.labelLarge.copy(color = GRWhite)) },
                    onClick = {
                        viewModel.onCalibrate()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("TOGGLE ORIENTATION", style = MaterialTheme.typography.labelLarge.copy(color = GRWhite)) },
                    onClick = {
                        viewModel.toggleOrientation()
                        showMenu = false
                    }
                )

            }
        }

        BoxWithConstraints {
            val isLandscape = maxWidth > maxHeight

            val inclinometer = @Composable {
                val (rollValue, pitchValue) = if (isLandscape) {
                    angle.pitch to angle.roll // Intercambiar en landscape
                } else {
                    angle.roll to angle.pitch // Normal en portrait
                }
                CombinedInclinometer(
                    roll = rollValue,
                    pitch = pitchValue,
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
                    horizontalArrangement = Arrangement.SpaceEvenly,
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

