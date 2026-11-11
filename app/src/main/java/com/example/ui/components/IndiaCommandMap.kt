package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RoutePlanningEngine
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.sqrt

@OptIn(ExperimentalTextApi::class)
@Composable
fun IndiaCommandMap(
    regions: List<IndiaRegion>,
    disasters: List<DisasterIncident>,
    shelters: List<Shelter>,
    selectedDisaster: DisasterIncident?,
    selectedShelter: Shelter?,
    routes: List<EvacuationRoute> = emptyList(),
    onDisasterSelected: (DisasterIncident) -> Unit,
    onShelterSelected: (Shelter) -> Unit,
    onRegionSelected: (IndiaRegion) -> Unit,
    modifier: Modifier = Modifier
) {
    // Map bounds for India (Lat: ~7°N to ~37.5°N, Lon: ~67°E to ~98°E)
    val minLon = 67.0
    val maxLon = 98.0
    val minLat = 7.0
    val maxLat = 37.5

    // Transform State (Pan & Zoom)
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Pulsing radar animation
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    // Layer toggles
    var showDisasters by remember { mutableStateOf(true) }
    var showShelters by remember { mutableStateOf(true) }
    var showRoutes by remember { mutableStateOf(true) }
    var showRiskOverlays by remember { mutableStateOf(true) }

    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CommandDarkBg)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("india_interactive_canvas")
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.85f, 4.5f)
                        panOffset = Offset(
                            x = panOffset.x + pan.x,
                            y = panOffset.y + pan.y
                        )
                    }
                }
                .pointerInput(regions, disasters, shelters, zoomScale, panOffset) {
                    detectTapGestures { tapOffset ->
                        val w = size.width
                        val h = size.height

                        fun toScreenOffset(lat: Double, lon: Double): Offset {
                            val normX = ((lon - minLon) / (maxLon - minLon)).toFloat()
                            val normY = ((maxLat - lat) / (maxLat - minLat)).toFloat()
                            val baseX = normX * w
                            val baseY = normY * h
                            val centeredX = (baseX - w / 2f) * zoomScale + w / 2f + panOffset.x
                            val centeredY = (baseY - h / 2f) * zoomScale + h / 2f + panOffset.y
                            return Offset(centeredX, centeredY)
                        }

                        // Check tap against disaster pins (radius 24dp)
                        var clickedDisaster: DisasterIncident? = null
                        for (d in disasters) {
                            val pin = toScreenOffset(d.lat, d.lon)
                            val dist = sqrt((tapOffset.x - pin.x) * (tapOffset.x - pin.x) + (tapOffset.y - pin.y) * (tapOffset.y - pin.y))
                            if (dist <= 36f) {
                                clickedDisaster = d
                                break
                            }
                        }

                        if (clickedDisaster != null) {
                            onDisasterSelected(clickedDisaster)
                            return@detectTapGestures
                        }

                        // Check tap against shelter pins
                        var clickedShelter: Shelter? = null
                        for (s in shelters) {
                            val pin = toScreenOffset(s.lat, s.lon)
                            val dist = sqrt((tapOffset.x - pin.x) * (tapOffset.x - pin.x) + (tapOffset.y - pin.y) * (tapOffset.y - pin.y))
                            if (dist <= 30f) {
                                clickedShelter = s
                                break
                            }
                        }

                        if (clickedShelter != null) {
                            onShelterSelected(clickedShelter)
                            return@detectTapGestures
                        }

                        // Check tap against regions
                        for (r in regions) {
                            val pin = toScreenOffset(r.lat, r.lon)
                            val dist = sqrt((tapOffset.x - pin.x) * (tapOffset.x - pin.x) + (tapOffset.y - pin.y) * (tapOffset.y - pin.y))
                            if (dist <= 28f) {
                                onRegionSelected(r)
                                break
                            }
                        }
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height

            fun project(lat: Double, lon: Double): Offset {
                val normX = ((lon - minLon) / (maxLon - minLon)).toFloat()
                val normY = ((maxLat - lat) / (maxLat - minLat)).toFloat()
                val baseX = normX * canvasW
                val baseY = normY * canvasH
                val cx = (baseX - canvasW / 2f) * zoomScale + canvasW / 2f + panOffset.x
                val cy = (baseY - canvasH / 2f) * zoomScale + canvasH / 2f + panOffset.y
                return Offset(cx, cy)
            }

            // 1. Draw Subcontinent Grid and Guide Lines
            drawGrid(zoomScale, panOffset)

            // 2. Draw Simplified India Landmass Contour
            drawIndiaLandmassContour(::project)

            // 3. Draw State Risk Halos & Labels
            if (showRiskOverlays) {
                for (region in regions) {
                    val center = project(region.lat, region.lon)
                    val baseRadius = 24f * zoomScale

                    val haloColor = when (region.riskLevel) {
                        RiskLevel.CRITICAL -> CriticalRed.copy(alpha = 0.22f)
                        RiskLevel.HIGH -> WarningOrange.copy(alpha = 0.20f)
                        RiskLevel.MODERATE -> AlertAmber.copy(alpha = 0.16f)
                        RiskLevel.LOW -> SafeGreen.copy(alpha = 0.12f)
                    }

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(haloColor, Color.Transparent),
                            center = center,
                            radius = baseRadius * 1.8f
                        ),
                        radius = baseRadius * 1.8f,
                        center = center
                    )

                    // Draw state abbreviation code
                    if (zoomScale >= 1.0f) {
                        val textLayout = textMeasurer.measure(
                            text = region.code,
                            style = TextStyle(
                                color = TextMuted.copy(alpha = 0.65f),
                                fontSize = (9 * zoomScale.coerceIn(0.9f, 1.4f)).sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(center.x - textLayout.size.width / 2f, center.y - textLayout.size.height / 2f)
                        )
                    }
                }
            }

            // 4. Draw Evacuation Routes (Connecting selected disaster to shelters)
            if (showRoutes && selectedDisaster != null) {
                val dOffset = project(selectedDisaster.lat, selectedDisaster.lon)
                for (s in shelters) {
                    val isCandidate = s.stateName.equals(selectedDisaster.stateName, ignoreCase = true) ||
                            (s.isAvailable && RoutePlanningEngine.calculateHaversineDistanceKm(selectedDisaster.lat, selectedDisaster.lon, s.lat, s.lon) < 220.0)

                    if (isCandidate) {
                        val sOffset = project(s.lat, s.lon)
                        val matchingRoute = routes.find { it.toShelterName.equals(s.name, ignoreCase = true) }
                        val isBlocked = matchingRoute?.status == RouteStatus.BLOCKED

                        val routeColor = if (isBlocked) CriticalRed else SafeGreen
                        val strokeStyle = if (isBlocked) Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f)))
                        else Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), pulseProgress * 30f))

                        drawLine(
                            color = routeColor.copy(alpha = 0.85f),
                            start = dOffset,
                            end = sOffset,
                            strokeWidth = 3.5f,
                            pathEffect = strokeStyle.pathEffect
                        )

                        // If blocked, draw red warning indicator midpoint
                        if (isBlocked) {
                            val mid = Offset((dOffset.x + sOffset.x) / 2f, (dOffset.y + sOffset.y) / 2f)
                            drawCircle(color = CriticalRed, radius = 9f, center = mid)
                            drawLine(color = Color.White, start = Offset(mid.x - 4f, mid.y - 4f), end = Offset(mid.x + 4f, mid.y + 4f), strokeWidth = 2f)
                            drawLine(color = Color.White, start = Offset(mid.x + 4f, mid.y - 4f), end = Offset(mid.x - 4f, mid.y + 4f), strokeWidth = 2f)
                        }
                    }
                }
            }

            // 5. Draw Shelter Pins
            if (showShelters) {
                for (s in shelters) {
                    val sOffset = project(s.lat, s.lon)
                    val isSelected = selectedShelter?.id == s.id

                    val pinColor = when {
                        s.status == ShelterStatus.COMPROMISED -> CriticalRed
                        s.status == ShelterStatus.FULL -> AlertAmber
                        s.occupancyPercentage > 85 -> WarningOrange
                        else -> SafeGreen
                    }

                    // Shelter base circle
                    val radius = if (isSelected) 13f * zoomScale else 9f * zoomScale
                    drawCircle(
                        color = pinColor,
                        radius = radius,
                        center = sOffset
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = radius * 0.45f,
                        center = sOffset
                    )
                    drawCircle(
                        color = Color.White,
                        radius = radius,
                        center = sOffset,
                        style = Stroke(width = 2f)
                    )

                    // Draw shelter bed indicator if zoomed in
                    if (zoomScale >= 1.2f) {
                        val labelText = "${s.availableBeds} beds"
                        val textLayout = textMeasurer.measure(
                            text = labelText,
                            style = TextStyle(
                                color = SafeGreen,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                background = CommandSurface.copy(alpha = 0.85f)
                            )
                        )
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(sOffset.x - textLayout.size.width / 2f, sOffset.y + radius + 3f)
                        )
                    }
                }
            }

            // 6. Draw Disaster Pins with Pulsing Radar
            if (showDisasters) {
                for (d in disasters) {
                    val dOffset = project(d.lat, d.lon)
                    val isSelected = selectedDisaster?.id == d.id

                    val pinColor = when (d.severity) {
                        RiskLevel.CRITICAL -> CriticalRed
                        RiskLevel.HIGH -> WarningOrange
                        RiskLevel.MODERATE -> AlertAmber
                        RiskLevel.LOW -> SafeGreen
                    }

                    // Radar pulse wave
                    val pulseRadius = (22f + (pulseProgress * 38f)) * zoomScale
                    val pulseAlpha = (1f - pulseProgress) * 0.6f
                    drawCircle(
                        color = pinColor.copy(alpha = pulseAlpha),
                        radius = pulseRadius,
                        center = dOffset,
                        style = Stroke(width = 2.5f)
                    )

                    // Inner core marker
                    val coreRadius = if (isSelected) 14f * zoomScale else 10f * zoomScale
                    drawCircle(
                        color = pinColor,
                        radius = coreRadius,
                        center = dOffset
                    )
                    drawCircle(
                        color = Color.White,
                        radius = coreRadius,
                        center = dOffset,
                        style = Stroke(width = 2f)
                    )

                    // Label for disaster
                    val labelText = "${d.type.displayName.uppercase()}: ${d.districtName}"
                    val textLayout = textMeasurer.measure(
                        text = labelText,
                        style = TextStyle(
                            color = if (isSelected) CyberCyan else TextPrimary,
                            fontSize = (9 * zoomScale.coerceIn(0.9f, 1.2f)).sp,
                            fontWeight = FontWeight.ExtraBold,
                            background = CommandDarkBg.copy(alpha = 0.85f)
                        )
                    )
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(dOffset.x - textLayout.size.width / 2f, dOffset.y - coreRadius - textLayout.size.height - 3f)
                    )
                }
            }
        }

        // Top-right Map Controls (Zoom +, Zoom -, Reset, Layer Toggles)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilledIconButton(
                onClick = { zoomScale = (zoomScale * 1.3f).coerceAtMost(4.5f) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = CommandSurfaceVariant),
                modifier = Modifier.size(36.dp).testTag("zoom_in_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = TextPrimary)
            }
            FilledIconButton(
                onClick = { zoomScale = (zoomScale / 1.3f).coerceAtLeast(0.85f) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = CommandSurfaceVariant),
                modifier = Modifier.size(36.dp).testTag("zoom_out_button")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = TextPrimary)
            }
            FilledIconButton(
                onClick = {
                    zoomScale = 1.0f
                    panOffset = Offset.Zero
                },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = CommandSurfaceVariant),
                modifier = Modifier.size(36.dp).testTag("reset_map_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset View", tint = CyberCyan)
            }
        }

        // Bottom Map Legend
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CommandSurface.copy(alpha = 0.92f))
                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LegendItem(color = CriticalRed, label = "Critical Risk")
                LegendItem(color = WarningOrange, label = "High")
                LegendItem(color = AlertAmber, label = "Moderate")
                LegendItem(color = SafeGreen, label = "Safe Shelter")
                LegendItem(color = CyberCyan, label = "Evacuation Vector")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

