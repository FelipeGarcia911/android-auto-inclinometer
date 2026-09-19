package com.felipeg.inclinometer4x4.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipeg.inclinometer4x4.ui.theme.InstrumentBorder
import com.felipeg.inclinometer4x4.ui.theme.InstrumentCaution
import com.felipeg.inclinometer4x4.ui.theme.InstrumentHigh
import com.felipeg.inclinometer4x4.ui.theme.InstrumentNormal
import com.felipeg.inclinometer4x4.ui.theme.InstrumentText
import java.util.Locale
import kotlin.math.abs

fun formatAngle(angle: Float): String =
    String.format(Locale.US, "%+.1f°", if (angle.isFinite()) angle else 0f)

@Composable
fun AngleReadout(
    label: String,
    angle: Float,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(InstrumentBorder.copy(alpha = 0.28f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = accent,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
            )
        }
        Text(
            text = formatAngle(angle),
            color = InstrumentText,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            maxLines = 1,
        )
    }
}

internal fun angleZoneColor(angle: Float): Color = when {
    abs(angle) >= 30f -> InstrumentHigh
    abs(angle) >= 15f -> InstrumentCaution
    else -> InstrumentNormal
}
