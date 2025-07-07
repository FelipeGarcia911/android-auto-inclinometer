package com.felipeg.inclinometer4x4.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.sqrt

@Composable
fun GForceMeter(
    gForceX: Float,
    gForceY: Float,
    maxGForce: Float,
    modifier: Modifier = Modifier,
    size: Dp = 300.dp
) {
    val textMeasurer = rememberTextMeasurer()
    val backgroundColor = Color.Black
    val crosshairColor = Color.White
    val circleColor = Color(0xFF4A90E2)
    val indicatorColor = Color.Red

    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val center = this.center
        val dynamicMaxG = if (maxGForce > 2f) ceil(maxGForce) else 2f

        // Background
        drawCircle(backgroundColor, radius = radius)

        // Concentric circles and their labels
        drawConcentricCirclesWithLabels(radius, center, circleColor, textMeasurer, dynamicMaxG)

        // Crosshair
        drawCrosshair(radius, center, crosshairColor)

        // Direction labels
        drawDirectionLabels(radius, center, textMeasurer)

        // G-force indicator
        drawGForceIndicator(gForceX, gForceY, radius, center, indicatorColor, dynamicMaxG)

        // G-force readout
        drawGForceReadout(gForceX, gForceY, maxGForce, center, textMeasurer)
    }
}

private fun DrawScope.drawConcentricCirclesWithLabels(
    radius: Float,
    center: Offset,
    color: Color,
    textMeasurer: TextMeasurer,
    maxG: Float
) {
    val labelStyle = TextStyle(color = Color.White, fontSize = 10.sp)
    val numberOfCircles = 4

    for (i in 1..numberOfCircles) {
        val gValue = maxG / numberOfCircles * i
        val circleRadius = radius * (gValue / maxG)
        drawCircle(
            color,
            radius = circleRadius.toFloat(),
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
        )
        val text = "%.1f".format(gValue)
        val textLayout = textMeasurer.measure(AnnotatedString(text), style = labelStyle)
        drawText(
            textLayout,
            topLeft = Offset(center.x + circleRadius.toFloat() + 4.dp.toPx(), center.y - textLayout.size.height / 2)
        )
        drawText(
            textLayout,
            topLeft = Offset(center.x - textLayout.size.height / 2 , center.y - circleRadius.toFloat() + 4.dp.toPx())
        )
    }
}

private fun DrawScope.drawCrosshair(radius: Float, center: Offset, color: Color) {
    // Horizontal line
    drawLine(color, start = Offset(center.x - radius, center.y), end = Offset(center.x + radius, center.y), strokeWidth = 1.dp.toPx())
    // Vertical line
    drawLine(color, start = Offset(center.x, center.y - radius), end = Offset(center.x, center.y + radius), strokeWidth = 1.dp.toPx())
}

private fun DrawScope.drawDirectionLabels(radius: Float, center: Offset, textMeasurer: TextMeasurer) {
    val labelStyle = TextStyle(color = Color.White, fontSize = 12.sp)
    val padding = 12.dp.toPx()
    val verticalPadding = 14.dp.toPx()

    // Acceleration
    val accelText = textMeasurer.measure(AnnotatedString("ACCEL"), style = labelStyle)
    drawText(accelText, topLeft = Offset(center.x - accelText.size.width / 2, padding))

    // Brake
    val brakeText = textMeasurer.measure(AnnotatedString("BRAKE"), style = labelStyle)
    drawText(brakeText, topLeft = Offset(center.x - brakeText.size.width / 2, size.height - padding - brakeText.size.height))

    // Left
    val leftText = textMeasurer.measure(AnnotatedString("LEFT"), style = labelStyle)
    drawText(leftText, topLeft = Offset(padding, center.y - leftText.size.height / 2 - verticalPadding))

    // Right
    val rightText = textMeasurer.measure(AnnotatedString("RIGHT"), style = labelStyle)
    drawText(rightText, topLeft = Offset(size.width - padding - rightText.size.width, center.y - rightText.size.height / 2 + verticalPadding))
}

private fun DrawScope.drawGForceIndicator(
    gForceX: Float,
    gForceY: Float,
    radius: Float,
    center: Offset,
    color: Color,
    maxG: Float
) {
    val sensitivity = radius / maxG

    // Clamp the G-force values to the max
    val clampedX = gForceX.coerceIn(-maxG, maxG)
    val clampedY = gForceY.coerceIn(-maxG, maxG)

    val indicatorX = center.x + clampedX * sensitivity
    val indicatorY = center.y - clampedY * sensitivity // Y is inverted

    drawCircle(color, radius = 10.dp.toPx(), center = Offset(indicatorX, indicatorY))
}

private fun DrawScope.drawGForceReadout(
    gForceX: Float,
    gForceY: Float,
    maxGForce: Float,
    center: Offset,
    textMeasurer: TextMeasurer
) {
    val currentG = sqrt(gForceX * gForceX + gForceY * gForceY)
    val readoutStyle = TextStyle(color = Color.White, fontSize = 14.sp)

    val currentGText = "Current: %.2f G".format(currentG)
    val maxGText = "Max: %.2f G".format(maxGForce)
    val yOffset = 10.dp.toPx()

    val currentGLayout = textMeasurer.measure(AnnotatedString(currentGText), style = readoutStyle)
    val maxGLayout = textMeasurer.measure(AnnotatedString(maxGText), style = readoutStyle)

    drawText(
        currentGLayout,
        topLeft = Offset(center.x - currentGLayout.size.width / 2, center.y - currentGLayout.size.height - yOffset)
    )
    drawText(
        maxGLayout,
        topLeft = Offset(center.x - maxGLayout.size.width / 2, center.y + yOffset)
    )
}
