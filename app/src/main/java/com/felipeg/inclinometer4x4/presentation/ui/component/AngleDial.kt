package com.felipeg.inclinometer4x4.presentation.ui.component

import android.graphics.Paint
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Global configuration for the angle dial: defines size, thickness, and scale parameters.
 */
object AngleDialConfig {
    /** Default radius of the dial */
    val defaultRadius: Dp = 80.dp
    /** Total Canvas size = radius * 2 */
    val defaultSize: Dp = defaultRadius * 2
    /** Stroke thickness factor relative to the radius */
    const val thicknessFactor: Float = 0.04f
    /** Text size factor relative to the radius */
    const val textSizeFactor: Float = 0.05f
    /** Length multiplier for major ticks */
    const val majorTickFactor: Float = 1.2f
    /** Length multiplier for minor ticks */
    const val minorTickFactor: Float = 0.6f
    /** Multiplier of stroke thickness for label offset */
    const val labelOffsetFactor: Float = 4f
    /** Multiplier of stroke thickness for needle length */
    const val needleLengthFactor: Float = 2f
}

/**
 * Generic composable for an analog angle dial with custom scale and ticks.
 *
 * @param angle Current angle in degrees (clamped between [minAngle, maxAngle]).
 * @param modifier Modifier for layout adjustments.
 * @param radius Radius of the dial.
 * @param minAngle Minimum angle of the scale.
 * @param maxAngle Maximum angle of the scale.
 * @param majorStep Interval for major ticks in degrees.
 * @param minorStep Interval for minor ticks in degrees.
 */
@Composable
fun AngleDial(
    angle: Float,
    modifier: Modifier = Modifier,
    radius: Dp = AngleDialConfig.defaultRadius,
    minAngle: Float = -45f,
    maxAngle: Float = 45f,
    majorStep: Float = 15f,
    minorStep: Float = 5f
) {
    val dialColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier.size(radius * 2)) {
        drawGauge(
            radius = radius.toPx(),
            center = center,
            minAngle = minAngle,
            maxAngle = maxAngle,
            angle = angle,
            tickColor = dialColor,
            majorStep = majorStep,
            minorStep = minorStep
        )
    }
}

private fun DrawScope.drawGauge(
    radius: Float,
    center: Offset,
    minAngle: Float,
    maxAngle: Float,
    angle: Float,
    tickColor: Color,
    majorStep: Float,
    minorStep: Float
) {
    val startAngleDeg = 180f + minAngle
    val sweepAngleDeg = maxAngle - minAngle
    val thickness = radius * AngleDialConfig.thicknessFactor

    // Draw background arc
    drawArc(
        color = tickColor.copy(alpha = 0.2f),
        startAngle = startAngleDeg,
        sweepAngle = sweepAngleDeg,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = Stroke(width = thickness)
    )

    // Prepare tick steps and text paint
    val majorStepInt = majorStep.toInt().coerceAtLeast(1)
    val minorStepInt = minorStep.toInt().coerceAtLeast(1)
    val textPaint = TextPaint().apply {
        color = tickColor.toArgb()
        textSize = (radius * AngleDialConfig.textSizeFactor).sp.toPx()
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    // Draw ticks
    for (tick in minAngle.toInt()..maxAngle.toInt() step minorStepInt) {
        val thetaRad = Math.toRadians((180 + tick).toDouble())
        val cosT = cos(thetaRad).toFloat()
        val sinT = sin(thetaRad).toFloat()

        val outer = Offset(center.x + cosT * radius, center.y + sinT * radius)
        val innerRadius = if (tick % majorStepInt == 0) radius - thickness * AngleDialConfig.majorTickFactor else radius - thickness
        val inner = Offset(center.x + cosT * innerRadius, center.y + sinT * innerRadius)

        // Draw tick line
        drawLine(
            color = tickColor,
            start = inner,
            end = outer,
            strokeWidth = if (tick % majorStepInt == 0) thickness * AngleDialConfig.majorTickFactor else thickness * AngleDialConfig.minorTickFactor,
            cap = StrokeCap.Round
        )

        // Draw label for major ticks
        if (tick % majorStepInt == 0) {
            val labelRadius = radius - thickness * AngleDialConfig.labelOffsetFactor
            val labelX = center.x + cosT * labelRadius
            val labelY = center.y + sinT * labelRadius + textPaint.textSize / 3
            drawContext.canvas.nativeCanvas.drawText(tick.toString(), labelX, labelY, textPaint)
        }
    }

    // Draw needle
    val clampedAngle = angle.coerceIn(minAngle, maxAngle)
    val thetaRad = Math.toRadians((180 + clampedAngle).toDouble())
    val cosN = cos(thetaRad).toFloat()
    val sinN = sin(thetaRad).toFloat()
    val needleLen = radius - thickness * AngleDialConfig.needleLengthFactor
    val needleEnd = Offset(center.x + cosN * needleLen, center.y + sinN * needleLen)
    drawLine(
        color = tickColor,
        start = center,
        end = needleEnd,
        strokeWidth = thickness,
        cap = StrokeCap.Round
    )
}
