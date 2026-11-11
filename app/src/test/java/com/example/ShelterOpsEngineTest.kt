package com.example

import com.example.data.DisasterRepository
import com.example.engine.DisasterPredictionEngine
import com.example.engine.RoutePlanningEngine
import com.example.engine.ShelterAllocationEngine
import com.example.model.OptimizationGoal
import com.example.model.RiskLevel
import com.example.model.RouteStatus
import com.example.model.ShelterStatus
import org.junit.Assert.*
import org.junit.Test

class ShelterOpsEngineTest {

    @Test
    fun testDisasterPredictionEngineCalculatesProbabilities() {
        val disasters = DisasterRepository.getInitialDisasters()
        val floodIncident = disasters.first { it.id == "DIS_ASSAM_01" }

        val result = DisasterPredictionEngine.evaluateIncident(floodIncident)

        assertTrue("Probability score must be between 0.0 and 1.0", result.probabilityScore in 0.0f..1.0f)
        assertTrue("Confidence must be at least 80%", result.confidencePercent >= 80)
        assertEquals("Severe flood with river surge must evaluate to CRITICAL", RiskLevel.CRITICAL, result.overallRiskLevel)
        assertEquals("Must produce 3 forecast horizons", 3, result.horizons.size)
        assertTrue("Must provide explainable contributors", result.contributors.isNotEmpty())
    }

    @Test
    fun testShelterAllocationEngineSatisfiesHardConstraints() {
        val disasters = DisasterRepository.getInitialDisasters()
        val shelters = DisasterRepository.getInitialShelters()
        val assamDisaster = disasters.first { it.id == "DIS_ASSAM_01" }

        val plan = ShelterAllocationEngine.solveAllocation(
            incident = assamDisaster,
            allShelters = shelters,
            displacedCountOverride = 12000,
            goal = OptimizationGoal.PRIORITIZE_VULNERABLE
        )

        assertNotNull("Allocation plan must not be null", plan)
        assertTrue("Must allocate people", plan.totalAllocated > 0)
        assertTrue("Must explore multiple states", plan.exploredStatesCount > 0)

        // Hard constraint test 1: No shelter exceeds capacity
        for (item in plan.allocations) {
            assertTrue(
                "Shelter ${item.shelter.name} allocated (${item.allocatedCount}) must not exceed available capacity (${item.shelter.availableBeds})",
                item.allocatedCount <= item.shelter.availableBeds
            )
            // Hard constraint test 2: No shelter in hazard zone
            assertTrue("Allocated shelter must be flood safe", item.shelter.isFloodSafeZone)
            assertNotEquals("Allocated shelter must not be compromised", ShelterStatus.COMPROMISED, item.shelter.status)
        }
    }

    @Test
    fun testRoutePlanningDetectsBlockedHighways() {
        val disasters = DisasterRepository.getInitialDisasters()
        val shelters = DisasterRepository.getInitialShelters()
        val assamDisaster = disasters.first { it.id == "DIS_ASSAM_01" }
        val dhubriShelter = shelters.first { it.id == "SH_AS_04" }

        val route = RoutePlanningEngine.planRoute(assamDisaster, dhubriShelter)

        assertEquals("Old NH-37 trunk road must be detected as BLOCKED", RouteStatus.BLOCKED, route.status)
        assertNotNull("Must provide hazard explanation note", route.hazardNote)
    }
}
