package com.felipeg.inclinometer4x4.presentation.model

import com.felipeg.common.domain.model.GForce
import com.felipeg.common.domain.model.Orientation

data class DashboardUiState(
    val orientation: Orientation = Orientation.Zero,
    val gForce: GForce = GForce.Zero,
    val maxGForce: Float = 0f,
    val screenOrientation: ScreenOrientation = ScreenOrientation.PORTRAIT
)

enum class ScreenOrientation {
    PORTRAIT,
    LANDSCAPE
}
