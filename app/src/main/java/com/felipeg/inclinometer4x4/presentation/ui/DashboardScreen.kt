package com.felipeg.inclinometer4x4.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.felipeg.common.domain.model.GForce
import com.felipeg.common.domain.model.Orientation
import com.felipeg.common.domain.model.Quaternion
import com.felipeg.inclinometer4x4.Screen
import com.felipeg.inclinometer4x4.platform.rotation.currentDeviceRotation
import com.felipeg.inclinometer4x4.presentation.model.DashboardUiState
import com.felipeg.inclinometer4x4.presentation.model.SensorDiagnosticsUiState
import com.felipeg.inclinometer4x4.presentation.ui.component.AngleReadout
import com.felipeg.inclinometer4x4.presentation.ui.component.CalibrationIndicator
import com.felipeg.inclinometer4x4.presentation.ui.component.CompactGForce
import com.felipeg.inclinometer4x4.presentation.ui.component.OffroadInclinometer
import com.felipeg.inclinometer4x4.presentation.ui.component.angleZoneColor
import com.felipeg.inclinometer4x4.presentation.viewmodel.SensorViewModel
import com.felipeg.inclinometer4x4.ui.theme.Inclinometer4x4Theme
import com.felipeg.inclinometer4x4.ui.theme.InstrumentBackground
import com.felipeg.inclinometer4x4.ui.theme.InstrumentMutedText
import com.felipeg.inclinometer4x4.ui.theme.InstrumentSurface
import com.felipeg.inclinometer4x4.ui.theme.InstrumentText

@Composable
fun DashboardScreen(
    viewModel: SensorViewModel = hiltViewModel(),
    onScreenChange: (Screen) -> Unit,
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
    LaunchedEffect(deviceRotation) {
        viewModel.onRotationChanged(deviceRotation)
    }

    DashboardContent(
        uiState = uiState,
        onCalibrate = viewModel::calibrateZero,
        onResetCalibration = viewModel::resetCalibration,
        onToggleScreen = viewModel::toggleScreenOrientation,
        onOpenDiagnostics = { onScreenChange(Screen.Diagnostics) },
        onOpenAbout = { onScreenChange(Screen.About) },
    )
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onCalibrate: () -> Unit,
    onResetCalibration: () -> Unit,
    onToggleScreen: () -> Unit,
    onOpenDiagnostics: () -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(InstrumentBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(8.dp),
    ) {
        val isLandscape = maxWidth > maxHeight
        val isCalibrated = uiState.diagnostics.referenceRotation != null

        if (isLandscape) {
            LandscapeDashboard(
                uiState = uiState,
                isCalibrated = isCalibrated,
                onCalibrate = onCalibrate,
                onResetCalibration = onResetCalibration,
                onToggleScreen = onToggleScreen,
                onOpenDiagnostics = onOpenDiagnostics,
                onOpenAbout = onOpenAbout,
            )
        } else {
            PortraitDashboard(
                uiState = uiState,
                isCalibrated = isCalibrated,
                onCalibrate = onCalibrate,
                onResetCalibration = onResetCalibration,
                onToggleScreen = onToggleScreen,
                onOpenDiagnostics = onOpenDiagnostics,
                onOpenAbout = onOpenAbout,
            )
        }
    }
}

