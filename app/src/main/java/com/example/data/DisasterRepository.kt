package com.example.data

import com.example.model.*

object DisasterRepository {

    fun getInitialDisasters(): List<DisasterIncident> {
        return listOf(
            // 1. Floods in Assam (Scenario 1)
            DisasterIncident(
                id = "DIS_ASSAM_01",
                title = "Brahmaputra Basin Severe Flash Inundation",
                type = DisasterType.FLOOD,
                stateName = "Assam",
                districtName = "Barpeta & Morigaon",
                lat = 26.32,
                lon = 91.00,
                severity = RiskLevel.CRITICAL,
                probability = 0.94f,
                affectedPopulation = 18500,
                metrics = MeteorologicalMetrics(
                    rainfall24hMm = 284.5,
                    riverLevelMeters = 44.82,
                    riverDangerMarkMeters = 41.50,
                    tempCelsius = 27.5,
                    humidityPercent = 94,
                    soilMoisturePercent = 96
                ),
                horizonPredictions = listOf(
                    HorizonPrediction("24 Hours", RiskLevel.CRITICAL, 0.95f, "Inundation expanding to 142 low-lying revenue villages"),
                    HorizonPrediction("48 Hours", RiskLevel.HIGH, 0.88f, "Upstream runoff peaking; water level stabilization expected"),
                    HorizonPrediction("7 Days", RiskLevel.MODERATE, 0.62f, "Recession begins if monsoon trough shifts southward")
                ),
                contributors = listOf(
                    DataContributor("CWC River Level Surge (+3.32m above danger mark)", 42, "44.82m", "Critical breach of secondary dykes"),
                    DataContributor("IMD Doppler Radar Extreme Precipitation", 34, "284.5 mm / 24h", "Heavy downpour saturation"),
                    DataContributor("Topographic Slope & Soil Saturation", 15, "96% moisture", "Zero absorption capacity"),
                    DataContributor("Historical 10-Yr Flood Recurrence", 9, "High affinity", "Brahmaputra high risk corridor")
                ),
                confidencePercent = 94,
                dataSources = listOf("IMD Guwahati Radar", "CWC Gauge #204 (Dhubri)", "NDMA Sentinel-1 SAR Flood Inundation Map"),
                isSimulated = true,
                recommendedActions = listOf(
                    "Deploy NDRF 1st Battalion boat rescue teams to Barpeta and Morigaon lowlands",
                    "Evacuate vulnerable populations immediately to high-altitude relief centers",
                    "Activate chlorine water-purification units to prevent cholera outbreaks",
                    "Establish alternate transport along NH-27 bypass as NH-37 is partially submerged"
                ),
                reportedAt = "Today, 04:15 IST"
            ),

            // 2. Cyclones in Odisha (Scenario 2)
            DisasterIncident(
                id = "DIS_ODISHA_02",
                title = "Very Severe Cyclonic Storm 'Sagar' Landfall",
                type = DisasterType.CYCLONE,
                stateName = "Odisha",
                districtName = "Puri & Kendrapara",
                lat = 19.81,
                lon = 85.83,
                severity = RiskLevel.CRITICAL,
                probability = 0.96f,
                affectedPopulation = 24000,
                metrics = MeteorologicalMetrics(
                    rainfall24hMm = 310.0,
                    windSpeedKmh = 145.0,
                    tempCelsius = 26.0,
                    humidityPercent = 98,
                    soilMoisturePercent = 90
                ),
                horizonPredictions = listOf(
                    HorizonPrediction("24 Hours", RiskLevel.CRITICAL, 0.98f, "Peak coastal storm surge 2.5m - 3.8m expected at landfall"),
                    HorizonPrediction("48 Hours", RiskLevel.HIGH, 0.85f, "Decaying into deep depression moving northwest towards Mayurbhanj"),
                    HorizonPrediction("7 Days", RiskLevel.LOW, 0.20f, "Cyclone dissipation complete, restoration phase active")
                ),
                contributors = listOf(
                    DataContributor("INSAT-3D Cyclone Intensity & Dvorak T-Number", 45, "T5.0 (145 km/h)", "Destructive squall winds"),
                    DataContributor("Tidal Observatory Storm Surge Model", 30, "3.2m surge", "Sea water inundation of 5km coastal belt"),
                    DataContributor("IMD Coastal Radar Paradip", 18, "310 mm rain", "Severe localized flash flooding"),
                    DataContributor("Population Density in Kutchha Houses", 7, "High risk housing", "High roof-collapse vulnerability")
                ),
                confidencePercent = 96,
                dataSources = listOf("IMD Regional Meteorological Centre Bhubaneswar", "INCOIS Tsunami & Surge Center", "NDRF 3rd Battalion Cuttack"),
                isSimulated = true,
                recommendedActions = listOf(
                    "Mandatory evacuation of all settlements within 3km of coastal shoreline",
                    "Stock Multipurpose Cyclone Shelters (ODRAF) with dry food and diesel generators",
                    "Issue red alert to coastal fishermen and suspend offshore port operations at Paradip",
                    "Pre-position power grid emergency restoration teams with heavy cranes"
                ),
                reportedAt = "Today, 03:30 IST"
            ),

            // 3. Earthquakes in Uttarakhand (Scenario 3)
            DisasterIncident(
                id = "DIS_UK_03",
                title = "Chamoli-Joshimath M6.8 Himalayan Tectonic Event",
                type = DisasterType.EARTHQUAKE,
                stateName = "Uttarakhand",
                districtName = "Chamoli & Rudraprayag",
                lat = 30.38,
                lon = 79.33,
                severity = RiskLevel.CRITICAL,
                probability = 0.91f,
                affectedPopulation = 8200,
                metrics = MeteorologicalMetrics(
                    rainfall24hMm = 12.0,
                    seismicMagnitude = 6.8,
                    tempCelsius = 14.0,
                    humidityPercent = 60
                ),
                horizonPredictions = listOf(
                    HorizonPrediction("24 Hours", RiskLevel.CRITICAL, 0.92f, "High probability of M4.5+ aftershocks along Main Central Thrust (MCT)"),
                    HorizonPrediction("48 Hours", RiskLevel.HIGH, 0.79f, "Slope stabilization monitoring; secondary landslide warnings"),
                    HorizonPrediction("7 Days", RiskLevel.MODERATE, 0.45f, "Aftershock frequency decay following Omori's Law")
                ),
                contributors = listOf(
                    DataContributor("National Center for Seismology (NCS) Hypocenter", 50, "6.8 Richter / 12km depth", "Severe surface ground motion"),
                    DataContributor("Structural Fragility & Old Stone Masonry", 25, "Zone V location", "High collapse probability"),
                    DataContributor("Steep Himalayan Slope Instability", 18, "42° average grade", "Triggered debris and rockfall avalanches"),
                    DataContributor("Historical 1999 Chamoli Seismic Fault", 7, "MCT Fault Line", "Known seismic gap energy release")
                ),
                confidencePercent = 91,
                dataSources = listOf("National Center for Seismology (NCS)", "Wadia Institute of Himalayan Geology", "SDRF Uttarakhand"),
                isSimulated = true,
                recommendedActions = listOf(
                    "Deploy Indian Army and SDRF canine search-and-rescue teams to Joshimath rubble zones",
                    "Establish open-ground seismic relief camps in Gopeshwar sports complex",
                    "Airtight inspection of hydel power dams along Alaknanda river",
                    "Mobilize mobile trauma surgical units and blood reserves from Dehradun"
                ),
                reportedAt = "Today, 02:45 IST"
            ),

            // 4. Landslides in Himachal Pradesh (Scenario 4)
            DisasterIncident(
                id = "DIS_HP_04",
                title = "Mandi-Kullu Cloudburst Debris Flow & Slope Rupture",
                type = DisasterType.LANDSLIDE,
                stateName = "Himachal Pradesh",
                districtName = "Mandi & Kullu",
                lat = 31.70,
                lon = 76.93,
                severity = RiskLevel.HIGH,
                probability = 0.88f,
                affectedPopulation = 5400,
                metrics = MeteorologicalMetrics(
                    rainfall24hMm = 195.0,
                    soilMoisturePercent = 98,
                    tempCelsius = 19.0,
                    humidityPercent = 92
                ),
                horizonPredictions = listOf(
                    HorizonPrediction("24 Hours", RiskLevel.HIGH, 0.90f, "Ongoing saturated mudflow; active slope creeping along NH-21"),
                    HorizonPrediction("48 Hours", RiskLevel.HIGH, 0.82f, "Further rainfall predicted, high vulnerability of valley road corridors"),
                    HorizonPrediction("7 Days", RiskLevel.MODERATE, 0.50f, "Excavation and geological pinning of fractured rockfaces")
                ),
                contributors = listOf(
                    DataContributor("Soil Moisture Sensor Saturation Rate", 40, "98% saturated", "Shear strength reduction in soil strata"),
                    DataContributor("IMD Cloudburst Localized Rainfall Peak", 35, "195mm / 6 hrs", "Sudden torrential hydraulic pressure"),
                    DataContributor("Geological Survey of India (GSI) Slope Map", 17, "Unstable scree slope", "High historical landslide recurrence"),
                    DataContributor("Vegetative Deforestation Index", 8, "Moderate canopy", "Reduced root cohesion on hillsides")
                ),
                confidencePercent = 88,
                dataSources = listOf("Geological Survey of India (GSI)", "IMD Shimla", "Himachal State Disaster Management Authority"),
                isSimulated = true,
                recommendedActions = listOf(
                    "Halt vehicular traffic on Chandigarh-Manali Highway (NH-21) between Pandoh and Aut",
                    "Evacuate hamlets situated below hanging boulder cones into Mandi safe shelters",
                    "Position heavy earth-moving equipment and rock breakers at Aut tunnel entrance",
                    "Deploy satellite drone monitoring for potential river damming by landslide debris"
                ),
                reportedAt = "Today, 05:00 IST"
            ),

            // 5. Flooding in Punjab (Scenario 5)
            DisasterIncident(
                id = "DIS_PB_05",
                title = "Sutlej & Beas Basin Dam Discharge Overflow",
                type = DisasterType.FLOOD,
                stateName = "Punjab",
                districtName = "Rupnagar & Firozpur",
                lat = 30.96,
                lon = 76.52,
                severity = RiskLevel.HIGH,
                probability = 0.86f,
                affectedPopulation = 12000,
                metrics = MeteorologicalMetrics(
                    rainfall24hMm = 142.0,
                    riverLevelMeters = 24.10,
                    riverDangerMarkMeters = 22.80,
                    tempCelsius = 31.0,
                    humidityPercent = 88,
                    soilMoisturePercent = 85
                ),
                horizonPredictions = listOf(
                    HorizonPrediction("24 Hours", RiskLevel.HIGH, 0.89f, "Downstream surge reaching Anandpur Sahib and low-lying farm belts"),
                    HorizonPrediction("48 Hours", RiskLevel.HIGH, 0.75f, "Controlled dam release throttling will gradually ease water velocity"),
                    HorizonPrediction("7 Days", RiskLevel.LOW, 0.35f, "Complete drainage through Harike barrage wetland channel")
                ),
                contributors = listOf(
                    DataContributor("Bhakra Beas Management Board (BBMB) Outflow", 45, "65,000 cusecs", "Dam spillway overflow into riverbed"),
                    DataContributor("Catchment Basin Rainfall Volume", 30, "142mm rain", "Continuous feeder stream flooding"),
                    DataContributor("Embankment Integrity Weak Spots", 15, "Old earthen bunds", "Risk of breaches near agricultural lands"),
                    DataContributor("Flat Alluvial Plain Drainage Delay", 10, "Low gradient", "Slow standing water discharge")
                ),
                confidencePercent = 86,
                dataSources = listOf("Bhakra Beas Management Board (BBMB)", "Punjab Irrigation Dept", "IMD Chandigarh"),
                isSimulated = true,
                recommendedActions = listOf(
                    "Erect sandbag reinforcements at identified weak bunds along Anandpur Sahib canal",
                    "Relocate livestock and rural families to high-ground school shelters and Gurudwaras",
                    "Deploy NDRF 7th Battalion with inflatable motorized boats",
                    "Distribute water purification tablets and ORS kits across inundated sub-divisions"
                ),
                reportedAt = "Today, 01:15 IST"
            ),

            // 6. Kerala Landslide Alert (Western Ghats)
            DisasterIncident(
                id = "DIS_KL_06",
                title = "Wayanad-Idukki Slope Failure & Torrential Inundation",
                type = DisasterType.LANDSLIDE,
                stateName = "Kerala",
                districtName = "Wayanad",
                lat = 11.68,
                lon = 76.13,
                severity = RiskLevel.CRITICAL,
                probability = 0.92f,
                affectedPopulation = 9500,
                metrics = MeteorologicalMetrics(
                    rainfall24hMm = 312.0,
                    soilMoisturePercent = 99,
                    tempCelsius = 22.0,
                    humidityPercent = 96
                ),
                horizonPredictions = listOf(
                    HorizonPrediction("24 Hours", RiskLevel.CRITICAL, 0.94f, "Extreme soil saturation triggering slope liquefaction"),
                    HorizonPrediction("48 Hours", RiskLevel.HIGH, 0.81f, "Intermittent showers continue over Western Ghats crest"),
                    HorizonPrediction("7 Days", RiskLevel.MODERATE, 0.40f, "Gradual stabilization of runoff channels")
                ),
                contributors = listOf(
                    DataContributor("Monsoon Orographic Cloud Precipitation", 46, "312mm / 24h", "Record extreme monsoon event"),
                    DataContributor("Deep Laterite Soil Saturation", 32, "99% moisture", "Hydrostatic pressure slope breakdown"),
                    DataContributor("GSI Landslide Susceptibility Hazard Zone", 14, "Very high tier", "High gradient plantation valleys"),
                    DataContributor("Historical Chooralmala Rupture", 8, "Repeat zone", "Geological fault zone activation")
                ),
                confidencePercent = 92,
                dataSources = listOf("IMD Thiruvananthapuram", "Kerala State Disaster Management Authority (KSDMA)", "GSI Regional Centre"),
                isSimulated = true,
                recommendedActions = listOf(
                    "Immediate evacuation of plantation workers to Meppadi community shelter halls",
                    "Air Force ALH helicopters on standby for mountain hamlet medical airlifts",
                    "Strict night curfew on ghat mountain passes (Thamarassey Ghat Road)",
                    "Pre-positioning heavy earthmovers and sniffer dog rescue squads"
                ),
                reportedAt = "Today, 06:10 IST"
            ),

            // 7. Tamil Nadu Coastal Warning
            DisasterIncident(
                id = "DIS_TN_07",
                title = "Coromandel Coast Depressive Surge & Cyclone Alert",
                type = DisasterType.CYCLONE,
                stateName = "Tamil Nadu",
                districtName = "Cuddalore & Nagapattinam",
                lat = 11.75,
                lon = 79.76,
                severity = RiskLevel.HIGH,
                probability = 0.84f,
                affectedPopulation = 14500,
                metrics = MeteorologicalMetrics(
                    rainfall24hMm = 175.0,
                    windSpeedKmh = 95.0,
                    tempCelsius = 27.0,
                    humidityPercent = 90
                ),
                horizonPredictions = listOf(
                    HorizonPrediction("24 Hours", RiskLevel.HIGH, 0.86f, "Gale winds up to 105 km/h along delta coastline"),
                    HorizonPrediction("48 Hours", RiskLevel.MODERATE, 0.65f, "Storm tracks inland weakening over interior Tamil Nadu"),
                    HorizonPrediction("7 Days", RiskLevel.LOW, 0.15f, "Dry easterly winds resume")
                ),
                contributors = listOf(
                    DataContributor("Bay of Bengal Low Pressure Deepening", 40, "994 hPa", "Cyclonic vortex strengthening"),
                    DataContributor("Coastal Lowland Storm Surge Vulnerability", 35, "1.8m surge", "Estuary backwater flooding"),
                    DataContributor("IMD Coastal Radar Chennai", 15, "175 mm rain", "Widespread agricultural waterlogging"),
                    DataContributor("Dense Coastal Fishing Communities", 10, "Vulnerable huts", "Catamaran and boat damage threat")
                ),
                confidencePercent = 84,
                dataSources = listOf("IMD RMC Chennai", "TNSDMA State Emergency Operations Centre", "Coast Guard District HQ No. 5"),
                isSimulated = true,
                recommendedActions = listOf(
                    "Transfer coastal residents to Tsunami & Cyclone Relief Shelters in Cuddalore",
                    "Anchor mechanized fishing craft inside Karaikal & Cuddalore fishing harbors",
                    "Deploy water pumps to drain vulnerable agrarian lowlands in Cauvery delta",
                    "Distribute tarpaulins and dry food packets to Panchayat emergency centers"
                ),
                reportedAt = "Today, 04:45 IST"
            ),

            // 8. Bihar Kosi River Flood
            DisasterIncident(
                id = "DIS_BR_08",
                title = "Kosi River Transboundary Flood Discharge",
                type = DisasterType.FLOOD,
                stateName = "Bihar",
                districtName = "Supaul & Saharsa",
                lat = 26.12,
                lon = 86.60,
                severity = RiskLevel.CRITICAL,
                probability = 0.93f,
                affectedPopulation = 22000,
                metrics = MeteorologicalMetrics(
                    rainfall24hMm = 210.0,
                    riverLevelMeters = 36.40,
                    riverDangerMarkMeters = 34.75,
                    tempCelsius = 29.0,
                    humidityPercent = 92,
                    soilMoisturePercent = 95
                ),
                horizonPredictions = listOf(
                    HorizonPrediction("24 Hours", RiskLevel.CRITICAL, 0.95f, "Spur #14 and embankment under severe hydrostatic pressure"),
                    HorizonPrediction("48 Hours", RiskLevel.HIGH, 0.84f, "Water logging of railway links between Saharsa and Mansi"),
                    HorizonPrediction("7 Days", RiskLevel.MODERATE, 0.58f, "Water level stabilization subject to Nepal upstream weather")
                ),
                contributors = listOf(
                    DataContributor("Birpur Barrage Outflow Surge", 48, "4,62,000 cusecs", "Extreme upstream transboundary discharge"),
                    DataContributor("CWC Danger Mark Inundation", 28, "+1.65m excess", "Embankment seepage risk"),
                    DataContributor("Siltation & River Course Shifting", 14, "Heavy silt load", "Channel braiding and bank erosion"),
                    DataContributor("High Rural Population Density", 10, "Zone 1 floodplain", "Vulnerable thatch settlements")
                ),
                confidencePercent = 93,
                dataSources = listOf("Central Water Commission (CWC)", "Bihar Disaster Management Dept", "Nepal Hydrology Dept"),
                isSimulated = true,
                recommendedActions = listOf(
                    "Round-the-clock patrolling on East & West Kosi flood embankments",
                    "Operate SDRF country boats for village evacuations across Supaul and Madhepura",
                    "Supply halogen water purification kits and snake-bite anti-venom to PHCs",
                    "Prepare railway platforms and highway flanks as elevated safety zones"
                ),
                reportedAt = "Today, 03:00 IST"
            ),

            // 9. Gujarat Coastal & Seismic Alert
            DisasterIncident(
                id = "DIS_GJ_09",
                title = "Kutch Seismic Tremor & Coastal Vulnerability",
                type = DisasterType.EARTHQUAKE,
                stateName = "Gujarat",
                districtName = "Kutch (Bhuj-Anjar)",
                lat = 23.24,
                lon = 69.66,
                severity = RiskLevel.MODERATE,
                probability = 0.68f,
                affectedPopulation = 4200,
                metrics = MeteorologicalMetrics(
                    rainfall24hMm = 4.0,
                    seismicMagnitude = 5.2,
                    tempCelsius = 34.0,
                    humidityPercent = 55
                ),
                horizonPredictions = listOf(
                    HorizonPrediction("24 Hours", RiskLevel.MODERATE, 0.70f, "Micro-tremors localized along Kutch Mainland Fault (KMF)"),
                    HorizonPrediction("48 Hours", RiskLevel.LOW, 0.40f, "Tectonic stress redistribution without structural collapse"),
                    HorizonPrediction("7 Days", RiskLevel.LOW, 0.22f, "Stable background seismic baseline")
                ),
                contributors = listOf(
                    DataContributor("Institute of Seismological Research (ISR) Sensor", 52, "5.2 Richter / 18km depth", "Intermediate shallow crustal focus"),
                    DataContributor("Seismic Zone V Classification", 28, "Zone V", "High historical earthquake risk (2001 Bhuj)"),
                    DataContributor("Industrial Port Proximity", 12, "Kandla Port nearby", "Maritime chemical infrastructure surveillance"),
                    DataContributor("Soil Liquefaction Susceptibility", 8, "Rann salt pan edges", "Ground settlement risk")
                ),
                confidencePercent = 82,
                dataSources = listOf("Institute of Seismological Research (ISR) Gandhinagar", "GSDMA", "National Seismological Network"),
                isSimulated = true,
                recommendedActions = listOf(
                    "Activate GSDMA emergency response teams at Bhuj and Gandhidham",
                    "Structural integrity audit of high-capacity water reservoirs and oil pipelines",
                    "Public announcement on open ground congregation drills and safe zones",
                    "Standby check of civil defense wireless radio network"
                ),
                reportedAt = "Today, 06:40 IST"
            )
        )
    }

