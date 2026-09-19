package com.felipeg.inclinometer4x4.presentation.ui

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.felipeg.inclinometer4x4.ui.theme.GRWhite
import com.felipeg.inclinometer4x4.presentation.ui.component.CombinedInclinometer
import com.felipeg.inclinometer4x4.presentation.ui.component.GForceMeter
import com.felipeg.inclinometer4x4.presentation.viewmodel.SensorViewModel
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.felipeg.inclinometer4x4.Screen
import com.felipeg.inclinometer4x4.platform.rotation.currentDeviceRotation

@Composable
fun DashboardScreen(
    viewModel: SensorViewModel = hiltViewModel(),
    onScreenChange: (Screen) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    // This is the correct place for the lifecycle observer
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.startSensors()
                Lifecycle.Event.ON_PAUSE -> viewModel.stopSensors()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val deviceRotation = currentDeviceRotation()
    LaunchedEffect(deviceRotation) {
        viewModel.onRotationChanged(deviceRotation)
    }

    Box(modifier = Modifier.fillMaxSize().background(GRBlack)) {
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
                    text = { Text("SENSOR DIAGNOSTICS", style = MaterialTheme.typography.labelLarge.copy(color = GRWhite)) },
                    onClick = {
                        onScreenChange(Screen.Diagnostics)
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("TOGGLE SCREEN", style = MaterialTheme.typography.labelLarge.copy(color = GRWhite)) },
                    onClick = {
                        viewModel.toggleScreenOrientation()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("CALIBRATE ZERO", style = MaterialTheme.typography.labelLarge.copy(color = GRWhite)) },
                    onClick = {
                        viewModel.calibrateZero()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("RESET CALIBRATION", style = MaterialTheme.typography.labelLarge.copy(color = GRWhite)) },
                    onClick = {
                        viewModel.resetCalibration()
                        showMenu = false
                    }
                )
            }
        }

        BoxWithConstraints {
            val isLandscape = maxWidth > maxHeight

            val inclinometer = @Composable {
                CombinedInclinometer(
                    roll = uiState.orientation.roll,
                    pitch = uiState.orientation.pitch,
                    modifier = Modifier.size(300.dp)
                )
            }

            val gForceMeter = @Composable {
                GForceMeter(
                    gForceX = uiState.gForce.x,
                    gForceY = uiState.gForce.y,
                    maxGForce = uiState.maxGForce,
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
