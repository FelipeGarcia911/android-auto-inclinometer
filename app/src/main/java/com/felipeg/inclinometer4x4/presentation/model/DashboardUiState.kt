package com.felipeg.inclinometer4x4.presentation.model

import com.felipeg.common.domain.model.GForce
import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.common.domain.model.Orientation
import com.felipeg.common.domain.model.Quaternion
import com.felipeg.common.domain.model.RotationSensorType

data class DashboardUiState(
    val orientation: Orientation = Orientation.Zero,
    val gForce: GForce = GForce.Zero,
    val maxGForce: Float = 0f,
    val screenOrientation: ScreenOrientation = ScreenOrientation.PORTRAIT,
    val diagnostics: SensorDiagnosticsUiState = SensorDiagnosticsUiState()
)

data class SensorDiagnosticsUiState(
    val sensorActive: Boolean = false,
    val sensorType: RotationSensorType? = null,
    val samplingRateHz: Float = 0f,
    val currentRotation: Quaternion? = null,
    val referenceRotation: Quaternion? = null,
    val rawOrientation: Orientation = Orientation.Zero,
    val calibratedOrientation: Orientation = Orientation.Zero,
    val filteredOrientation: Orientation = Orientation.Zero,
    val deviceRotation: DeviceRotation = DeviceRotation.ROTATION_0
)

enum class ScreenOrientation {
    PORTRAIT,
    LANDSCAPE
}
