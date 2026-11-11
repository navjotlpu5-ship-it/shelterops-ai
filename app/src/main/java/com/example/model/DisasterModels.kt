package com.example.model

enum class DisasterType(val displayName: String, val iconName: String) {
    FLOOD("Flood", "ic_flood"),
    CYCLONE("Cyclone", "ic_cyclone"),
    EARTHQUAKE("Earthquake", "ic_earthquake"),
    LANDSLIDE("Landslide", "ic_landslide"),
    DROUGHT("Drought", "ic_drought"),
    FOREST_FIRE("Forest Fire", "ic_fire")
}

data class MeteorologicalMetrics(
    val rainfall24hMm: Double,
    val riverLevelMeters: Double = 0.0,
    val riverDangerMarkMeters: Double = 0.0,
    val windSpeedKmh: Double = 0.0,
    val seismicMagnitude: Double = 0.0,
    val soilMoisturePercent: Int = 50,
    val tempCelsius: Double = 28.0,
    val humidityPercent: Int = 75,
    val pressureHpa: Double = 1012.0,
    val weatherConditionText: String = "Observing",
    val lastSyncedAt: String = "Cached Baseline",
    val isLiveTelemetry: Boolean = false
) {
    val isRiverAboveDanger: Boolean
        get() = riverDangerMarkMeters > 0 && riverLevelMeters >= riverDangerMarkMeters
}

data class DataContributor(
    val featureName: String,
    val weightPercent: Int,
    val measuredValue: String,
    val thresholdImpact: String
)

data class HorizonPrediction(
    val horizon: String, // "24 Hours", "48 Hours", "7 Days"
    val riskLevel: RiskLevel,
    val probability: Float, // 0.0 - 1.0
    val forecastSummary: String
)

data class DisasterIncident(
    val id: String,
    val title: String,
    val type: DisasterType,
    val stateName: String,
    val districtName: String,
    val lat: Double,
    val lon: Double,
    val severity: RiskLevel,
    val probability: Float, // 0.0 to 1.0
    val affectedPopulation: Int,
    val metrics: MeteorologicalMetrics,
    val horizonPredictions: List<HorizonPrediction>,
    val contributors: List<DataContributor>,
    val confidencePercent: Int,
    val dataSources: List<String>,
    val isSimulated: Boolean,
    val recommendedActions: List<String>,
    val reportedAt: String
)
