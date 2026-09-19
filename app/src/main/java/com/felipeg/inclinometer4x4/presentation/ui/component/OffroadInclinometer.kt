package com.felipeg.inclinometer4x4.presentation.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipeg.inclinometer4x4.ui.theme.Inclinometer4x4Theme
import com.felipeg.inclinometer4x4.ui.theme.InstrumentBorder
import com.felipeg.inclinometer4x4.ui.theme.InstrumentCaution
import com.felipeg.inclinometer4x4.ui.theme.InstrumentGrid
import com.felipeg.inclinometer4x4.ui.theme.InstrumentGround
import com.felipeg.inclinometer4x4.ui.theme.InstrumentHigh
import com.felipeg.inclinometer4x4.ui.theme.InstrumentMutedText
import com.felipeg.inclinometer4x4.ui.theme.InstrumentNormal
import com.felipeg.inclinometer4x4.ui.theme.InstrumentSky
import com.felipeg.inclinometer4x4.ui.theme.InstrumentSurface
import com.felipeg.inclinometer4x4.ui.theme.InstrumentText
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val VISUAL_LIMIT_DEGREES = 45f

data class OffroadInclinometerColors(
    val background: Color = InstrumentSurface,
    val border: Color = InstrumentBorder,
    val grid: Color = InstrumentGrid,
    val text: Color = InstrumentText,
    val mutedText: Color = InstrumentMutedText,
    val normal: Color = InstrumentNormal,
    val caution: Color = InstrumentCaution,
    val high: Color = InstrumentHigh,
    val sky: Color = InstrumentSky,
    val ground: Color = InstrumentGround,
)

/**
 * Presentation-only 4x4 inclinometer. Roll and pitch are already calibrated and
 * filtered by the sensor pipeline; this component only maps them to pixels.
 */
