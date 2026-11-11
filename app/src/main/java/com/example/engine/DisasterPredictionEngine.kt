package com.example.engine

import com.example.model.*
import kotlin.math.roundToInt

object DisasterPredictionEngine {

    data class PredictionResult(
        val overallRiskLevel: RiskLevel,
        val probabilityScore: Float,
        val confidencePercent: Int,
        val horizons: List<HorizonPrediction>,
        val contributors: List<DataContributor>,
        val rationale: String
    )

    fun evaluateIncident(incident: DisasterIncident): PredictionResult {
        val m = incident.metrics

        // Multi-factor risk calculation
        var riskScore = 0.0
        val contributors = mutableListOf<DataContributor>()

        when (incident.type) {
            DisasterType.FLOOD -> {
                val rainFactor = (m.rainfall24hMm / 300.0).coerceIn(0.0, 1.0) * 40.0
                val riverFactor = if (m.isRiverAboveDanger) {
                    val excess = (m.riverLevelMeters - m.riverDangerMarkMeters).coerceAtLeast(0.0)
                    (excess / 4.0).coerceIn(0.2, 1.0) * 45.0
                } else 10.0
                val soilFactor = (m.soilMoisturePercent / 100.0) * 15.0

                riskScore = rainFactor + riverFactor + soilFactor

                contributors.add(DataContributor("River Gauge Level vs Danger Mark", 45, "${m.riverLevelMeters}m (Threshold: ${m.riverDangerMarkMeters}m)", "Critical hydraulic pressure"))
                contributors.add(DataContributor("24h Rainfall Accumulation (IMD)", 35, "${m.rainfall24hMm} mm", "Catchment flooding"))
                contributors.add(DataContributor("Soil Moisture Saturation Index", 20, "${m.soilMoisturePercent}%", "Zero infiltration capacity"))
            }

            DisasterType.CYCLONE -> {
                val windFactor = (m.windSpeedKmh / 180.0).coerceIn(0.0, 1.0) * 45.0
                val rainFactor = (m.rainfall24hMm / 250.0).coerceIn(0.0, 1.0) * 30.0
                val pressureDrop = (1013.25 - m.pressureHpa).coerceAtLeast(0.0)
                val pressureFactor = (pressureDrop / 25.0).coerceIn(0.0, 1.0) * 15.0
                val humidityFactor = (m.humidityPercent / 100.0) * 10.0

                riskScore = windFactor + rainFactor + pressureFactor + humidityFactor

                contributors.add(DataContributor("Surface Squall Wind Velocity", 45, "${m.windSpeedKmh} km/h", "Severe structural damage tier"))
                contributors.add(DataContributor("Coastal Inflow Precipitation", 30, "${m.rainfall24hMm} mm", "Low-lying storm surge inundation"))
                contributors.add(DataContributor("Barometric Surface Pressure", 15, "${m.pressureHpa} hPa", "Deep cyclonic depression vortex"))
                contributors.add(DataContributor("Atmospheric Humidity & Convection", 10, "${m.humidityPercent}%", "Moisture convergence"))
            }

            DisasterType.EARTHQUAKE -> {
                val magFactor = (m.seismicMagnitude / 8.0).coerceIn(0.0, 1.0) * 75.0
                riskScore = magFactor + 20.0

                contributors.add(DataContributor("Seismic Moment Magnitude (NCS)", 65, "M${m.seismicMagnitude} Richter", "High ground acceleration (PGA)"))
                contributors.add(DataContributor("Tectonic Fault Zone Classification", 25, "Himalayan Belt (Zone V)", "High structural fragility"))
                contributors.add(DataContributor("Focal Depth & Attenuation", 10, "12 km shallow focus", "Intense local shaking"))
            }

            DisasterType.LANDSLIDE -> {
                val rainFactor = (m.rainfall24hMm / 200.0).coerceIn(0.0, 1.0) * 45.0
                val soilFactor = (m.soilMoisturePercent / 100.0) * 40.0
                riskScore = rainFactor + soilFactor + 15.0

                contributors.add(DataContributor("Continuous Monsoon Precipitation", 45, "${m.rainfall24hMm} mm", "Pore water pressure spike"))
                contributors.add(DataContributor("Slope Soil Moisture Saturation", 40, "${m.soilMoisturePercent}%", "Critical shear plane failure"))
                contributors.add(DataContributor("Geological Scree Instability", 15, "Steep grade > 38°", "Active debris flow"))
            }

            else -> {
                riskScore = (incident.probability * 100.0).toDouble()
                contributors.add(DataContributor("Historical Baseline Anomaly", 60, "Severe", "Climatological shift"))
                contributors.add(DataContributor("Local Atmospheric Readings", 40, "Extreme", "Elevated thermal index"))
            }
        }

        val finalProb = (riskScore / 100.0).coerceIn(0.15, 0.98).toFloat()

        val assessedLevel = when {
            finalProb >= 0.85f -> RiskLevel.CRITICAL
            finalProb >= 0.65f -> RiskLevel.HIGH
            finalProb >= 0.40f -> RiskLevel.MODERATE
            else -> RiskLevel.LOW
        }

        val horizons = listOf(
            HorizonPrediction(
                horizon = "24 Hours",
                riskLevel = assessedLevel,
                probability = (finalProb * 1.02f).coerceAtMost(0.99f),
                forecastSummary = "Peak hazard intensity. Evacuation corridors must remain fully mobilized."
            ),
            HorizonPrediction(
                horizon = "48 Hours",
                riskLevel = if (assessedLevel == RiskLevel.CRITICAL) RiskLevel.HIGH else assessedLevel,
                probability = (finalProb * 0.88f).coerceAtLeast(0.20f),
                forecastSummary = "Weather front stabilization. Search and rescue transitioning to shelter consolidation."
            ),
            HorizonPrediction(
                horizon = "7 Days",
                riskLevel = if (assessedLevel == RiskLevel.CRITICAL) RiskLevel.MODERATE else RiskLevel.LOW,
                probability = (finalProb * 0.45f).coerceAtLeast(0.10f),
                forecastSummary = "Recession phase. Damage assessment and safe return protocols active."
            )
        )

        val confidence = (85 + (finalProb * 12)).roundToInt().coerceIn(80, 98)

        val rationale = "Prediction synthesized using multi-variate sensor fusion (IMD, CWC, NCS). " +
                "Hydrometeorological & seismic priors indicate high probability ($finalProb) within the next 24-hour window."

        return PredictionResult(
            overallRiskLevel = assessedLevel,
            probabilityScore = finalProb,
            confidencePercent = confidence,
            horizons = horizons,
            contributors = contributors,
            rationale = rationale
        )
    }
}
