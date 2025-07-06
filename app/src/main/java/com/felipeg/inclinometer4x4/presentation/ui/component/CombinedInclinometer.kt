package com.felipeg.inclinometer4x4.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CombinedInclinometer(
    roll: Float,
    pitch: Float,
    modifier: Modifier = Modifier,
    size: Dp = 400.dp
) {
    val skyColor = Color(0xFF4A90E2)
    val groundColor = Color(0xFFB87333)
    val horizonAndLinesColor = Color.White
    val textMeasurer = rememberTextMeasurer()
    val rollIndicatorColor = Color.Black

    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val innerRadius = radius * 0.80f // Widen the border by making the inner circle smaller
        val center = this.center
        val pitchOffset = pitch * 4f // Sensitivity

        // --- Static Part ---
        // 1. Draw the black outer ring for the roll scale
        drawCircle(color = rollIndicatorColor, radius = radius)

        // 2. Draw the fixed roll scale on the sides, within the black ring
        drawRollScale(outerRadius = radius, innerRadius = innerRadius, textMeasurer = textMeasurer)

        // --- Rotating Part (Horizon) ---
        // 3. Clip the inner area to draw the horizon
        clipPath(Path().apply { addOval(Rect(center = center, radius = innerRadius)) }) {
            // 4. The horizon rotates with roll and translates with pitch
            rotate(degrees = roll, pivot = center) {
                translate(top = pitchOffset) {
                    // Sky and Ground
                    drawRect(
                        color = skyColor,
                        topLeft = Offset(center.x - size.toPx(), center.y - size.toPx()),
                        size = this.size * 2f
                    )
                    drawRect(
                        color = groundColor,
                        topLeft = Offset(center.x - size.toPx(), center.y),
                        size = this.size * 2f
                    )
                    // Pitch Ladder (moves with the horizon)
                    drawPitchLadder(
                        radius = innerRadius, // Use innerRadius for pitch ladder
                        color = horizonAndLinesColor,
                        textMeasurer = textMeasurer
                    )
                }
            }
        }

        // --- Fixed Overlay Part ---
        // 5. Fixed aircraft symbol in the center
        val aircraftWingWidth = 40.dp.toPx()
        // Center dot
        drawCircle(Color.Red, radius = 3.dp.toPx())
        // Wings
        drawLine(
            color = Color.Red,
            start = Offset(center.x - aircraftWingWidth, center.y),
            end = Offset(center.x - 5.dp.toPx(), center.y),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color.Red,
            start = Offset(center.x + 5.dp.toPx(), center.y),
            end = Offset(center.x + aircraftWingWidth, center.y),
            strokeWidth = 2.dp.toPx()
        )
    }
}

