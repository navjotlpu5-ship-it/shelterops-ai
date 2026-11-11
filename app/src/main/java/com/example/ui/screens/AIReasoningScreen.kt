package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class AIApproachInfo(
    val title: String,
    val subtitle: String,
    val paradigm: String,
    val implementationInShelterOps: String,
    val mathematicalFoundation: String,
    val sampleCodeSnippet: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accentColor: Color
)

@Composable
fun AIReasoningScreen(
    modifier: Modifier = Modifier
) {
    val approaches = listOf(
        AIApproachInfo(
            title = "1. Logical Reasoning & Rule Systems",
            subtitle = "Zero-Violation Hard Constraints",
            paradigm = "First-Order Logic & Safety Invariants",
            implementationInShelterOps = "Enforces strict life-safety filters: Never allocate evacuees to a shelter inside a compromised hazard zone, and strictly prohibit exceeding designated structural bed capacity.",
            mathematicalFoundation = "∀s ∈ Shelters, Occupancy(s) + Allocated(s) ≤ Capacity(s) ∧ Status(s) ≠ HAZARD_COMPROMISED",
            sampleCodeSnippet = """
if (shelter.status == COMPROMISED || !shelter.isFloodSafe) {
    pruneCandidate("Hard Safety Invariant Violated")
}
            """.trimIndent(),
            icon = Icons.Default.Gavel,
            accentColor = CriticalRed
        ),
        AIApproachInfo(
            title = "2. Probabilistic Reasoning & ML",
            subtitle = "Hazard Likelihood & Horizon Forecasting",
            paradigm = "Bayesian Uncertainty & Multi-Variate Random Forest",
            implementationInShelterOps = "Estimates natural disaster probabilities across 24h, 48h, and 7-day horizons by fusing IMD precipitation radar, CWC river gauge surges, and USGS seismic moment sensors.",
            mathematicalFoundation = "P(Disaster | Sensors) = (P(Sensors | Disaster) · P(Prior)) / P(Sensors)",
            sampleCodeSnippet = """
val riskScore = (rainMm / 300.0) * 0.40 + 
                (riverGaugeExcess / 4.0) * 0.45 + 
                (soilMoisture / 100.0) * 0.15
            """.trimIndent(),
            icon = Icons.Default.QueryStats,
            accentColor = WarningOrange
        ),
        AIApproachInfo(
            title = "3. State-Space Search & Graph Pathfinding",
            subtitle = "Evacuation Corridors & Roadblock Detours",
            paradigm = "A* Heuristic Search & Terrain Tortuosity",
            implementationInShelterOps = "Explores network graphs between disaster zones and shelters, automatically identifying impassable road segments (e.g. submerged NH-37 or landslide on NH-21) and navigating detours.",
            mathematicalFoundation = "f(n) = g(n) + h(n), where h(n) = Haversine(n, Shelter) · TerrainMultiplier",
            sampleCodeSnippet = """
fun planRoute(incident, shelter): EvacuationRoute {
    val d = haversine(incident, shelter) * tortuosityFactor
    if (isRoadwayFlooded) flagDetour(status = BLOCKED)
}
            """.trimIndent(),
            icon = Icons.Default.AltRoute,
            accentColor = HighTechBlue
        ),
        AIApproachInfo(
            title = "4. Constraint Optimization (OR-Tools)",
            subtitle = "Smart Shelter Allocation Engine",
            paradigm = "Mixed-Integer Linear Programming / Multi-Objective Heuristics",
            implementationInShelterOps = "Minimizes a multi-objective cost function balancing travel distance, vulnerability delays, medical capabilities, and shelter load balance while satisfying all hard bounds.",
            mathematicalFoundation = "Min Cost = w1·Distance + w2·VulnerabilityDelay + w3·ResourceDeficit + w4·Imbalance",
            sampleCodeSnippet = """
val score = (medScore * 0.7f) + (foodDays * 1.5f) - 
            (distancePenalty * 0.6f) - roadblockPenalty
            """.trimIndent(),
            icon = Icons.Default.Bolt,
            accentColor = CyberCyan
        ),
        AIApproachInfo(
            title = "5. Multi-Agent Systems (MAS)",
            subtitle = "Coordinated Emergency Action Plan",
            paradigm = "Distributed Autonomous Agents with Inter-Agent Protocol",
            implementationInShelterOps = "6 specialized agents (Prediction, Risk Assessment, Shelter Allocation, Route Planning, Medical Response, Resource Management) communicate asynchronously to achieve unified consensus.",
            mathematicalFoundation = "Consensus = ⋂_{i=1}^6 AgentProposals(i) subject to ConflictResolutionRules",
            sampleCodeSnippet = """
PredictionAgent.broadcast(surgeAlert) ->
RiskAgent.demographics(vulnerableCount) ->
ShelterAgent.allocate() ->
MedicalAgent.reserveICU()
            """.trimIndent(),
            icon = Icons.Default.Groups,
            accentColor = AccentPurple
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommandDarkBg)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CommandSurface)
                    .border(1.dp, CyberCyan, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ADVANCED AI ARCHITECTURE & REASONING",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Multi-Paradigm Disaster Intelligence",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ShelterOps AI combines rule-based logical safety invariants, machine learning probabilistic predictions, A* graph search, constraint optimization algorithms, and multi-agent coordination. Below is a comprehensive architectural breakdown suitable for academic and university demonstrations.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(approaches) { approach ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CommandSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(14.dp)
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
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(approach.accentColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(approach.icon, contentDescription = null, tint = approach.accentColor, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = approach.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = approach.paradigm, color = approach.accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "ShelterOps Implementation:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = approach.implementationInShelterOps, color = TextPrimary, fontSize = 11.sp, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Formal Mathematical Representation:", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(CommandSurfaceVariant)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = approach.mathematicalFoundation,
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Algorithm Snippet:", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0A0F1A))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = approach.sampleCodeSnippet,
                            color = SafeGreen,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
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
