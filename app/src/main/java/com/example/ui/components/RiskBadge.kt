package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RiskLevel
import com.example.ui.theme.*

@Composable
fun RiskBadge(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val (bgColor, borderColor, textColor) = when (riskLevel) {
        RiskLevel.CRITICAL -> Triple(CriticalRedDim, CriticalRed, CriticalRed)
        RiskLevel.HIGH -> Triple(Color(0x33F97316), WarningOrange, WarningOrange)
        RiskLevel.MODERATE -> Triple(AlertAmberDim, AlertAmber, AlertAmber)
        RiskLevel.LOW -> Triple(SafeGreenDim, SafeGreen, SafeGreen)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = if (isCompact) 6.dp else 10.dp, vertical = if (isCompact) 2.dp else 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 5.dp else 7.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = if (isCompact) riskLevel.name else riskLevel.label,
                color = textColor,
                fontSize = if (isCompact) 10.sp else 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}