@Composable
fun OffroadInclinometer(
    roll: Float,
    pitch: Float,
    modifier: Modifier = Modifier,
    colors: OffroadInclinometerColors = OffroadInclinometerColors(),
) {
    val graphicRoll by animateFloatAsState(
        targetValue = roll,
        animationSpec = tween(durationMillis = 80, easing = LinearEasing),
        label = "rollGraphic",
    )
    val graphicPitch by animateFloatAsState(
        targetValue = pitch,
        animationSpec = tween(durationMillis = 80, easing = LinearEasing),
        label = "pitchGraphic",
    )
    val textMeasurer = rememberTextMeasurer()
    val scaleTextStyle = TextStyle(
        color = colors.mutedText,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
    )

    Canvas(modifier = modifier) {
        val inset = 4.dp.toPx()
        drawRoundRect(
            color = colors.background,
            topLeft = Offset(inset, inset),
            size = Size(size.width - inset * 2, size.height - inset * 2),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
        )
        drawRoundRect(
            color = colors.border,
            topLeft = Offset(inset, inset),
            size = Size(size.width - inset * 2, size.height - inset * 2),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
            style = Stroke(1.dp.toPx()),
        )

        val center = Offset(size.width * 0.46f, size.height * 0.72f)
        val radius = min(size.width * 0.37f, size.height * 0.62f)
        drawZoneArc(center, radius, -45f, -30f, colors.high)
        drawZoneArc(center, radius, -30f, -15f, colors.caution)
        drawZoneArc(center, radius, -15f, 15f, colors.normal)
        drawZoneArc(center, radius, 15f, 30f, colors.caution)
        drawZoneArc(center, radius, 30f, 45f, colors.high)

        for (angle in -40..40 step 10) {
            val major = angle % 20 == 0 || angle == 0
            val outer = polarPoint(center, radius - 2.dp.toPx(), angle.toFloat())
            val inner = polarPoint(
                center,
                radius - if (major) 15.dp.toPx() else 10.dp.toPx(),
                angle.toFloat(),
            )
            drawLine(
                color = if (angle == 0) colors.text else colors.grid,
                start = outer,
                end = inner,
                strokeWidth = if (major) 2.dp.toPx() else 1.dp.toPx(),
                cap = StrokeCap.Round,
            )
            val label = if (angle > 0) "+$angle" else angle.toString()
            val measured = textMeasurer.measure(label, scaleTextStyle)
            val labelPoint = polarPoint(center, radius - 27.dp.toPx(), angle.toFloat())
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(
                    labelPoint.x - measured.size.width / 2f,
                    labelPoint.y - measured.size.height / 2f,
                ),
            )
        }

        val rollOnScale = graphicRoll.coerceIn(-VISUAL_LIMIT_DEGREES, VISUAL_LIMIT_DEGREES)
        val rollPointer = polarPoint(center, radius + 1.dp.toPx(), rollOnScale)
        drawCircle(
            color = angleZoneColor(graphicRoll),
            radius = 5.dp.toPx(),
            center = rollPointer,
        )

        val viewport = Rect(
            left = size.width * 0.10f,
            top = size.height * 0.28f,
            right = size.width * 0.82f,
            bottom = size.height * 0.88f,
        )
        val pitchOnScale = graphicPitch.coerceIn(-VISUAL_LIMIT_DEGREES, VISUAL_LIMIT_DEGREES)
        val pitchOffset = pitchOnScale / VISUAL_LIMIT_DEGREES * viewport.height * 0.34f
        val horizonY = viewport.center.y + pitchOffset
        clipRect(viewport.left, viewport.top, viewport.right, viewport.bottom) {
            drawRect(
                color = colors.sky,
                topLeft = viewport.topLeft,
                size = viewport.size,
            )
            drawRect(
                color = colors.ground,
                topLeft = Offset(viewport.left, horizonY),
                size = Size(viewport.width, viewport.bottom - horizonY),
            )
            drawLine(
                color = colors.text.copy(alpha = 0.7f),
                start = Offset(viewport.left, horizonY),
                end = Offset(viewport.right, horizonY),
                strokeWidth = 2.dp.toPx(),
            )
            for (mark in -40..40 step 10) {
                if (mark == 0) continue
                val y = horizonY - mark / VISUAL_LIMIT_DEGREES * viewport.height * 0.34f
                val halfWidth = if (mark % 20 == 0) viewport.width * 0.10f else viewport.width * 0.065f
                drawLine(
                    color = colors.grid,
                    start = Offset(viewport.center.x - halfWidth, y),
                    end = Offset(viewport.center.x + halfWidth, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }
        }

        drawLine(
            color = colors.grid,
            start = Offset(viewport.left, viewport.center.y),
            end = Offset(viewport.right, viewport.center.y),
            strokeWidth = 1.dp.toPx(),
        )
        drawVehicle(
            center = Offset(viewport.center.x, viewport.center.y),
            width = min(viewport.width * 0.32f, size.height * 0.25f),
            roll = graphicRoll,
            color = colors.text,
            accent = angleZoneColor(graphicRoll),
        )

        val pitchRailX = size.width * 0.90f
        val pitchTop = size.height * 0.28f
        val pitchBottom = size.height * 0.86f
        fun pitchY(degrees: Float): Float = (pitchTop + pitchBottom) / 2f -
            degrees / VISUAL_LIMIT_DEGREES * (pitchBottom - pitchTop) / 2f
        drawLine(
            color = colors.grid,
            start = Offset(pitchRailX, pitchTop),
            end = Offset(pitchRailX, pitchBottom),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(colors.high, Offset(pitchRailX, pitchTop), Offset(pitchRailX, pitchY(30f)), 3.dp.toPx())
        drawLine(colors.caution, Offset(pitchRailX, pitchY(30f)), Offset(pitchRailX, pitchY(15f)), 3.dp.toPx())
        drawLine(colors.normal, Offset(pitchRailX, pitchY(15f)), Offset(pitchRailX, pitchY(-15f)), 3.dp.toPx())
        drawLine(colors.caution, Offset(pitchRailX, pitchY(-15f)), Offset(pitchRailX, pitchY(-30f)), 3.dp.toPx())
        drawLine(colors.high, Offset(pitchRailX, pitchY(-30f)), Offset(pitchRailX, pitchBottom), 3.dp.toPx())
        for (mark in -40..40 step 10) {
            val y = pitchY(mark.toFloat())
            val tick = if (mark % 20 == 0) 10.dp.toPx() else 6.dp.toPx()
            drawLine(
                color = if (mark == 0) colors.text else colors.grid,
                start = Offset(pitchRailX - tick, y),
                end = Offset(pitchRailX + tick, y),
                strokeWidth = if (mark == 0) 2.dp.toPx() else 1.dp.toPx(),
            )
            if (mark % 20 == 0) {
                val label = if (mark > 0) "+$mark" else mark.toString()
                val measured = textMeasurer.measure(label, scaleTextStyle)
                drawText(
                    measured,
                    topLeft = Offset(
                        pitchRailX + 12.dp.toPx(),
                        y - measured.size.height / 2f,
                    ),
                )
            }
        }
        val pitchPointerY = pitchY(pitchOnScale)
        val pitchPointer = Path().apply {
            moveTo(pitchRailX - 14.dp.toPx(), pitchPointerY)
            lineTo(pitchRailX - 4.dp.toPx(), pitchPointerY - 6.dp.toPx())
            lineTo(pitchRailX - 4.dp.toPx(), pitchPointerY + 6.dp.toPx())
            close()
        }
        drawPath(pitchPointer, angleZoneColor(graphicPitch))

        val up = textMeasurer.measure("UP", scaleTextStyle)
        drawText(up, topLeft = Offset(pitchRailX - up.size.width / 2f, pitchTop - 20.dp.toPx()))
        val down = textMeasurer.measure("DOWN", scaleTextStyle)
        drawText(down, topLeft = Offset(pitchRailX - down.size.width / 2f, pitchBottom + 5.dp.toPx()))
    }
}

private fun DrawScope.drawZoneArc(
    center: Offset,
    radius: Float,
    fromDegrees: Float,
    toDegrees: Float,
    color: Color,
) {
    drawArc(
        color = color.copy(alpha = 0.85f),
        startAngle = -90f + fromDegrees,
        sweepAngle = toDegrees - fromDegrees,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2f, radius * 2f),
        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Butt),
    )
}

