package com.example.engine

import com.example.model.DisasterIncident
import com.example.model.DisasterType
import com.example.model.EvacuationRoute
import com.example.model.RouteStatus
import com.example.model.Shelter
import kotlin.math.*

object RoutePlanningEngine {

    /**
     * Computes distance in kilometers between two geo-coordinates using Haversine formula
     */
    fun calculateHaversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Generates a realistic evacuation route with terrain tortuosity, hazard detection, and transit modes
     */
    fun planRoute(incident: DisasterIncident, shelter: Shelter): EvacuationRoute {
        val straightDist = calculateHaversineDistanceKm(incident.lat, incident.lon, shelter.lat, shelter.lon)
        
        // Terrain multiplier: Mountainous regions have winding roads (tortuosity factor ~1.55x); Plains ~1.25x
        val isHilly = incident.type == DisasterType.LANDSLIDE || incident.stateName in listOf("Uttarakhand", "Himachal Pradesh", "Jammu & Kashmir", "Sikkim")
        val tortuosity = if (isHilly) 1.55 else 1.25
        val roadDistanceKm = (straightDist * tortuosity).coerceAtLeast(3.2)

        // Average transit speed
        val avgSpeedKmh = if (isHilly) 28.0 else 42.0
        val travelTimeMins = ((roadDistanceKm / avgSpeedKmh) * 60).roundToInt().coerceAtLeast(15)

        // Road condition detection
        val (routeStatus, roadName, hazardNote, transitModes) = when {
            // Scenario 1: Assam Brahmaputra Floods
            incident.id == "DIS_ASSAM_01" && shelter.name.contains("Barpeta", ignoreCase = true) -> {
                Quad(
                    RouteStatus.ELEVATED_RISK,
                    "NH-27 High-Elevated Bypass via Howly",
                    "Lowland feeder roads submerged; use elevated embankment corridor",
                    listOf("NDRF Inflatable Rescue Boats", "State Transport Elevated Buses", "Army 4x4 Heavy Shuttles")
                )
            }
            incident.id == "DIS_ASSAM_01" && shelter.name.contains("Morigaon", ignoreCase = true) -> {
                Quad(
                    RouteStatus.CLEAR_SAFE,
                    "State Highway 3 (Jagi Road - Morigaon Safe Axis)",
                    "Cleared by NDRF motorized reconnaissance squads",
                    listOf("SDRF Evacuation Shuttles", "Ambulances", "Relief Mini-Buses")
                )
            }
            incident.id == "DIS_ASSAM_01" && shelter.name.contains("Dhubri", ignoreCase = true) -> {
                Quad(
                    RouteStatus.BLOCKED,
                    "Old NH-37 Riverside Trunk Road",
                    "Breached at km marker 142 due to Brahmaputra dyke overflow. Detour required via NH-17.",
                    listOf("River Patrol Barges Only")
                )
            }

            // Scenario 2: Odisha Cyclone
            incident.id == "DIS_ODISHA_02" && shelter.name.contains("Puri", ignoreCase = true) -> {
                Quad(
                    RouteStatus.CLEAR_SAFE,
                    "NH-316 New Puri-Bhubaneswar Expressway",
                    "Pre-cleared heavy debris-resistant corridor with power tree-cutters deployed",
                    listOf("ODRAF Emergency Buses", "Medical Mobile Ambulances")
                )
            }
            incident.id == "DIS_ODISHA_02" && shelter.name.contains("Jagatsinghpur", ignoreCase = true) -> {
                Quad(
                    RouteStatus.ELEVATED_RISK,
                    "Coastal Embankment Road SH-12",
                    "High tidal wave spray and gale squalls; convoy speed restricted to 20 km/h",
                    listOf("Armored Police Transporters", "NDRF Heavy Trucks")
                )
            }

            // Scenario 3: Uttarakhand Earthquake
            incident.id == "DIS_UK_03" && shelter.name.contains("Joshimath", ignoreCase = true) -> {
                Quad(
                    RouteStatus.ELEVATED_RISK,
                    "Badrinath National Highway (NH-7)",
                    "Minor rockfall cleared near Helang; one-way emergency convoy escort active",
                    listOf("Indian Army 4WD Shuttles", "Mountain Rescue 4x4 Jeeps", "Paramedic Ambulances")
                )
            }
            incident.id == "DIS_UK_03" && shelter.name.contains("Gopeshwar", ignoreCase = true) -> {
                Quad(
                    RouteStatus.CLEAR_SAFE,
                    "Chamoli-Gopeshwar Arterial Hill Highway",
                    "Structurally surveyed and cleared for two-way relief transit",
                    listOf("SDRF Evacuation Vans", "Trauma Care Ambulances")
                )
            }

            // Scenario 4: Himachal Landslide
            incident.id == "DIS_HP_04" && shelter.name.contains("Kullu", ignoreCase = true) -> {
                Quad(
                    RouteStatus.BLOCKED,
                    "NH-21 Pandoh Dam to Aut Tunnel Stretch",
                    "Massive debris slide blocking both lanes; transit rerouted through Kataula-Bajaura hill road.",
                    listOf("Light Mountain Vehicles Only via Kataula Bypass")
                )
            }
            incident.id == "DIS_HP_04" -> {
                Quad(
                    RouteStatus.CLEAR_SAFE,
                    "Mandi Valley Main Access Road",
                    "Open and reinforced with concrete retaining walls",
                    listOf("Himachal Roadways Emergency Buses", "Civil Defense Vans")
                )
            }

            // Scenario 5: Punjab Flood
            incident.id == "DIS_PB_05" -> {
                Quad(
                    RouteStatus.CLEAR_SAFE,
                    "Rupnagar-Anandpur Sahib Four-Lane Highway (NH-205)",
                    "Elevated expressway above flood inundation levels",
                    listOf("Punjab State Transport Buses", "Tractor-Trolley Relief Convoys", "Mobile Medical Vans")
                )
            }

            // General fallback
            else -> {
                Quad(
                    RouteStatus.CLEAR_SAFE,
                    "National/State Primary Emergency Transit Axis",
                    "Designated green corridor monitored by Traffic Police & Civil Defense",
                    listOf("Emergency Evacuation Buses", "Rapid Response Ambulances")
                )
            }
        }

        return EvacuationRoute(
            id = "ROUTE_${incident.id}_${shelter.id}",
            fromLocation = "${incident.districtName}, ${incident.stateName}",
            toShelterName = shelter.name,
            distanceKm = (roadDistanceKm * 10.0).roundToInt() / 10.0,
            travelTimeMins = travelTimeMins,
            status = routeStatus,
            roadName = roadName,
            transitModes = transitModes,
            hazardNote = hazardNote,
            detourAvailable = true
        )
    }

    private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
