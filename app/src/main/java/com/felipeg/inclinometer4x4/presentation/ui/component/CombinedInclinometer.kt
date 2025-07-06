package com.felipeg.inclinometer4x4.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
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
    val skyColor = Color(0xFF4A90E2)
    val groundColor = Color(0xFFB87333)
    val horizonAndLinesColor = Color.White

    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val center = this.center
        val pitchOffset = pitch * 4f // Sensitivity

        clipPath(Path().apply { addOval(Rect(center = center, radius = radius)) }) {
            // Apply pitch translation first, then roll rotation.
            translate(top = pitchOffset) {
                rotate(degrees = roll, pivot = center) {
                    // Sky - Extend beyond the visible radius to avoid gaps
                    drawRect(
                        color = skyColor,
                        topLeft = Offset(center.x - radius * 2, center.y - radius * 2),
                        size = this.size.copy(width = radius * 4, height = radius * 2)
                    )
                    // Ground - Extend beyond the visible radius
                    drawRect(
                        color = groundColor,
                        topLeft = Offset(center.x - radius * 2, center.y),
                        size = this.size.copy(width = radius * 4, height = radius * 2)
                    )

                    // Draw pitch lines (relative to the horizon)
                    drawPitchLadder(pitch = 0f, radius = radius, color = horizonAndLinesColor)
                }
            }
        }

        // Draw the fixed elements of the UI (dial, pointer) on top

        // Dial border
        drawCircle(color = dialColor, radius = radius, style = Stroke(width = 2.dp.toPx()))

        // Fixed pointer (representing the aircraft)
        val pointerSize = 10.dp.toPx()
        drawLine(
            color = Color.Red,
            start = Offset(center.x - pointerSize, center.y),
            end = Offset(center.x + pointerSize, center.y),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color.Red,
            start = Offset(center.x, center.y - pointerSize),
            end = Offset(center.x, center.y + pointerSize),
            strokeWidth = 2.dp.toPx()
        )
    }
}

// The original drawPitchLadder can be reused. It's now drawn inside the transformed scope.
// It doesn't need to handle pitch anymore, so we pass 0.
private fun DrawScope.drawPitchLadder(pitch: Float, radius: Float, color: Color) {
    val pitchOffset = pitch * 4 // This is 0 now.
    val lineLength = radius / 2f

    for (i in -45..45 step 15) {
        val y = center.y - i * 4 + pitchOffset
        drawLine(
            color = color,
            start = Offset(center.x - lineLength, y),
            end = Offset(center.x + lineLength, y),
            strokeWidth = 1.dp.toPx()
        )
    }
}
