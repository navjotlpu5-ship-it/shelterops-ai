package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EvacuationRoute
import com.example.model.RouteStatus
import com.example.ui.theme.*
import com.example.viewmodel.ShelterOpsUiState

@Composable
fun EvacuationRoutesScreen(
    uiState: ShelterOpsUiState,
    modifier: Modifier = Modifier
) {
    val incident = uiState.selectedDisaster
    val routes = uiState.allocationPlan?.allocations?.map { it.route } ?: emptyList()
    val safeCount = routes.count { it.status == RouteStatus.CLEAR_SAFE }
    val blockedCount = routes.count { it.status == RouteStatus.BLOCKED }
    val cautionCount = routes.count { it.status == RouteStatus.ELEVATED_RISK }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommandDarkBg)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Route Network Status Summary
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CommandSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "EVACUATION CORRIDOR NETWORK",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Active Sector: ${incident?.districtName ?: "Assam"}, ${incident?.stateName ?: "India"}",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SafeGreenDim)
                                .border(1.dp, SafeGreen, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = "CONVOY ESCORTS ACTIVE", color = SafeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RouteStatusSummaryCard("Safe Corridors", "$safeCount Open", SafeGreen, Modifier.weight(1f))
                        RouteStatusSummaryCard("Elevated Risk", "$cautionCount Caution", WarningOrange, Modifier.weight(1f))
                        RouteStatusSummaryCard("Blocked Corridors", "$blockedCount Impassable", CriticalRed, Modifier.weight(1f))
                    }
                }
            }
        }

        // Section: Active Evacuation Corridors
        item {
            Text(
                text = "PLANNED EVACUATION CORRIDORS & DETOURS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(routes) { route ->
            val statusColor = when (route.status) {
                RouteStatus.CLEAR_SAFE -> SafeGreen
                RouteStatus.ELEVATED_RISK -> WarningOrange
                RouteStatus.BLOCKED -> CriticalRed
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CommandSurface)
                    .border(1.dp, if (route.status == RouteStatus.BLOCKED) CriticalRed else BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(14.dp)
                    .testTag("route_card_${route.id}")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = route.status.label.uppercase(),
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${route.distanceKm} km • ~${route.travelTimeMins} mins",
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = route.roadName,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "From: ${route.fromLocation} → To: ${route.toShelterName}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    if (route.hazardNote != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (route.status == RouteStatus.BLOCKED) CriticalRedDim else WarningOrange.copy(alpha = 0.15f))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (route.status == RouteStatus.BLOCKED) CriticalRed else WarningOrange,
                                    modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = route.hazardNote,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Designated Transit Modes:", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        route.transitModes.forEach { mode ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CommandSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(text = mode, color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RouteStatusSummaryCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CommandSurfaceVariant)
            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = title, color = TextMuted, fontSize = 9.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