/**
 * Draws the schematic background grid of latitude/longitude coordinates
 */
private fun DrawScope.drawGrid(zoomScale: Float, panOffset: Offset) {
    val step = 60f * zoomScale
    val stroke = Stroke(width = 0.6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f)))
    var x = panOffset.x % step
    while (x < size.width) {
        drawLine(color = BorderSubtle.copy(alpha = 0.4f), start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 0.6f, pathEffect = stroke.pathEffect)
        x += step
    }
    var y = panOffset.y % step
    while (y < size.height) {
        drawLine(color = BorderSubtle.copy(alpha = 0.4f), start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 0.6f, pathEffect = stroke.pathEffect)
        y += step
    }
}

/**
 * Renders the prominent mainland geometric contour of India
 */
private fun DrawScope.drawIndiaLandmassContour(project: (Double, Double) -> Offset) {
    // Strategic anchor coordinates tracing northern Himalayas, eastern coastline, southern tip (Kanyakumari), and western coastline (Gujarat/Rann)
    val boundaryPoints = listOf(
        Pair(35.5, 74.8), // Kashmir North-West
        Pair(34.8, 77.2), // Ladakh Karakoram
        Pair(32.5, 78.9), // HP/Tibet Border
        Pair(30.6, 80.8), // Uttarakhand Border
        Pair(27.9, 88.5), // Sikkim
        Pair(28.5, 94.5), // Arunachal Pradesh North
        Pair(27.2, 96.5), // Arunachal East
        Pair(24.5, 93.8), // Manipur / Mizoram
        Pair(22.2, 89.1), // Sundarbans / Bengal Coast
        Pair(20.0, 86.2), // Odisha Coast (Paradip)
        Pair(17.7, 83.3), // Visakhapatnam
        Pair(13.1, 80.3), // Chennai Coast
        Pair(8.1, 77.5),  // Kanyakumari Tip
        Pair(9.9, 76.2),  // Kochi / Kerala Coast
        Pair(15.4, 73.8), // Goa Coast
        Pair(19.0, 72.8), // Mumbai / Maharashtra Coast
        Pair(20.8, 70.4), // Saurashtra South
        Pair(22.3, 69.0), // Dwarka / Gujarat West
        Pair(24.0, 68.8), // Kutch Rann
        Pair(27.5, 71.2), // Rajasthan Desert Border
        Pair(31.5, 74.5), // Punjab Border
        Pair(35.5, 74.8)  // Closing Loop
    )

    val path = Path()
    boundaryPoints.forEachIndexed { index, (lat, lon) ->
        val pt = project(lat, lon)
        if (index == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
    }

    // Draw translucent mainland fill
    drawPath(
        path = path,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF132035), Color(0xFF0C1423)),
            center = project(22.0, 79.0),
            radius = size.width * 0.7f
        )
    )

    // Draw glowing cyan subcontinent border
    drawPath(
        path = path,
        color = CyberCyan.copy(alpha = 0.55f),
        style = Stroke(width = 2.0f, pathEffect = PathEffect.cornerPathEffect(18f))
    )
}