    fun getInitialShelters(): List<Shelter> {
        return listOf(
            // Assam Shelters (for Disaster DIS_ASSAM_01)
            Shelter(
                id = "SH_AS_01",
                name = "Barpeta Govt College Relief Campus",
                stateName = "Assam",
                districtName = "Barpeta",
                lat = 26.34,
                lon = 91.03,
                totalCapacity = 4500,
                currentOccupancy = 1200,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 12,
                    oxygenCylindersCount = 45,
                    firstAidKitsCount = 200,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 14,
                    drinkingWaterLiters = 35000,
                    powerBackupHours = 72,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 48
                ),
                contactPerson = "Dr. Manas Bora (Chief Medical Officer)",
                phone = "+91 94350-12849",
                elevationMeters = 54.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_AS_02",
                name = "Morigaon District Sports Complex",
                stateName = "Assam",
                districtName = "Morigaon",
                lat = 26.25,
                lon = 92.34,
                totalCapacity = 5200,
                currentOccupancy = 1800,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 8,
                    oxygenCylindersCount = 30,
                    firstAidKitsCount = 180,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 12,
                    drinkingWaterLiters = 42000,
                    powerBackupHours = 60,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 55
                ),
                contactPerson = "Smt. Pranati Saikia (Relief Superintendent)",
                phone = "+91 94351-87201",
                elevationMeters = 52.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_AS_03",
                name = "Guwahati University Flood Haven",
                stateName = "Assam",
                districtName = "Kamrup Metropolitan",
                lat = 26.15,
                lon = 91.66,
                totalCapacity = 6000,
                currentOccupancy = 950,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 20,
                    oxygenCylindersCount = 65,
                    firstAidKitsCount = 350,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 21,
                    drinkingWaterLiters = 60000,
                    powerBackupHours = 96,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 80
                ),
                contactPerson = "Prof. J. Goswami (Disaster Cell Head)",
                phone = "+91 94352-44119",
                elevationMeters = 68.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_AS_04",
                name = "Dhubri Higher Secondary School Shelter",
                stateName = "Assam",
                districtName = "Dhubri",
                lat = 26.02,
                lon = 89.98,
                totalCapacity = 3000,
                currentOccupancy = 2750,
                status = ShelterStatus.NEAR_FULL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 4,
                    oxygenCylindersCount = 18,
                    firstAidKitsCount = 90,
                    ambulanceAssigned = false
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 5,
                    drinkingWaterLiters = 14000,
                    powerBackupHours = 24,
                    isDisabilityAccessible = false,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 24
                ),
                contactPerson = "Shri R. Ahmed (Headmaster & Camp In-charge)",
                phone = "+91 94355-66778",
                elevationMeters = 38.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_AS_05",
                name = "Nagaon Polytech Emergency Shelter",
                stateName = "Assam",
                districtName = "Nagaon",
                lat = 26.35,
                lon = 92.68,
                totalCapacity = 4000,
                currentOccupancy = 800,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 6,
                    oxygenCylindersCount = 25,
                    firstAidKitsCount = 150,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 10,
                    drinkingWaterLiters = 28000,
                    powerBackupHours = 48,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 36
                ),
                contactPerson = "Er. A. K. Deka (Camp Coordinator)",
                phone = "+91 94354-99882",
                elevationMeters = 61.0,
                isFloodSafeZone = true
            ),

            // Odisha Shelters (for Disaster DIS_ODISHA_02)
            Shelter(
                id = "SH_OD_01",
                name = "Puri Multi-Purpose Cyclone Shelter (ODRAF #12)",
                stateName = "Odisha",
                districtName = "Puri",
                lat = 19.83,
                lon = 85.85,
                totalCapacity = 6500,
                currentOccupancy = 1500,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 16,
                    oxygenCylindersCount = 50,
                    firstAidKitsCount = 300,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 18,
                    drinkingWaterLiters = 55000,
                    powerBackupHours = 84,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 70
                ),
                contactPerson = "Capt. B. K. Mohapatra (ODRAF Commander)",
                phone = "+91 94370-55441",
                elevationMeters = 18.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_OD_02",
                name = "Kendrapara High School Reinforced Shelter",
                stateName = "Odisha",
                districtName = "Kendrapara",
                lat = 20.50,
                lon = 86.42,
                totalCapacity = 5000,
                currentOccupancy = 2100,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 10,
                    oxygenCylindersCount = 35,
                    firstAidKitsCount = 220,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 14,
                    drinkingWaterLiters = 40000,
                    powerBackupHours = 72,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 50
                ),
                contactPerson = "Smt. Ananya Rout (District Relief Officer)",
                phone = "+91 94371-88992",
                elevationMeters = 15.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_OD_03",
                name = "Bhubaneswar Kalinga Stadium Mega Relief Base",
                stateName = "Odisha",
                districtName = "Khordha (Bhubaneswar)",
                lat = 20.29,
                lon = 85.82,
                totalCapacity = 10000,
                currentOccupancy = 2400,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 35,
                    oxygenCylindersCount = 120,
                    firstAidKitsCount = 600,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 25,
                    drinkingWaterLiters = 120000,
                    powerBackupHours = 120,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 140
                ),
                contactPerson = "Dr. S. K. Das (Director Health Operations)",
                phone = "+91 94372-11003",
                elevationMeters = 45.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_OD_04",
                name = "Jagatsinghpur Coastal Relief Hub",
                stateName = "Odisha",
                districtName = "Jagatsinghpur",
                lat = 20.26,
                lon = 86.16,
                totalCapacity = 4500,
                currentOccupancy = 3900,
                status = ShelterStatus.NEAR_FULL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 5,
                    oxygenCylindersCount = 20,
                    firstAidKitsCount = 120,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 7,
                    drinkingWaterLiters = 25000,
                    powerBackupHours = 48,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 40
                ),
                contactPerson = "Shri P. Parida (Sub-Divisional Magistrate)",
                phone = "+91 94373-77665",
                elevationMeters = 12.0,
                isFloodSafeZone = true
            ),

            // Uttarakhand Shelters (for Disaster DIS_UK_03)
            Shelter(
                id = "SH_UK_01",
                name = "Gopeshwar District Sports Stadium",
                stateName = "Uttarakhand",
                districtName = "Chamoli (Gopeshwar)",
                lat = 30.41,
                lon = 79.32,
                totalCapacity = 3500,
                currentOccupancy = 900,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 14,
                    oxygenCylindersCount = 40,
                    firstAidKitsCount = 250,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 16,
                    drinkingWaterLiters = 30000,
                    powerBackupHours = 72,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 45
                ),
                contactPerson = "Col. R. S. Negi (Ex-Army Camp Incharge)",
                phone = "+91 94120-33211",
                elevationMeters = 1450.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_UK_02",
                name = "Joshimath Cantt Open Ground Relief Center",
                stateName = "Uttarakhand",
                districtName = "Chamoli (Joshimath)",
                lat = 30.55,
                lon = 79.56,
                totalCapacity = 2800,
                currentOccupancy = 1600,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 8,
                    oxygenCylindersCount = 28,
                    firstAidKitsCount = 190,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 10,
                    drinkingWaterLiters = 22000,
                    powerBackupHours = 48,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 32
                ),
                contactPerson = "Maj. V. Rawat (SDRF Base Commander)",
                phone = "+91 94121-77889",
                elevationMeters = 1890.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_UK_03",
                name = "Rudraprayag Govt Inter College",
                stateName = "Uttarakhand",
                districtName = "Rudraprayag",
                lat = 30.28,
                lon = 78.98,
                totalCapacity = 3200,
                currentOccupancy = 650,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 10,
                    oxygenCylindersCount = 30,
                    firstAidKitsCount = 210,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 14,
                    drinkingWaterLiters = 28000,
                    powerBackupHours = 60,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 40
                ),
                contactPerson = "Dr. Hema Bisht (District Medical Officer)",
                phone = "+91 94122-66554",
                elevationMeters = 890.0,
                isFloodSafeZone = true
            ),

            // Himachal Pradesh Shelters (for Disaster DIS_HP_04)
            Shelter(
                id = "SH_HP_01",
                name = "Mandi Vallabh Degree College Camp",
                stateName = "Himachal Pradesh",
                districtName = "Mandi",
                lat = 31.71,
                lon = 76.92,
                totalCapacity = 3000,
                currentOccupancy = 850,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 8,
                    oxygenCylindersCount = 25,
                    firstAidKitsCount = 160,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 15,
                    drinkingWaterLiters = 26000,
                    powerBackupHours = 60,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 35
                ),
                contactPerson = "Shri Vikram Thakur (Relief Commissioner)",
                phone = "+91 94180-22334",
                elevationMeters = 760.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_HP_02",
                name = "Kullu Dhalpur Ground Emergency Center",
                stateName = "Himachal Pradesh",
                districtName = "Kullu",
                lat = 31.95,
                lon = 77.10,
                totalCapacity = 2800,
                currentOccupancy = 1100,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 6,
                    oxygenCylindersCount = 22,
                    firstAidKitsCount = 140,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 12,
                    drinkingWaterLiters = 24000,
                    powerBackupHours = 48,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 30
                ),
                contactPerson = "Smt. Shailja Sharma (Red Cross Lead)",
                phone = "+91 94181-44556",
                elevationMeters = 1220.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_HP_03",
                name = "Sundernagar Community Hall Shelter",
                stateName = "Himachal Pradesh",
                districtName = "Mandi",
                lat = 31.53,
                lon = 76.89,
                totalCapacity = 2200,
                currentOccupancy = 400,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 4,
                    oxygenCylindersCount = 16,
                    firstAidKitsCount = 110,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 14,
                    drinkingWaterLiters = 20000,
                    powerBackupHours = 48,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 28
                ),
                contactPerson = "Er. N. K. Sen (Civil Defense Officer)",
                phone = "+91 94182-99001",
                elevationMeters = 850.0,
                isFloodSafeZone = true
            ),

            // Punjab Shelters (for Disaster DIS_PB_05)
            Shelter(
                id = "SH_PB_01",
                name = "Rupnagar Govt College Disaster Shelter",
                stateName = "Punjab",
                districtName = "Rupnagar",
                lat = 30.98,
                lon = 76.53,
                totalCapacity = 4200,
                currentOccupancy = 1100,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 10,
                    oxygenCylindersCount = 32,
                    firstAidKitsCount = 200,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 16,
                    drinkingWaterLiters = 38000,
                    powerBackupHours = 72,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 50
                ),
                contactPerson = "S. Gurpreet Singh (SDM Relief)",
                phone = "+91 98140-55667",
                elevationMeters = 260.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_PB_02",
                name = "Anandpur Sahib Mega Gurudwara Complex",
                stateName = "Punjab",
                districtName = "Rupnagar (Anandpur Sahib)",
                lat = 31.23,
                lon = 76.50,
                totalCapacity = 7500,
                currentOccupancy = 2100,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 15,
                    oxygenCylindersCount = 45,
                    firstAidKitsCount = 280,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 30,
                    drinkingWaterLiters = 80000,
                    powerBackupHours = 96,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 90
                ),
                contactPerson = "Jathedar Kulwant Singh (Langars & Relief)",
                phone = "+91 98141-88990",
                elevationMeters = 310.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_PB_03",
                name = "Firozpur Cantt Indoor Stadium",
                stateName = "Punjab",
                districtName = "Firozpur",
                lat = 30.92,
                lon = 74.61,
                totalCapacity = 4000,
                currentOccupancy = 900,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 8,
                    oxygenCylindersCount = 24,
                    firstAidKitsCount = 175,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 14,
                    drinkingWaterLiters = 34000,
                    powerBackupHours = 60,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 45
                ),
                contactPerson = "Dr. Harpreet Kaur (Civil Surgeon)",
                phone = "+91 98142-33445",
                elevationMeters = 198.0,
                isFloodSafeZone = true
            ),

            // Additional Pan-India Shelters (Kerala, Delhi, Mumbai, Kolkata)
            Shelter(
                id = "SH_KL_01",
                name = "Meppadi Community Center & St. Joseph Hall",
                stateName = "Kerala",
                districtName = "Wayanad (Meppadi)",
                lat = 11.55,
                lon = 76.12,
                totalCapacity = 3800,
                currentOccupancy = 1200,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 12,
                    oxygenCylindersCount = 36,
                    firstAidKitsCount = 220,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 16,
                    drinkingWaterLiters = 32000,
                    powerBackupHours = 72,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 48
                ),
                contactPerson = "Dr. Thomas Mathew (KSDMA Liaison)",
                phone = "+91 94470-11223",
                elevationMeters = 920.0,
                isFloodSafeZone = true
            ),
            Shelter(
                id = "SH_DL_01",
                name = "Thyagaraj Stadium Emergency Shelter",
                stateName = "Delhi",
                districtName = "South Delhi",
                lat = 28.57,
                lon = 77.21,
                totalCapacity = 8000,
                currentOccupancy = 1400,
                status = ShelterStatus.OPERATIONAL,
                medical = MedicalSupport(
                    hasDoctorOnSite = true,
                    icuBedsCount = 25,
                    oxygenCylindersCount = 80,
                    firstAidKitsCount = 400,
                    ambulanceAssigned = true
                ),
                supplies = ShelterSupplies(
                    foodDaysRemaining = 20,
                    drinkingWaterLiters = 90000,
                    powerBackupHours = 120,
                    isDisabilityAccessible = true,
                    hasWomenChildSafeZone = true,
                    sanitationUnits = 100
                ),
                contactPerson = "Shri R. K. Sharma (DDMA Controller)",
                phone = "+91 98100-99887",
                elevationMeters = 215.0,
                isFloodSafeZone = true
            )
        )
    }
}
