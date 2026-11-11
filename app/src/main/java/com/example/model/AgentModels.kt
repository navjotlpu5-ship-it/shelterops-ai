package com.example.model

enum class AgentRole(val title: String, val shortDesc: String) {
    DISASTER_PREDICTION("Disaster Prediction Agent", "Evaluates weather, river telemetry & disaster probability"),
    RISK_ASSESSMENT("Risk Assessment Agent", "Identifies affected populations & vulnerability demographics"),
    SHELTER_ALLOCATION("Shelter Allocation Agent", "Executes constraint optimization for safe shelter assignments"),
    ROUTE_PLANNING("Route Planning Agent", "Calculates evacuation paths & identifies blocked roads"),
    MEDICAL_RESPONSE("Medical Response Agent", "Assesses medical demands, ICU beds, oxygen & triage kits"),
    RESOURCE_MANAGEMENT("Resource Management Agent", "Tracks food rations, potable water & electrical backup")
}

enum class AgentState {
    STANDBY,
    ANALYZING,
    OPTIMIZING,
    READY
}

data class AgentMessage(
    val id: String,
    val senderRole: AgentRole,
    val targetRole: AgentRole? = null,
    val timestamp: String,
    val headline: String,
    val payload: String,
    val isUrgent: Boolean = false
)

data class AIAgentInfo(
    val role: AgentRole,
    val state: AgentState,
    val confidence: Int, // 0 - 100
    val keyFinding: String,
    val operationalOutput: String,
    val parametersMonitored: List<String>
)

data class CoordinatedActionPlan(
    val incidentTitle: String,
    val overallRiskGrade: RiskLevel,
    val leadDirectives: List<String>,
    val evacuationStrategy: String,
    val medicalDispatch: String,
    val logisticsSupport: String,
    val consensusAchieved: Boolean,
    val generatedTime: String
)