private fun polarPoint(center: Offset, radius: Float, degrees: Float): Offset {
    val radians = degrees / 180f * PI.toFloat()
    return Offset(
        x = center.x + sin(radians) * radius,
        y = center.y - cos(radians) * radius,
    )
}

private fun DrawScope.drawVehicle(
    center: Offset,
    width: Float,
    roll: Float,
    color: Color,
    accent: Color,
) {
    val bodyHeight = width * 0.30f
    rotate(degrees = roll, pivot = center) {
        val body = Path().apply {
            moveTo(center.x - width * 0.48f, center.y + bodyHeight * 0.35f)
            lineTo(center.x - width * 0.34f, center.y - bodyHeight * 0.22f)
            lineTo(center.x - width * 0.20f, center.y - bodyHeight * 0.58f)
            lineTo(center.x + width * 0.20f, center.y - bodyHeight * 0.58f)
            lineTo(center.x + width * 0.34f, center.y - bodyHeight * 0.22f)
            lineTo(center.x + width * 0.48f, center.y + bodyHeight * 0.35f)
            close()
        }
        drawPath(body, color = color, style = Stroke(2.dp.toPx()))
        drawLine(
            color = accent,
            start = Offset(center.x - width * 0.43f, center.y + bodyHeight * 0.15f),
            end = Offset(center.x + width * 0.43f, center.y + bodyHeight * 0.15f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(color, width * 0.06f, Offset(center.x - width * 0.34f, center.y + bodyHeight * 0.48f))
        drawCircle(color, width * 0.06f, Offset(center.x + width * 0.34f, center.y + bodyHeight * 0.48f))
    }
}

@Preview(name = "Off-road inclinometer", widthDp = 640, heightDp = 320)
@Composable
private fun OffroadInclinometerPreview() {
    Inclinometer4x4Theme {
        OffroadInclinometer(
            roll = -18.4f,
            pitch = 9.7f,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
