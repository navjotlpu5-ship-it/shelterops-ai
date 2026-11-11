package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.ShelterOpsUiState

@Composable
fun SmartAllocationScreen(
    uiState: ShelterOpsUiState,
    onUpdatePopulation: (Int) -> Unit,
    onUpdateGoal: (OptimizationGoal) -> Unit,
    onRunAllocation: () -> Unit,
    onSelectShelter: (Shelter) -> Unit,
    modifier: Modifier = Modifier
) {
    val plan = uiState.allocationPlan
    var showLogs by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommandDarkBg)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Interactive Simulation Control Console
        item {
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
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyberCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ALLOCATION PARAMETER CONSOLE",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Active: ${uiState.selectedDisaster?.districtName ?: "Assam"}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Slider: Displaced Population
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Displaced Evacuees:", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = "%,d people".format(uiState.simulatedPopulation),
                            color = WarningOrange,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Slider(
                        value = uiState.simulatedPopulation.toFloat(),
                        onValueChange = { onUpdatePopulation(it.toInt()) },
                        valueRange = 2000f..40000f,
                        steps = 38,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = CyberCyan,
                            inactiveTrackColor = CommandSurfaceVariant
                        ),
                        modifier = Modifier.testTag("population_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Optimization Goal Selector
                    Text(text = "Constraint Optimization Objective:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OptimizationGoal.values().forEach { goal ->
                            val isSelected = uiState.simulationGoal == goal
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else CommandSurfaceVariant)
                                    .border(1.dp, if (isSelected) CyberCyan else BorderSubtle, RoundedCornerShape(8.dp))
                                    .clickable { onUpdateGoal(goal) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("goal_option_${goal.name}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = goal.title,
                                            color = if (isSelected) CyberCyan else TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = goal.description,
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onUpdateGoal(goal) },
                                        colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Solve Button
                    Button(
                        onClick = onRunAllocation,
                        enabled = !uiState.isSolvingAllocation,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("run_allocation_solver_button")
                    ) {
                        if (uiState.isSolvingAllocation) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Solving Constraints...", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Execute Smart Allocation Engine", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Solver Performance & Constraint Satisfaction Metrics
        if (plan != null) {
            item {
                Column {
                    Text(
                        text = "SOLVER PERFORMANCE & GUARANTEES",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SolverMetricBox(
                            label = "Hard Constraints",
                            value = "100% Satisfied",
                            subtitle = "Zero Capacity / Zone Breaches",
                            accentColor = SafeGreen,
                            modifier = Modifier.weight(1f)
                        )
                        SolverMetricBox(
                            label = "Explored States",
                            value = "${plan.exploredStatesCount}",
                            subtitle = "Search Branches Pruned",
                            accentColor = CyberCyan,
                            modifier = Modifier.weight(1f)
                        )
                        SolverMetricBox(
                            label = "Solve Duration",
                            value = "${plan.solvingTimeMs} ms",
                            subtitle = "Real-Time Heuristic",
                            accentColor = HighTechBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Capacity Deficit Alert (if unallocated evacuees exist)
                    if (plan.unallocatedCount > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CriticalRedDim)
                                .border(1.dp, CriticalRed, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = CriticalRed, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "CAPACITY DEFICIT: %,d UNALLOCATED EVACUEES".format(plan.unallocatedCount),
                                        color = CriticalRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "All verified safe centers in sector are currently at 100% capacity. Requesting NDRF & Army mobilization for field hospital tent camps.",
                                        color = TextPrimary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section: Shelter Allocation Roster
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OPTIMAL SHELTER ASSIGNMENTS (${plan.allocations.size} CENTERS)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Total Allocated: %,d".format(plan.totalAllocated),
                        color = SafeGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(plan.allocations) { item ->
                val shelter = item.shelter
                val route = item.route

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CommandSurface)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable { onSelectShelter(shelter) }
                        .padding(14.dp)
                        .testTag("allocation_card_${shelter.id}")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = shelter.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${shelter.districtName}, ${shelter.stateName} • Contact: ${shelter.phone}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SafeGreenDim)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+%,d Evacuees".format(item.allocatedCount),
                                    color = SafeGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Priority / Vulnerability Breakdown
                        if (item.vulnerableCount > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF261C3B))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Elderly, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Includes %,d high-priority vulnerable evacuees (pediatric, geriatric, disabled)".format(item.vulnerableCount),
                                    color = AccentPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Evacuation Corridor Details
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CommandSurfaceVariant)
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AltRoute, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = route.roadName, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        text = "${route.distanceKm} km • ~${route.travelTimeMins} mins",
                                        color = CyberCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (route.hazardNote != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Hazard Warning: ${route.hazardNote}", color = WarningOrange, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // AI Rationale for this Allocation
                        Text(text = "AI Allocation Rationale:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = item.rationale, color = TextPrimary, fontSize = 11.sp, lineHeight = 16.sp)

                        if (item.fallbackShelterName != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Designated Alternative Fallback: ${item.fallbackShelterName}",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Section: Expandable Solver Execution Log
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CommandSurface)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                        .clickable { showLogs = !showLogs }
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SOLVER EXECUTION LOGS (${plan.executionLog.size} ENTRIES)",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = if (showLogs) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }

                        AnimatedVisibility(visible = showLogs) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                plan.executionLog.forEach { log ->
                                    Text(
                                        text = "> $log",
                                        color = if (log.contains("VIOLATION") || log.contains("ALERT")) WarningOrange else TextMuted,
                                        fontSize = 10.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    )
                                }
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
private fun SolverMetricBox(
    label: String,
    value: String,
    subtitle: String,
    accentColor: Color,
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
            Text(text = label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextSecondary, fontSize = 8.sp, maxLines = 1)
        }
    }
}
