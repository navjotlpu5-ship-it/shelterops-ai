package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.*
import com.example.ui.components.CommandStatCard
import com.example.ui.components.IndiaCommandMap
import com.example.ui.components.RiskBadge
import com.example.ui.theme.*
import com.example.viewmodel.AppNavTab
import com.example.viewmodel.ShelterOpsUiState

@Composable
fun CommandOverviewScreen(
    uiState: ShelterOpsUiState,
    onNavigateTab: (AppNavTab) -> Unit,
    onSelectDisaster: (DisasterIncident) -> Unit,
    onSelectShelter: (Shelter) -> Unit,
    onSelectRegion: (IndiaRegion) -> Unit,
    onRunAllocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalAffected = uiState.disasters.sumOf { it.affectedPopulation }
    val totalBeds = uiState.shelters.sumOf { it.totalCapacity }
    val availableBeds = uiState.shelters.sumOf { it.availableBeds }
    val criticalCount = uiState.disasters.count { it.severity == RiskLevel.CRITICAL }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommandDarkBg)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Top Statistics Grid
        item {
            Text(
                text = "NATIONAL EMERGENCY SITUATION OVERVIEW",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 2x3 Grid of Stat Cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CommandStatCard(
                    title = "Predicted Events",
                    value = "${uiState.disasters.size}",
                    subtitle = "$criticalCount Critical Alerts",
                    icon = Icons.Default.Warning,
                    accentColor = CriticalRed,
                    modifier = Modifier.weight(1f)
                )
                CommandStatCard(
                    title = "At-Risk Population",
                    value = "%,d".format(totalAffected),
                    subtitle = "Across 9 Monitored Basins",
                    icon = Icons.Default.People,
                    accentColor = WarningOrange,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CommandStatCard(
                    title = "Emergency Shelters",
                    value = "${uiState.shelters.size}",
                    subtitle = "%,d Total Bed Capacity".format(totalBeds),
                    icon = Icons.Default.HomeWork,
                    accentColor = SafeGreen,
                    modifier = Modifier.weight(1f)
                )
                CommandStatCard(
                    title = "Available Beds",
                    value = "%,d".format(availableBeds),
                    subtitle = "${((availableBeds.toFloat() / totalBeds) * 100).toInt()}% Net Reserve",
                    icon = Icons.Default.Bed,
                    accentColor = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val plan = uiState.allocationPlan
                val allocated = plan?.totalAllocated ?: 0
                val percentAllocated = if (plan != null && plan.totalDisplaced > 0) ((allocated.toFloat() / plan.totalDisplaced) * 100).toInt() else 0
                CommandStatCard(
                    title = "People Allocated",
                    value = "%,d".format(allocated),
                    subtitle = "$percentAllocated% Assigned Safely",
                    icon = Icons.Default.AssignmentTurnedIn,
                    accentColor = HighTechBlue,
                    modifier = Modifier.weight(1f)
                )
                CommandStatCard(
                    title = "Medical & Supplies",
                    value = "READY",
                    subtitle = "72h Autonomy • All Centers",
                    icon = Icons.Default.LocalHospital,
                    accentColor = AccentPurple,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: Interactive Map Spotlight
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "INDIA INTERACTIVE DISASTER MAP",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
                TextButton(
                    onClick = { onNavigateTab(AppNavTab.INDIA_MAP) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Fullscreen Map →", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                IndiaCommandMap(
                    regions = uiState.regions,
                    disasters = uiState.disasters,
                    shelters = uiState.shelters,
                    selectedDisaster = uiState.selectedDisaster,
                    selectedShelter = uiState.selectedShelter,
                    routes = uiState.allocationPlan?.allocations?.map { it.route } ?: emptyList(),
                    onDisasterSelected = onSelectDisaster,
                    onShelterSelected = onSelectShelter,
                    onRegionSelected = onSelectRegion,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Section: Active Scenario Spotlight Card
        item {
            val incident = uiState.selectedDisaster
            if (incident != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CommandSurface)
                        .border(1.dp, BorderCyan, RoundedCornerShape(14.dp))
                        .padding(16.dp)
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
                                        .background(CriticalRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ACTIVE SCENARIO SPOTLIGHT",
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            RiskBadge(riskLevel = incident.severity, isCompact = true)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = incident.title,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Location: ${incident.districtName}, ${incident.stateName} • Reported: ${incident.reportedAt}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricChip(
                                label = "Estimated At-Risk",
                                value = "%,d people".format(incident.affectedPopulation),
                                modifier = Modifier.weight(1f)
                            )
                            MetricChip(
                                label = "AI Probability",
                                value = "${(incident.probability * 100).toInt()}% Confidence",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        // Live Weather Telemetry Summary
                        val m = incident.metrics
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CommandSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "LIVE IMD: ${m.tempCelsius}°C • ${m.humidityPercent}% RH • ${m.pressureHpa.toInt()} hPa • ${m.rainfall24hMm}mm Rain",
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Recommended Emergency Actions:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        incident.recommendedActions.take(2).forEach { action ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", color = CyberCyan, fontSize = 12.sp)
                                Text(action, color = TextPrimary, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onNavigateTab(AppNavTab.SMART_ALLOCATION) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("quick_allocate_button")
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Smart Allocation Engine", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { onNavigateTab(AppNavTab.AI_PREDICTIONS) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("quick_predictions_button")
                            ) {
                                Icon(Icons.Default.Insights, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Predictions & Horizons", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section: Live Monitored Incident Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INDIA DISASTER ALERT & INCIDENT FEED",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${uiState.disasters.size} active",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        items(uiState.disasters) { incident ->
            val isSelected = uiState.selectedDisaster?.id == incident.id
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) CommandSurfaceVariant else CommandSurface)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) CyberCyan else BorderSubtle,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectDisaster(incident) }
                    .padding(14.dp)
                    .testTag("disaster_item_${incident.id}")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = incident.type.displayName.uppercase(),
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp
                            )
                            Text(" • ${incident.stateName}", color = TextMuted, fontSize = 10.sp)
                        }
                        RiskBadge(riskLevel = incident.severity, isCompact = true)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = incident.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "District: ${incident.districtName}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "%,d at risk".format(incident.affectedPopulation),
                            color = WarningOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
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
private fun MetricChip(
    label: String,
    value: String,
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
            Text(text = label, color = TextMuted, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
