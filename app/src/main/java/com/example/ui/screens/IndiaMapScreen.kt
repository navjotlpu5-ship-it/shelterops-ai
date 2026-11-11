package com.example.ui.screens

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
import com.example.model.*
import com.example.ui.components.IndiaCommandMap
import com.example.ui.components.RiskBadge
import com.example.ui.theme.*
import com.example.viewmodel.AppNavTab
import com.example.viewmodel.ShelterOpsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndiaMapScreen(
    uiState: ShelterOpsUiState,
    onSelectDisaster: (DisasterIncident) -> Unit,
    onSelectShelter: (Shelter) -> Unit,
    onSelectRegion: (IndiaRegion) -> Unit,
    onClearShelterSelection: () -> Unit,
    onNavigateTab: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterType by remember { mutableStateOf<DisasterType?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredDisasters = remember(uiState.disasters, filterType, searchQuery) {
        uiState.disasters.filter { d ->
            (filterType == null || d.type == filterType) &&
                    (searchQuery.isBlank() || d.title.contains(searchQuery, ignoreCase = true) ||
                            d.stateName.contains(searchQuery, ignoreCase = true) ||
                            d.districtName.contains(searchQuery, ignoreCase = true))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CommandDarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Filter Bar (Search + State Quick Select Chips + Disaster Type chips)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CommandSurface)
                    .border(1.dp, BorderSubtle)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search any state, district, or disaster...", color = TextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = CommandSurfaceVariant,
                        unfocusedContainerColor = CommandSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("map_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Disaster Type Filters
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterChip(
                            selected = filterType == null,
                            onClick = { filterType = null },
                            label = { Text("All Hazards (${uiState.disasters.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                selectedLabelColor = CyberCyan
                            )
                        )
                    }
                    items(DisasterType.values()) { type ->
                        val count = uiState.disasters.count { it.type == type }
                        FilterChip(
                            selected = filterType == type,
                            onClick = { filterType = if (filterType == type) null else type },
                            label = { Text("${type.displayName} ($count)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                selectedLabelColor = CyberCyan
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // States / UTs Quick Nav Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(uiState.regions) { region ->
                        val isSelected = uiState.selectedRegion?.id == region.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) CyberCyan.copy(alpha = 0.25f) else CommandSurfaceVariant)
                                .border(1.dp, if (isSelected) CyberCyan else BorderSubtle, RoundedCornerShape(6.dp))
                                .clickable { onSelectRegion(region) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (region.riskLevel) {
                                                RiskLevel.CRITICAL -> CriticalRed
                                                RiskLevel.HIGH -> WarningOrange
                                                RiskLevel.MODERATE -> AlertAmber
                                                RiskLevel.LOW -> SafeGreen
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = region.name,
                                    color = if (isSelected) CyberCyan else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Central Map Area
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                IndiaCommandMap(
                    regions = uiState.regions,
                    disasters = filteredDisasters,
                    shelters = uiState.shelters,
                    selectedDisaster = uiState.selectedDisaster,
                    selectedShelter = uiState.selectedShelter,
                    routes = uiState.allocationPlan?.allocations?.map { it.route } ?: emptyList(),
                    onDisasterSelected = onSelectDisaster,
                    onShelterSelected = onSelectShelter,
                    onRegionSelected = onSelectRegion,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Floating Notice Banner
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(CommandSurface.copy(alpha = 0.90f))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Pan and pinch to zoom • Tap pins for telemetry details",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Bottom Inspector Sheet if Disaster or Shelter is tapped
            val activeDisaster = uiState.selectedDisaster
            val activeShelter = uiState.selectedShelter

            if (activeShelter != null) {
                // Shelter Detail Bottom Card
                ShelterInspectorCard(
                    shelter = activeShelter,
                    onClose = onClearShelterSelection,
                    onAllocateClick = { onNavigateTab(AppNavTab.SMART_ALLOCATION) }
                )
            } else if (activeDisaster != null) {
                // Disaster Detail Bottom Card
                DisasterInspectorCard(
                    disaster = activeDisaster,
                    onAllocateClick = { onNavigateTab(AppNavTab.SMART_ALLOCATION) },
                    onPredictClick = { onNavigateTab(AppNavTab.AI_PREDICTIONS) }
                )
            }
        }
    }
}

@Composable
private fun ShelterInspectorCard(
    shelter: Shelter,
    onClose: () -> Unit,
    onAllocateClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(CommandSurface)
            .border(1.dp, SafeGreen.copy(alpha = 0.5f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(14.dp)
            .testTag("shelter_inspector_card")
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
                            .background(SafeGreenDim),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.HomeWork, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "EMERGENCY SHELTER PROFILE", color = SafeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = shelter.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Capacity & Availability Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CapacityStatBox("Available Beds", "${shelter.availableBeds} beds", SafeGreen, Modifier.weight(1f))
                CapacityStatBox("Total Capacity", "${shelter.totalCapacity}", TextSecondary, Modifier.weight(1f))
                CapacityStatBox("Occupancy", "${shelter.occupancyPercentage}%", if (shelter.occupancyPercentage > 85) WarningOrange else CyberCyan, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Medical & Supplies Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SupplyTag(icon = Icons.Default.LocalHospital, text = "ICU Beds: ${shelter.medical.icuBedsCount}")
                SupplyTag(icon = Icons.Default.MedicalServices, text = "O2: ${shelter.medical.oxygenCylindersCount}")
                SupplyTag(icon = Icons.Default.Fastfood, text = "Food: ${shelter.supplies.foodDaysRemaining}d")
                SupplyTag(icon = Icons.Default.WaterDrop, text = "Water: ${shelter.supplies.drinkingWaterLiters / 1000}kL")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Contact: ${shelter.contactPerson} (${shelter.phone})",
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onAllocateClick,
                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(40.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Allocate Population To This Center", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DisasterInspectorCard(
    disaster: DisasterIncident,
    onAllocateClick: () -> Unit,
    onPredictClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(CommandSurface)
            .border(1.dp, BorderCyan, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(14.dp)
            .testTag("disaster_inspector_card")
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
                            .background(CriticalRedDim),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = CriticalRed, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "${disaster.type.displayName.uppercase()} ALERT", color = CriticalRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = disaster.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
                RiskBadge(riskLevel = disaster.severity, isCompact = true)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Region: ${disaster.districtName}, ${disaster.stateName} • Probability: ${(disaster.probability * 100).toInt()}% • Affected: %,d people".format(disaster.affectedPopulation),
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAllocateClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("Run Shelter Engine", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onPredictClick,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("AI Risk Horizons", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CapacityStatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CommandSurfaceVariant)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = TextMuted, fontSize = 9.sp)
            Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SupplyTag(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CommandSurfaceVariant)
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Icon(icon, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = text, color = TextPrimary, fontSize = 10.sp)
    }
}
