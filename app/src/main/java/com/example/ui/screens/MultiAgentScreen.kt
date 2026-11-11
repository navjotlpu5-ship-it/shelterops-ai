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
import com.example.model.AgentRole
import com.example.model.AIAgentInfo
import com.example.ui.theme.*
import com.example.viewmodel.ShelterOpsUiState

@Composable
fun MultiAgentScreen(
    uiState: ShelterOpsUiState,
    modifier: Modifier = Modifier
) {
    val agents = uiState.agents
    val messages = uiState.agentMessages
    val actionPlan = uiState.actionPlan

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommandDarkBg)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Multi-Agent Consensus Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF162032))
                    .border(1.dp, CyberCyan, RoundedCornerShape(14.dp))
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
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AUTONOMOUS RESPONSE AGENTS",
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "6 Specialized AI Agents Active in Consensus",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SafeGreenDim)
                                .border(1.dp, SafeGreen, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = "CONSENSUS: 100%", color = SafeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "The Multi-Agent architecture coordinates continuous telemetry assessment, demographic risk categorization, constraint-bounded shelter dispatch, and medical supply allocation into one unified incident response plan.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section: Agent Team Roster Cards (6 Agents)
        item {
            Text(
                text = "SPECIALIZED RESPONSE AGENTS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(agents) { agent ->
            AgentCard(agent = agent)
        }

        // Section: Synthesized Coordinated Incident Action Plan (IAP)
        if (actionPlan != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CommandSurface)
                        .border(1.dp, BorderCyan, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                        .testTag("incident_action_plan_card")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COORDINATED EMERGENCY ACTION PLAN",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Text(text = actionPlan.generatedTime, color = TextMuted, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = actionPlan.incidentTitle,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Lead Operational Directives:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        actionPlan.leadDirectives.forEach { directive ->
                            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                Text("• ", color = CyberCyan, fontSize = 12.sp)
                                Text(directive, color = TextPrimary, fontSize = 11.sp, lineHeight = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        PlanDetailSection("Evacuation Strategy", actionPlan.evacuationStrategy)
                        Spacer(modifier = Modifier.height(6.dp))
                        PlanDetailSection("Medical Dispatch", actionPlan.medicalDispatch)
                        Spacer(modifier = Modifier.height(6.dp))
                        PlanDetailSection("Logistics & Supplies", actionPlan.logisticsSupport)
                    }
                }
            }
        }

        // Section: Real-Time Inter-Agent Communication Feed
        item {
            Column {
                Text(
                    text = "INTER-AGENT COORDINATION TELEMETRY BUS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                messages.forEach { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CommandSurfaceVariant)
                            .border(1.dp, if (msg.isUrgent) WarningOrange.copy(alpha = 0.5f) else BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = msg.senderRole.title.substringBefore(" Agent").uppercase(),
                                        color = CyberCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (msg.targetRole != null) {
                                        Text(" → ${msg.targetRole.title.substringBefore(" Agent").uppercase()}", color = TextMuted, fontSize = 10.sp)
                                    }
                                }
                                Text(text = msg.timestamp, color = TextMuted, fontSize = 9.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = msg.headline, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = msg.payload, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
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
private fun AgentCard(agent: AIAgentInfo) {
    val (icon, color) = when (agent.role) {
        AgentRole.DISASTER_PREDICTION -> Pair(Icons.Default.Radar, CriticalRed)
        AgentRole.RISK_ASSESSMENT -> Pair(Icons.Default.Analytics, WarningOrange)
        AgentRole.SHELTER_ALLOCATION -> Pair(Icons.Default.Bolt, CyberCyan)
        AgentRole.ROUTE_PLANNING -> Pair(Icons.Default.AltRoute, HighTechBlue)
        AgentRole.MEDICAL_RESPONSE -> Pair(Icons.Default.LocalHospital, AccentPurple)
        AgentRole.RESOURCE_MANAGEMENT -> Pair(Icons.Default.Inventory2, SafeGreen)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CommandSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("agent_card_${agent.role.name}")
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
                            .background(color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = agent.role.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = agent.role.shortDesc, color = TextMuted, fontSize = 10.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CommandSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "${agent.confidence}% Confidence", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Key Finding: ${agent.keyFinding}", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = agent.operationalOutput, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Monitored Parameters:", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                agent.parametersMonitored.forEach { param ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CommandSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = param, color = TextMuted, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanDetailSection(title: String, content: String) {
    Column {
        Text(text = title.uppercase(), color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = content, color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
    }
}
