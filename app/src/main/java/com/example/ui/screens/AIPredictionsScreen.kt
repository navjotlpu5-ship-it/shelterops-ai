package com.example.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.DisasterPredictionEngine
import com.example.model.DisasterIncident
import com.example.model.DisasterType
import com.example.model.RiskLevel
import com.example.ui.components.RiskBadge
import com.example.ui.theme.*
import com.example.viewmodel.ShelterOpsUiState

@Composable
fun AIPredictionsScreen(
    uiState: ShelterOpsUiState,
    onSelectDisaster: (DisasterIncident) -> Unit,
    onSyncLiveWeather: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val incident = uiState.selectedDisaster ?: uiState.disasters.first()
    val predictionResult = remember(incident) { DisasterPredictionEngine.evaluateIncident(incident) }
    var selectedHorizonIndex by remember { mutableIntStateOf(0) }

    // Pulsing indicator for live telemetry
    val infiniteTransition = rememberInfiniteTransition(label = "LivePulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommandDarkBg)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Real-Time Weather Feed Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CommandSurface)
                    .border(1.dp, CyberCyan, RoundedCornerShape(14.dp))
                    .padding(16.dp)
                    .testTag("realtime_weather_feed_card")
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
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(SafeGreen.copy(alpha = pulseAlpha))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "REAL-TIME IMD WEATHER INTEGRATION",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        // Sync button
                        Button(
                            onClick = onSyncLiveWeather,
                            enabled = !uiState.isSyncingWeather,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp).testTag("sync_weather_button")
                        ) {
                            if (uiState.isSyncingWeather) {
                                CircularProgressIndicator(color = Color.Black, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Syncing...", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync IMD Feed", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val m = incident.metrics
                    Text(
                        text = "Condition: ${m.weatherConditionText}",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Station: ${incident.districtName}, ${incident.stateName} • Synced: ${m.lastSyncedAt}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5-Column Weather Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LiveMetricPill("Temperature", "${m.tempCelsius}°C", CyberCyan, Modifier.weight(1f))
                        LiveMetricPill("Humidity", "${m.humidityPercent}%", HighTechBlue, Modifier.weight(1f))
                        LiveMetricPill("24h Rain", "${m.rainfall24hMm}mm", if (m.rainfall24hMm > 100) CriticalRed else WarningOrange, Modifier.weight(1f))
                        LiveMetricPill("Wind", "${m.windSpeedKmh}km/h", AlertAmber, Modifier.weight(1f))
                        LiveMetricPill("Pressure", "${m.pressureHpa.toInt()}hPa", AccentPurple, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Active Data Source: IMD Doppler Weather Radar & Open-Meteo WMO observational mesh. " +
                                "All real-time readings directly drive the AI hydraulic risk and cyclonic probability calculations.",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Section: Selected Incident AI Assessment Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CommandSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "INCIDENT PREDICTION MODEL",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = incident.title,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${incident.districtName}, ${incident.stateName}",
                                color = CyberCyan,
                                fontSize = 12.sp
                            )
                        }
                        RiskBadge(riskLevel = predictionResult.overallRiskLevel)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gauges: Probability & Confidence
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PredictionGaugeBox(
                            title = "Hazard Probability",
                            value = "${(predictionResult.probabilityScore * 100).toInt()}%",
                            subtitle = "Calculated Risk Score",
                            accentColor = if (predictionResult.probabilityScore > 0.75f) CriticalRed else WarningOrange,
                            modifier = Modifier.weight(1f)
                        )
                        PredictionGaugeBox(
                            title = "Model Confidence",
                            value = "${predictionResult.confidencePercent}%",
                            subtitle = "Cross-Validation Accuracy",
                            accentColor = CyberCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "AI Reasoning Synthesis:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = predictionResult.rationale,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Section: Forecast Horizons (24h, 48h, 7d)
        item {
            Column {
                Text(
                    text = "PREDICTIVE RISK HORIZONS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                TabRow(
                    selectedTabIndex = selectedHorizonIndex,
                    containerColor = CommandSurface,
                    contentColor = CyberCyan
                ) {
                    predictionResult.horizons.forEachIndexed { index, horizon ->
                        Tab(
                            selected = selectedHorizonIndex == index,
                            onClick = { selectedHorizonIndex = index },
                            text = {
                                Text(
                                    text = horizon.horizon,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedHorizonIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedHorizonIndex == index) CyberCyan else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val currentHorizon = predictionResult.horizons[selectedHorizonIndex]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CommandSurfaceVariant)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${currentHorizon.horizon} Outlook",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            RiskBadge(riskLevel = currentHorizon.riskLevel, isCompact = true)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentHorizon.forecastSummary,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { currentHorizon.probability },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = when (currentHorizon.riskLevel) {
                                RiskLevel.CRITICAL -> CriticalRed
                                RiskLevel.HIGH -> WarningOrange
                                RiskLevel.MODERATE -> AlertAmber
                                RiskLevel.LOW -> SafeGreen
                            },
                            trackColor = CommandSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Estimated Likelihood: ${(currentHorizon.probability * 100).toInt()}%",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Section: Explainable AI - Data Contributors & Feature Weights
        item {
            Column {
                Text(
                    text = "EXPLAINABLE AI: FEATURE ATTRIBUTION",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sensor telemetry breakdown explaining which factors drove the prediction:",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                predictionResult.contributors.forEach { contributor ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CommandSurface)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = contributor.featureName,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${contributor.weightPercent}% Weight",
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { contributor.weightPercent / 100f },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                color = CyberCyan,
                                trackColor = CommandSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Observed: ${contributor.measuredValue}", color = TextSecondary, fontSize = 10.sp)
                                Text(text = contributor.thresholdImpact, color = WarningOrange, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section: Connected Data Feeds List
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CommandSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(text = "CONNECTED DATA FEEDS & OBSERVATORIES", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    incident.dataSources.forEach { source ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Icon(Icons.Default.Sensors, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = source, color = TextPrimary, fontSize = 11.sp)
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
private fun LiveMetricPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CommandSurfaceVariant)
            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = TextMuted, fontSize = 8.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = color, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun PredictionGaugeBox(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CommandSurfaceVariant)
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(text = title.uppercase(), color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = accentColor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextSecondary, fontSize = 10.sp)
        }
    }
}