@Composable
private fun LandscapeDashboard(
    uiState: DashboardUiState,
    isCalibrated: Boolean,
    onCalibrate: () -> Unit,
    onResetCalibration: () -> Unit,
    onToggleScreen: () -> Unit,
    onOpenDiagnostics: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(3.4f)
                .fillMaxHeight(),
        ) {
            DashboardHeader(
                isCalibrated = isCalibrated,
                onCalibrate = onCalibrate,
                onResetCalibration = onResetCalibration,
                onToggleScreen = onToggleScreen,
                onOpenDiagnostics = onOpenDiagnostics,
                onOpenAbout = onOpenAbout,
            )
            OffroadInclinometer(
                roll = uiState.orientation.roll,
                pitch = uiState.orientation.pitch,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            AngleReadouts(
                orientation = uiState.orientation,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = onCalibrate,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = InstrumentSurface),
            ) {
                Text("ZERO", color = InstrumentText, fontWeight = FontWeight.Bold)
            }
            CompactGForce(
                x = uiState.gForce.x,
                y = uiState.gForce.y,
                max = uiState.maxGForce,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "±45° VISUAL SCALE",
                color = InstrumentMutedText,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun PortraitDashboard(
    uiState: DashboardUiState,
    isCalibrated: Boolean,
    onCalibrate: () -> Unit,
    onResetCalibration: () -> Unit,
    onToggleScreen: () -> Unit,
    onOpenDiagnostics: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DashboardHeader(
            isCalibrated = isCalibrated,
            onCalibrate = onCalibrate,
            onResetCalibration = onResetCalibration,
            onToggleScreen = onToggleScreen,
            onOpenDiagnostics = onOpenDiagnostics,
            onOpenAbout = onOpenAbout,
        )
        OffroadInclinometer(
            roll = uiState.orientation.roll,
            pitch = uiState.orientation.pitch,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        AngleReadouts(
            orientation = uiState.orientation,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompactGForce(
                x = uiState.gForce.x,
                y = uiState.gForce.y,
                max = uiState.maxGForce,
                modifier = Modifier.weight(1f),
            )
            Button(
                onClick = onCalibrate,
                colors = ButtonDefaults.buttonColors(containerColor = InstrumentSurface),
            ) {
                Text("ZERO", color = InstrumentText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    isCalibrated: Boolean,
    onCalibrate: () -> Unit,
    onResetCalibration: () -> Unit,
    onToggleScreen: () -> Unit,
    onOpenDiagnostics: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "INCLINOMETER",
                color = InstrumentText,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                letterSpacing = 2.sp,
            )
            CalibrationIndicator(isCalibrated = isCalibrated)
        }
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = InstrumentMutedText,
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(InstrumentSurface),
            ) {
                DashboardMenuItem("CALIBRATE ZERO") {
                    showMenu = false
                    onCalibrate()
                }
                DashboardMenuItem("RESET CALIBRATION") {
                    showMenu = false
                    onResetCalibration()
                }
                DashboardMenuItem("SENSOR DIAGNOSTICS") {
                    showMenu = false
                    onOpenDiagnostics()
                }
                DashboardMenuItem("TOGGLE SCREEN") {
                    showMenu = false
                    onToggleScreen()
                }
                DashboardMenuItem("ABOUT") {
                    showMenu = false
                    onOpenAbout()
                }
            }
        }
    }
}

@Composable
private fun DashboardMenuItem(label: String, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Text(
                text = label,
                color = InstrumentText,
                style = MaterialTheme.typography.labelLarge,
            )
        },
        onClick = onClick,
    )
}

@Composable
private fun AngleReadouts(orientation: Orientation, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AngleReadout(
            label = "ROLL",
            angle = orientation.roll,
            accent = angleZoneColor(orientation.roll),
            modifier = Modifier.weight(1f),
        )
        AngleReadout(
            label = "PITCH",
            angle = orientation.pitch,
            accent = angleZoneColor(orientation.pitch),
            modifier = Modifier.weight(1f),
        )
    }
}

private val PreviewState = DashboardUiState(
    orientation = Orientation(pitch = 7.8f, roll = -12.4f),
    gForce = GForce(x = 0.12f, y = -0.08f),
    maxGForce = 0.62f,
    diagnostics = SensorDiagnosticsUiState(referenceRotation = Quaternion.Identity),
)

@Preview(name = "Dashboard landscape", widthDp = 800, heightDp = 360, showBackground = true)
@Composable
private fun DashboardLandscapePreview() {
    Inclinometer4x4Theme {
        DashboardContent(
            uiState = PreviewState,
            onCalibrate = {},
            onResetCalibration = {},
            onToggleScreen = {},
            onOpenDiagnostics = {},
            onOpenAbout = {},
        )
    }
}

@Preview(name = "Dashboard portrait", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun DashboardPortraitPreview() {
    Inclinometer4x4Theme {
        DashboardContent(
            uiState = PreviewState,
            onCalibrate = {},
            onResetCalibration = {},
            onToggleScreen = {},
            onOpenDiagnostics = {},
            onOpenAbout = {},
        )
    }
}
