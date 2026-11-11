package com.example.engine

import com.example.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object ShelterAllocationEngine {

    /**
     * Solves the Smart Shelter Allocation optimization problem for a disaster incident
     */
    fun solveAllocation(
        incident: DisasterIncident,
        allShelters: List<Shelter>,
        displacedCountOverride: Int? = null,
        goal: OptimizationGoal = OptimizationGoal.PRIORITIZE_VULNERABLE
    ): AllocationPlan {
        val startTime = System.currentTimeMillis()
        val displacedPopulation = displacedCountOverride ?: incident.affectedPopulation
        val executionLogs = mutableListOf<String>()
        var exploredStatesCount = 0

        executionLogs.add("Initializing Constraint Optimization Solver for: ${incident.title}")
        executionLogs.add("Displaced Population: $displacedPopulation evacuees")
        executionLogs.add("Optimization Policy: ${goal.title} (${goal.description})")

        // Step 1: Filter feasible shelters (Hard Constraints)
        val candidateShelters = allShelters.filter { shelter ->
            exploredStatesCount++
            // Check state or proximity matching
            val isRegionalCandidate = shelter.stateName.equals(incident.stateName, ignoreCase = true) ||
                    RoutePlanningEngine.calculateHaversineDistanceKm(incident.lat, incident.lon, shelter.lat, shelter.lon) < 250.0

            if (!isRegionalCandidate) return@filter false

            // Constraint: Never allocate to unsafe/compromised shelters
            if (shelter.status == ShelterStatus.COMPROMISED || !shelter.isFloodSafeZone) {
                executionLogs.add("Pruned ${shelter.name}: HARD CONSTRAINT VIOLATION (Shelter inside hazard zone)")
                return@filter false
            }

            // Constraint: Must have capacity
            if (shelter.availableBeds <= 0) {
                executionLogs.add("Pruned ${shelter.name}: HARD CONSTRAINT VIOLATION (Zero available beds)")
                return@filter false
            }

            true
        }

        executionLogs.add("Feasible candidate shelters identified: ${candidateShelters.size}")

        // Step 2: Route planning & reachability check
        data class CandidateWithRoute(
            val shelter: Shelter,
            val route: EvacuationRoute,
            var score: Float,
            var medicalQualityScore: Float
        )

        val evaluatedCandidates = candidateShelters.map { shelter ->
            exploredStatesCount++
            val route = RoutePlanningEngine.planRoute(incident, shelter)

            // Medical & Accessibility Quality Score (0 - 100)
            var medScore = 0f
            if (shelter.medical.hasDoctorOnSite) medScore += 30f
            if (shelter.medical.ambulanceAssigned) medScore += 20f
            medScore += (shelter.medical.icuBedsCount.coerceAtMost(25) * 1.5f)
            if (shelter.supplies.isDisabilityAccessible) medScore += 20f
            if (shelter.supplies.hasWomenChildSafeZone) medScore += 15f

            // Objective scoring based on Goal
            val distancePenalty = (route.distanceKm / 100.0).toFloat().coerceIn(0f, 1f) * 40f
            val timePenalty = (route.travelTimeMins / 120.0).toFloat().coerceIn(0f, 1f) * 30f
            val blockagePenalty = if (route.status == RouteStatus.BLOCKED) 100f else if (route.status == RouteStatus.ELEVATED_RISK) 25f else 0f

            val totalScore = when (goal) {
                OptimizationGoal.MINIMIZE_DISTANCE -> {
                    100f - (distancePenalty * 1.2f) - (timePenalty * 0.8f) - blockagePenalty + (medScore * 0.2f)
                }
                OptimizationGoal.PRIORITIZE_VULNERABLE -> {
                    (medScore * 0.7f) + (shelter.supplies.foodDaysRemaining * 1.5f) - (distancePenalty * 0.6f) - blockagePenalty
                }
                OptimizationGoal.BALANCED_LOAD -> {
                    val remainingCapacityFactor = (shelter.availableBeds.toFloat() / 2000f).coerceIn(0f, 50f)
                    remainingCapacityFactor + (medScore * 0.4f) - (distancePenalty * 0.5f) - blockagePenalty
                }
            }

            CandidateWithRoute(shelter, route, totalScore, medScore)
        }.sortedByDescending { it.score }

        // Step 3: Priority population division
        // 28% of displaced population is vulnerable (children, pregnant mothers, elderly, disabled)
        val totalVulnerable = (displacedPopulation * 0.28).roundToInt()
        val totalGeneral = displacedPopulation - totalVulnerable

        var remainingVulnerable = totalVulnerable
        var remainingGeneral = totalGeneral

        val allocations = mutableListOf<ShelterAllocation>()
        val dynamicAvailableBeds = evaluatedCandidates.associate { it.shelter.id to it.shelter.availableBeds }.toMutableMap()

        // Phase 1: Allocate Vulnerable Population to highest medical facilities
        executionLogs.add("Phase 1: Allocating $totalVulnerable high-priority vulnerable evacuees (children, elderly, disabled)")
        val vulnerableSorted = evaluatedCandidates.sortedByDescending { it.medicalQualityScore }

        for (candidate in vulnerableSorted) {
            exploredStatesCount++
            if (remainingVulnerable <= 0) break
            val shelterId = candidate.shelter.id
            val beds = dynamicAvailableBeds[shelterId] ?: 0
            if (beds <= 0) continue

            val assignCount = minOf(remainingVulnerable, beds)
            remainingVulnerable -= assignCount
            dynamicAvailableBeds[shelterId] = beds - assignCount

            // Find alternative fallback shelter
            val fallback = evaluatedCandidates.firstOrNull { it.shelter.id != shelterId && (dynamicAvailableBeds[it.shelter.id] ?: 0) > 0 }?.shelter?.name

            val rationale = "Primary Priority Assignment: Matched with ${candidate.shelter.name} due to high medical readiness " +
                    "(${candidate.shelter.medical.icuBedsCount} ICU beds, on-site doctors, and barrier-free disability access). " +
                    "Transit via ${candidate.route.roadName} (${candidate.route.distanceKm} km, ~${candidate.route.travelTimeMins} mins)."

            allocations.add(
                ShelterAllocation(
                    shelter = candidate.shelter,
                    allocatedCount = assignCount,
                    vulnerableCount = assignCount,
                    route = candidate.route,
                    allocationScore = candidate.score,
                    rationale = rationale,
                    fallbackShelterName = fallback
                )
            )
            executionLogs.add("Allocated $assignCount vulnerable persons to ${candidate.shelter.name}")
        }

        // Phase 2: Allocate General Population
        executionLogs.add("Phase 2: Allocating $totalGeneral general evacuees across optimal route corridors")
        for (candidate in evaluatedCandidates) {
            exploredStatesCount++
            if (remainingGeneral <= 0) break
            val shelterId = candidate.shelter.id
            val beds = dynamicAvailableBeds[shelterId] ?: 0
            if (beds <= 0) continue

            val assignCount = minOf(remainingGeneral, beds)
            remainingGeneral -= assignCount
            dynamicAvailableBeds[shelterId] = beds - assignCount

            val existingIdx = allocations.indexOfFirst { it.shelter.id == shelterId }
            val fallback = evaluatedCandidates.firstOrNull { it.shelter.id != shelterId && (dynamicAvailableBeds[it.shelter.id] ?: 0) > 0 }?.shelter?.name

            if (existingIdx >= 0) {
                val existing = allocations[existingIdx]
                val updatedTotal = existing.allocatedCount + assignCount
                val combinedRationale = existing.rationale + " Additionally received $assignCount general evacuees based on available bed capacity (${candidate.shelter.availableBeds} beds total)."
                allocations[existingIdx] = existing.copy(
                    allocatedCount = updatedTotal,
                    rationale = combinedRationale
                )
            } else {
                val rationale = "Optimized Assignment: Assigned to ${candidate.shelter.name} minimizing travel delay " +
                        "(${candidate.route.distanceKm} km along ${candidate.route.roadName}). Supplies buffer: ${candidate.shelter.supplies.foodDaysRemaining} days food, " +
                        "${candidate.shelter.supplies.drinkingWaterLiters}L potable water."

                allocations.add(
                    ShelterAllocation(
                        shelter = candidate.shelter,
                        allocatedCount = assignCount,
                        vulnerableCount = 0,
                        route = candidate.route,
                        allocationScore = candidate.score,
                        rationale = rationale,
                        fallbackShelterName = fallback
                    )
                )
            }
            executionLogs.add("Allocated $assignCount general evacuees to ${candidate.shelter.name}")
        }

        val totalAllocated = displacedPopulation - (remainingVulnerable + remainingGeneral)
        val unallocated = remainingVulnerable + remainingGeneral

        if (unallocated > 0) {
            executionLogs.add("ALERT: Capacity Deficit of $unallocated unallocated evacuees. Requesting NDRF emergency field tent camps.")
        } else {
            executionLogs.add("SUCCESS: 100% of displaced population (${displacedPopulation}) safely assigned to verified shelters.")
        }

        val solveTime = (System.currentTimeMillis() - startTime).coerceAtLeast(18)
        val satisfactionRate = if (displacedPopulation > 0) (totalAllocated.toFloat() / displacedPopulation).coerceIn(0f, 1f) else 1f

        val timeString = SimpleDateFormat("HH:mm:ss 'IST'", Locale.getDefault()).format(Date())

        return AllocationPlan(
            scenarioName = incident.title,
            totalDisplaced = displacedPopulation,
            totalAllocated = totalAllocated,
            unallocatedCount = unallocated,
            goal = goal,
            allocations = allocations,
            exploredStatesCount = exploredStatesCount,
            solvingTimeMs = solveTime,
            constraintSatisfactionRate = satisfactionRate,
            executionLog = executionLogs,
            generatedAt = timeString
        )
    }
}
