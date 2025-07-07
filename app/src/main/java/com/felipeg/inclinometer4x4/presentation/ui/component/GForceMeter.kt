package com.felipeg.inclinometer4x4.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

@Composable
fun GForceMeter(
    gForceX: Float,
    gForceY: Float,
    modifier: Modifier = Modifier,
    size: Dp = 300.dp
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val center = this.center

        drawCrosshair(radius, center)
        drawLabels(radius, center, textMeasurer)
        drawGForceIndicator(gForceX, gForceY, radius, center)
    }
}

private fun DrawScope.drawCrosshair(radius: Float, center: Offset) {
    val crosshairColor = Color.LightGray
    // Horizontal line
    drawLine(crosshairColor, start = Offset(center.x - radius, center.y), end = Offset(center.x + radius, center.y))
    // Vertical line
    drawLine(crosshairColor, start = Offset(center.x, center.y - radius), end = Offset(center.x, center.y + radius))
}

private fun DrawScope.drawLabels(radius: Float, center: Offset, textMeasurer: TextMeasurer) {
    val labelStyle = TextStyle(color = Color.Gray, fontSize = 12.sp)
    val padding = 8.dp.toPx()

    // Acceleration
    val accelText = textMeasurer.measure(AnnotatedString("ACCEL"), style = labelStyle)
    drawText(accelText, topLeft = Offset(center.x - accelText.size.width / 2, center.y - radius - padding - accelText.size.height))

    // Brake
    val brakeText = textMeasurer.measure(AnnotatedString("BRAKE"), style = labelStyle)
    drawText(brakeText, topLeft = Offset(center.x - brakeText.size.width / 2, center.y + radius + padding))

    // Left
    val leftText = textMeasurer.measure(AnnotatedString("LEFT"), style = labelStyle)
    drawText(leftText, topLeft = Offset(center.x - radius - padding - leftText.size.width, center.y - leftText.size.height / 2))

    // Right
    val rightText = textMeasurer.measure(AnnotatedString("RIGHT"), style = labelStyle)
    drawText(rightText, topLeft = Offset(center.x + radius + padding, center.y - rightText.size.height / 2))
}

private fun DrawScope.drawGForceIndicator(gForceX: Float, gForceY: Float, radius: Float, center: Offset) {
    val indicatorColor = Color.Red
    val maxG = 1.5f // Max G-force to display
    val sensitivity = radius / maxG

    // Clamp the G-force values to the max
    val clampedX = gForceX.coerceIn(-maxG, maxG)
    val clampedY = gForceY.coerceIn(-maxG, maxG)

    val indicatorX = center.x + clampedX * sensitivity
    val indicatorY = center.y - clampedY * sensitivity // Y is inverted

    drawCircle(indicatorColor, radius = 8.dp.toPx(), center = Offset(indicatorX, indicatorY))
}
