package com.example.model

enum class RouteStatus(val label: String) {
    CLEAR_SAFE("Safe Corridor"),
    ELEVATED_RISK("Caution / Slow Transit"),
    BLOCKED("Blocked / Impassable")
}

data class EvacuationRoute(
    val id: String,
    val fromLocation: String,
    val toShelterName: String,
    val distanceKm: Double,
    val travelTimeMins: Int,
    val status: RouteStatus,
    val roadName: String,
    val transitModes: List<String>,
    val hazardNote: String? = null,
    val detourAvailable: Boolean = true
)

enum class OptimizationGoal(val title: String, val description: String) {
    MINIMIZE_DISTANCE("Fastest Evacuation", "Minimizes travel distance and transfer duration"),
    PRIORITIZE_VULNERABLE("Vulnerability First", "Ensures elderly, children & medical cases reach top-tier facilities"),
    BALANCED_LOAD("Balanced Distribution", "Distributes evacuees evenly to prevent shelter overcrowding")
}

data class ShelterAllocation(
    val shelter: Shelter,
    val allocatedCount: Int,
    val vulnerableCount: Int,
    val route: EvacuationRoute,
    val allocationScore: Float,
    val rationale: String,
    val fallbackShelterName: String? = null
)

data class AllocationPlan(
    val scenarioName: String,
    val totalDisplaced: Int,
    val totalAllocated: Int,
    val unallocatedCount: Int,
    val goal: OptimizationGoal,
    val allocations: List<ShelterAllocation>,
    val exploredStatesCount: Int,
    val solvingTimeMs: Long,
    val constraintSatisfactionRate: Float, // 0.0 - 1.0 (e.g. 1.0 = 100% hard constraints satisfied)
    val executionLog: List<String>,
    val generatedAt: String
)
