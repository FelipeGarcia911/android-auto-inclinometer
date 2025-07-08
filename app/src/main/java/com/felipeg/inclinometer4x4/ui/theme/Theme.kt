package com.felipeg.inclinometer4x4.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GRColorScheme = darkColorScheme(
    primary = GRRed,
    onPrimary = GRWhite,
    background = GRBlack,
    onBackground = GRWhite,
    surface = GRBlack,
    onSurface = GRWhite,
    secondary = GRMediumGray,
    onSecondary = GRBlack,
    tertiary = GRDarkGray,
    onTertiary = GRWhite,
    error = GRRed,
    onError = GRWhite
)

@Composable
fun Inclinometer4x4Theme(
    content: @Composable () -> Unit
) {
    val colorScheme = GRColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}