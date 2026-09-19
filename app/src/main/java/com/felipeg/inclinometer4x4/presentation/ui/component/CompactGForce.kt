package com.felipeg.inclinometer4x4.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipeg.inclinometer4x4.ui.theme.InstrumentMutedText
import com.felipeg.inclinometer4x4.ui.theme.InstrumentSurfaceElevated
import com.felipeg.inclinometer4x4.ui.theme.InstrumentText
import java.util.Locale

@Composable
fun CompactGForce(
    x: Float,
    y: Float,
    max: Float,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = InstrumentSurfaceElevated),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "G-FORCE",
                color = InstrumentMutedText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                GValue("X", x)
                GValue("Y", y)
                GValue("MAX", max)
            }
        }
    }
}

@Composable
private fun GValue(label: String, value: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = InstrumentMutedText, style = MaterialTheme.typography.labelSmall)
        Text(
            text = String.format(Locale.US, "%+.2f", value),
            color = InstrumentText,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
