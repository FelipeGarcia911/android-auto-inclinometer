package com.felipeg.inclinometer4x4.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CombinedInclinometer(
    roll: Float,
    pitch: Float,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    val dialColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val center = this.center

        // Draw background
        drawCircle(color = backgroundColor, radius = radius)
        drawCircle(color = dialColor, radius = radius, style = Stroke(width = 2.dp.toPx()))

        // Draw pitch lines
        drawPitchLadder(pitch, radius, dialColor)

        // Draw roll indicator
        rotate(degrees = roll) {
            // Horizon line
            drawLine(
                color = Color.White,
                start = Offset(center.x - radius, center.y),
                end = Offset(center.x + radius, center.y),
                strokeWidth = 2.dp.toPx()
            )
            // Pointer
            drawLine(
                color = Color.Red,
                start = Offset(center.x, center.y - 10.dp.toPx()),
                end = Offset(center.x, center.y + 10.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

private fun DrawScope.drawPitchLadder(pitch: Float, radius: Float, color: Color) {
    val pitchOffset = pitch * 4 // Sensitivity
    val lineLength = radius / 2f

    for (i in -45..45 step 15) {
        val y = center.y - i * 4 + pitchOffset
        if (y > center.y - radius && y < center.y + radius) {
            drawLine(
                color = color,
                start = Offset(center.x - lineLength, y),
                end = Offset(center.x + lineLength, y),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}
