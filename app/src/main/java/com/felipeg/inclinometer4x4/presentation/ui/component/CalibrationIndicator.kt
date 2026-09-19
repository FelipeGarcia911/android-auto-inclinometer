package com.felipeg.inclinometer4x4.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipeg.inclinometer4x4.ui.theme.InstrumentCaution
import com.felipeg.inclinometer4x4.ui.theme.InstrumentNormal

@Composable
fun CalibrationIndicator(
    isCalibrated: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = if (isCalibrated) InstrumentNormal else InstrumentCaution
    Text(
        text = if (isCalibrated) "CALIBRATED" else "NOT CALIBRATED",
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = modifier
            .background(color.copy(alpha = 0.14f), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 4.dp),
    )
}