private fun DrawScope.drawRollScale(outerRadius: Float, innerRadius: Float, textMeasurer: TextMeasurer) {
    val center = this.center
    val angles = listOf(15, 30, 45)
    val textStyle = TextStyle(color = Color.White, fontSize = 12.sp)

    // Make lines longer and more visible
    val lineStartRadius = outerRadius
    val lineEndRadius = outerRadius - 5.dp.toPx() // Draw a line of a fixed length
    val textRadius = outerRadius - 15.dp.toPx()   // Position text inside the line

    // Draw 0 degree line and text on both sides
    val zeroText = textMeasurer.measure(AnnotatedString("0"), style = textStyle)
    val zeroLineY = center.y
    // Right
    drawLine(Color.White, Offset(center.x + lineStartRadius, zeroLineY), Offset(center.x + lineEndRadius, zeroLineY), 2.dp.toPx())
    drawText(zeroText, topLeft = Offset(center.x + textRadius - zeroText.size.width / 2, zeroLineY - zeroText.size.height / 2))
    // Left
    drawLine(Color.White, Offset(center.x - lineStartRadius, zeroLineY), Offset(center.x - lineEndRadius, zeroLineY), 2.dp.toPx())
    drawText(zeroText, topLeft = Offset(center.x - textRadius - zeroText.size.width / 2, zeroLineY - zeroText.size.height / 2))


    angles.forEach { angle ->
        val angleRad = Math.toRadians(angle.toDouble()).toFloat()
        val textLayout = textMeasurer.measure(AnnotatedString(angle.toString()), style = textStyle)

        // Draw marks for both positive (up) and negative (down) angles from the horizontal
        listOf(1, -1).forEach { sign ->
            val ySin = sin(angleRad) * sign
            val xCos = cos(angleRad)

            // --- Lines ---
            // Right Side Line
            val rStartX = center.x + lineStartRadius * xCos
            val rStartY = center.y - lineStartRadius * ySin
            val rEndX = center.x + lineEndRadius * xCos
            val rEndY = center.y - lineEndRadius * ySin
            drawLine(Color.White, Offset(rStartX, rStartY), Offset(rEndX, rEndY), 1.5.dp.toPx())

            // Left Side Line
            val lStartX = center.x - lineStartRadius * xCos
            val lStartY = rStartY
            val lEndX = center.x - lineEndRadius * xCos
            val lEndY = rEndY
            drawLine(Color.White, Offset(lStartX, lStartY), Offset(lEndX, lEndY), 1.5.dp.toPx())

            // --- Text ---
            // Right Side Text
            val rTextX = center.x + textRadius * xCos
            val rTextY = center.y - textRadius * ySin
            drawText(textLayout, topLeft = Offset(rTextX - textLayout.size.width / 2, rTextY - textLayout.size.height / 2))

            // Left Side Text
            val lTextX = center.x - textRadius * xCos
            val lTextY = rTextY
            drawText(textLayout, topLeft = Offset(lTextX - textLayout.size.width / 2, lTextY - textLayout.size.height / 2))
        }
    }
}


private fun DrawScope.drawPitchLadder(
    radius: Float,
    color: Color,
    textMeasurer: TextMeasurer
) {
    val sensitivity = 4f
    val center = this.center

    // Horizon line
    drawLine(
        color = color,
        start = Offset(center.x - radius * 0.8f, center.y), // Adjusted to inner radius
        end = Offset(center.x + radius * 0.8f, center.y),
        strokeWidth = 2.dp.toPx()
    )

    val pitchAngles = listOf(10, 20, 30, 45)
    val lineLengths = mapOf(
        10 to radius * 0.2f,
        20 to radius * 0.3f,
        30 to radius * 0.4f,
        45 to radius * 0.5f
    )
    val textStyle = TextStyle(color = color, fontSize = 12.sp)

    pitchAngles.forEach { angle ->
        val length = lineLengths[angle] ?: 0f
        val yPos = center.y - angle * sensitivity
        val yNeg = center.y + angle * sensitivity
        val textLayoutResult = textMeasurer.measure(AnnotatedString(angle.toString()), style = textStyle)
        val textHeight = textLayoutResult.size.height

        // Positive pitch lines and text
        drawLine(
            color = color,
            start = Offset(center.x - length, yPos),
            end = Offset(center.x + length, yPos),
            strokeWidth = 1.5.dp.toPx()
        )
        drawText(
            textLayoutResult,
            topLeft = Offset(center.x - length - textLayoutResult.size.width - 4.dp.toPx(), yPos - textHeight / 2)
        )
        drawText(
            textLayoutResult,
            topLeft = Offset(center.x + length + 4.dp.toPx(), yPos - textHeight / 2)
        )

        // Negative pitch lines and text
        val gap = 10.dp.toPx()
        drawLine(
            color = color,
            start = Offset(center.x - length, yNeg),
            end = Offset(center.x - gap, yNeg),
            strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
            color = color,
            start = Offset(center.x + gap, yNeg),
            end = Offset(center.x + length, yNeg),
            strokeWidth = 1.5.dp.toPx()
        )
        drawText(
            textLayoutResult,
            topLeft = Offset(center.x - length - textLayoutResult.size.width - 4.dp.toPx(), yNeg - textHeight / 2)
        )
        drawText(
            textLayoutResult,
            topLeft = Offset(center.x + length + 4.dp.toPx(), yNeg - textHeight / 2)
        )
    }
}
