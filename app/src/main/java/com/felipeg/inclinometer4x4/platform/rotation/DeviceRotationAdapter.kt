package com.felipeg.inclinometer4x4.platform.rotation

import android.content.pm.ActivityInfo
import android.view.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.inclinometer4x4.presentation.model.ScreenOrientation

@Composable
fun currentDeviceRotation(): DeviceRotation = when (LocalView.current.display.rotation) {
    Surface.ROTATION_90 -> DeviceRotation.ROTATION_90
    Surface.ROTATION_180 -> DeviceRotation.ROTATION_180
    Surface.ROTATION_270 -> DeviceRotation.ROTATION_270
    else -> DeviceRotation.ROTATION_0
}

fun ScreenOrientation.toRequestedOrientation(): Int = when (this) {
    ScreenOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    ScreenOrientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}
