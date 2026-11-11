package com.example.model

/**
 * Geographical state or union territory in India with coordinates,
 * capital, population, and historical disaster vulnerability profile.
 */
data class IndiaRegion(
    val id: String,
    val name: String,
    val code: String,
    val isUnionTerritory: Boolean = false,
    val lat: Double,
    val lon: Double,
    val capital: String,
    val populationMillions: Double,
    val primaryRisk: DisasterType,
    val secondaryRisk: DisasterType,
    val riskLevel: RiskLevel,
    val floodVulnerabilityIndex: Float, // 0.0 to 1.0
    val seismicZone: Int, // Zone II, III, IV, or V
    val cycloneProne: Boolean,
    val landslideProne: Boolean,
    val districts: List<String>
)

enum class RiskLevel(val label: String, val score: Int) {
    LOW("Low Risk", 1),
    MODERATE("Moderate Risk", 2),
    HIGH("High Risk", 3),
    CRITICAL("Very High / Critical Risk", 4)
}
