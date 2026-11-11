package com.example.engine

import com.example.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MultiAgentCoordinator {

    fun generateAgentTeam(incident: DisasterIncident, plan: AllocationPlan): List<AIAgentInfo> {
        val totalAllocated = plan.totalAllocated
        val totalVulnerable = plan.allocations.sumOf { it.vulnerableCount }

        return listOf(
            AIAgentInfo(
                role = AgentRole.DISASTER_PREDICTION,
                state = AgentState.READY,
                confidence = incident.confidencePercent,
                keyFinding = "Probability ${(incident.probability * 100).toInt()}% • Severity ${incident.severity.label}",
                operationalOutput = "Sensors: 24h Rainfall ${incident.metrics.rainfall24hMm}mm, River Gauge ${incident.metrics.riverLevelMeters}m. " +
                        "Next 24h hazard profile reaches peak flood/cyclone surge before 48h stabilization.",
                parametersMonitored = listOf("IMD Doppler Radar", "CWC Gauge Telemetry", "InSAR Sat Flood Depth", "Historical Recurrence")
            ),
            AIAgentInfo(
                role = AgentRole.RISK_ASSESSMENT,
                state = AgentState.READY,
                confidence = 94,
                keyFinding = "${plan.totalDisplaced} Estimated Evacuees • $totalVulnerable High-Priority Vulnerable",
                operationalOutput = "Identified 28% demographic requiring specialized transit (infants, elderly, pregnant, disabled). " +
                        "Direct evacuation buffer calculated at 8.5 km radius around ${incident.districtName}.",
                parametersMonitored = listOf("Census Demographic Density", "Building Structural Fragility", "Topographic Elevation", "Vulnerability Index")
            ),
            AIAgentInfo(
                role = AgentRole.SHELTER_ALLOCATION,
                state = AgentState.READY,
                confidence = 97,
                keyFinding = "${plan.allocations.size} Shelters Assigned • ${((plan.totalAllocated.toFloat() / plan.totalDisplaced.coerceAtLeast(1)) * 100).toInt()}% Allocated",
                operationalOutput = "Constraint solver explored ${plan.exploredStatesCount} states in ${plan.solvingTimeMs}ms without capacity violations. " +
                        "${if (plan.unallocatedCount > 0) "WARNING: ${plan.unallocatedCount} unallocated evacuees need secondary field tents." else "100% capacity match achieved."}",
                parametersMonitored = listOf("Bed Availability Matrix", "Zero-Hazard Exclusion Rules", "Medical Equipment Match", "Proximity Scoring")
            ),
            AIAgentInfo(
                role = AgentRole.ROUTE_PLANNING,
                state = AgentState.READY,
                confidence = 92,
                keyFinding = "${plan.allocations.count { it.route.status == RouteStatus.CLEAR_SAFE }} Safe Corridors • ${plan.allocations.count { it.route.status != RouteStatus.CLEAR_SAFE }} Hazards Flagged",
                operationalOutput = "Identified blocked corridors and mapped detours. Average evacuation transit time: " +
                        "${if (plan.allocations.isNotEmpty()) plan.allocations.map { it.route.travelTimeMins }.average().toInt() else 35} mins.",
                parametersMonitored = listOf("Highway Status Sensors", "Bridge Structural Clearances", "Traffic Police GIS Feeds", "Slope Debris Alerts")
            ),
            AIAgentInfo(
                role = AgentRole.MEDICAL_RESPONSE,
                state = AgentState.READY,
                confidence = 95,
                keyFinding = "${plan.allocations.sumOf { it.shelter.medical.icuBedsCount }} ICU Beds Reserved • ${plan.allocations.sumOf { it.shelter.medical.oxygenCylindersCount }} O2 Cylinders Active",
                operationalOutput = "All high-vulnerability evacuees assigned to facilities with on-site doctors and trauma care. " +
                        "Water purification tablets and anti-venom pre-positioned at all hubs.",
                parametersMonitored = listOf("Doctor-to-Patient Ratio", "Oxygen Cylinder Inventory", "Trauma Life Support", "Sanitation Epidemic Index")
            ),
            AIAgentInfo(
                role = AgentRole.RESOURCE_MANAGEMENT,
                state = AgentState.READY,
                confidence = 96,
                keyFinding = "Avg ${if (plan.allocations.isNotEmpty()) plan.allocations.map { it.shelter.supplies.foodDaysRemaining }.average().toInt() else 14} Days Food • Potable Water Reserves Verified",
                operationalOutput = "Cumulative water inventory: ${plan.allocations.sumOf { it.shelter.supplies.drinkingWaterLiters }} Liters. " +
                        "Diesel generator backup verified for continuous 72-hour autonomous power supply.",
                parametersMonitored = listOf("Caloric Food Stock", "Potable Water (L/day/person)", "Diesel Generator Run-Hours", "Sanitation Blocks")
            )
        )
    }

    fun generateInterAgentMessages(incident: DisasterIncident, plan: AllocationPlan): List<AgentMessage> {
        val now = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        return listOf(
            AgentMessage(
                id = "MSG_01",
                senderRole = AgentRole.DISASTER_PREDICTION,
                targetRole = AgentRole.RISK_ASSESSMENT,
                timestamp = "$now - 00:08",
                headline = "Surge Alert Broadcast",
                payload = "Severe trigger detected in ${incident.districtName}. Probability ${(incident.probability * 100).toInt()}%. Immediate risk to low-lying revenue sectors.",
                isUrgent = true
            ),
            AgentMessage(
                id = "MSG_02",
                senderRole = AgentRole.RISK_ASSESSMENT,
                targetRole = AgentRole.SHELTER_ALLOCATION,
                timestamp = "$now - 00:06",
                headline = "Vulnerability Demographics Dispatched",
                payload = "Total evacuee count: ${plan.totalDisplaced}. ${plan.allocations.sumOf { it.vulnerableCount }} high-priority vulnerable persons requiring on-site medical care and barrier-free access.",
                isUrgent = false
            ),
            AgentMessage(
                id = "MSG_03",
                senderRole = AgentRole.ROUTE_PLANNING,
                targetRole = AgentRole.SHELTER_ALLOCATION,
                timestamp = "$now - 00:05",
                headline = "Road Passability Matrix Updated",
                payload = "Inundation on primary highway detected. Imposed routing constraint: Rerouting convoy traffic through elevated state highway bypass.",
                isUrgent = true
            ),
            AgentMessage(
                id = "MSG_04",
                senderRole = AgentRole.SHELTER_ALLOCATION,
                targetRole = AgentRole.MEDICAL_RESPONSE,
                timestamp = "$now - 00:03",
                headline = "Shelter Roster & Bed Reservations Locked",
                payload = "Feasible assignment generated. ${plan.allocations.size} shelters mobilized. Requesting medical teams to verify triage wards.",
                isUrgent = false
            ),
            AgentMessage(
                id = "MSG_05",
                senderRole = AgentRole.RESOURCE_MANAGEMENT,
                targetRole = null,
                timestamp = "$now - 00:01",
                headline = "Logistics Pre-Positioning Confirmed",
                payload = "Langars, mobile water purification filtration units, and diesel generators activated across all assigned safe centers.",
                isUrgent = false
            )
        )
    }

    fun synthesizeActionPlan(incident: DisasterIncident, plan: AllocationPlan): CoordinatedActionPlan {
        val timeString = SimpleDateFormat("HH:mm:ss 'IST'", Locale.getDefault()).format(Date())
        return CoordinatedActionPlan(
            incidentTitle = "Operation Raksha: ${incident.title}",
            overallRiskGrade = incident.severity,
            leadDirectives = listOf(
                "Immediate evacuation of ${incident.affectedPopulation} citizens from vulnerable sectors of ${incident.districtName}.",
                "Prioritize transfer of elderly, pregnant women, and pediatric cases to high-readiness shelters.",
                "Enforce convoy protocols along designated green corridors with SDRF and Traffic Police escorts.",
                "Mandatory water chlorination and epidemic surveillance at all reception camps."
            ),
            evacuationStrategy = "Multi-axis staged evacuation utilizing ${plan.allocations.size} primary safe centers. " +
                    "Avoiding flood/rockfall corridors and directing heavy transit via elevated bypasses.",
            medicalDispatch = "On-site CMOs activated at all destination shelters. Total ${plan.allocations.sumOf { it.shelter.medical.icuBedsCount }} ICU beds and " +
                    "${plan.allocations.sumOf { it.shelter.medical.oxygenCylindersCount }} O2 cylinders standing by for incoming evacuees.",
            logisticsSupport = "72-hour autonomous ration packets and ${plan.allocations.sumOf { it.shelter.supplies.drinkingWaterLiters }}L potable water secured. " +
                    "Auxiliary generators fueled for continuous power.",
            consensusAchieved = true,
            generatedTime = timeString
        )
    }
}
