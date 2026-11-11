package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun TopCommandBar(
    activeScenarioKey: String,
    onScenarioSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scenarios = listOf(
        Triple("ASSAM_FLOOD", "Assam Floods", "Brahmaputra"),
        Triple("ODISHA_CYCLONE", "Odisha Cyclone", "Bay of Bengal"),
        Triple("UK_EARTHQUAKE", "Uttarakhand M6.8", "Chamoli-Joshimath"),
        Triple("HP_LANDSLIDE", "HP Landslides", "Mandi-Kullu"),
        Triple("PUNJAB_FLOOD", "Punjab Dam Overflow", "Sutlej Basin")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CommandSurface)
            .border(width = 1.dp, color = BorderSubtle)
            .padding(vertical = 10.dp, horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCyan.copy(alpha = 0.2f))
                        .border(1.dp, CyberCyan, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "ShelterOps Logo",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SHELTEROPS",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = " AI",
                            color = CyberCyan,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Text(
                        text = "NDMA Emergency Prediction & Shelter Allocation",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Live status badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CriticalRedDim)
                    .border(1.dp, CriticalRed.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(CriticalRed)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "LIVE OPS • RED ALERT",
                        color = CriticalRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Scenario Simulation quick selector chips
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "SCENARIO:",
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scenarios) { (key, title, subtitle) ->
                    val isSelected = activeScenarioKey == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberCyan.copy(alpha = 0.25f) else CommandSurfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) CyberCyan else BorderSubtle,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onScenarioSelected(key) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("scenario_chip_$key")
                    ) {
                        Column {
                            Text(
                                text = title,
                                color = if (isSelected) CyberCyan else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            Text(
                                text = subtitle,
                                color = if (isSelected) HighTechBlue else TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
