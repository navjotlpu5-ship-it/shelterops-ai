package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DisasterRepository
import com.example.data.IndiaGeoDataProvider
import com.example.engine.DisasterPredictionEngine
import com.example.engine.MultiAgentCoordinator
import com.example.engine.ShelterAllocationEngine
import com.example.model.*
import com.example.network.weather.LiveWeatherData
import com.example.network.weather.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String, val iconResName: String) {
    OVERVIEW("Overview", "ic_dashboard"),
    INDIA_MAP("India Map", "ic_map"),
    AI_PREDICTIONS("AI Predictions", "ic_radar"),
    SMART_ALLOCATION("Smart Allocation", "ic_allocation"),
    EVACUATION_ROUTES("Routes", "ic_routes"),
    MULTI_AGENT("Agent AI", "ic_agents"),
    AI_REASONING("AI Reasoning", "ic_brain")
}

data class ShelterOpsUiState(
    val currentTab: AppNavTab = AppNavTab.OVERVIEW,
    val disasters: List<DisasterIncident> = emptyList(),
    val shelters: List<Shelter> = emptyList(),
    val regions: List<IndiaRegion> = emptyList(),
    val selectedDisaster: DisasterIncident? = null,
    val selectedShelter: Shelter? = null,
    val selectedRegion: IndiaRegion? = null,
    val allocationPlan: AllocationPlan? = null,
    val agents: List<AIAgentInfo> = emptyList(),
    val agentMessages: List<AgentMessage> = emptyList(),
    val actionPlan: CoordinatedActionPlan? = null,
    val isSolvingAllocation: Boolean = false,
    val searchQuery: String = "",
    val disasterTypeFilter: DisasterType? = null,
    val riskFilter: RiskLevel? = null,
    // Real-Time Weather Integration
    val isSyncingWeather: Boolean = false,
    val lastWeatherReport: LiveWeatherData? = null,
    // Simulation controls
    val activeScenarioKey: String = "ASSAM_FLOOD",
    val simulatedPopulation: Int = 18500,
    val simulationGoal: OptimizationGoal = OptimizationGoal.PRIORITIZE_VULNERABLE,
    val isBlockedRoadSimulation: Boolean = false
)

class ShelterOpsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ShelterOpsUiState())
    val uiState: StateFlow<ShelterOpsUiState> = _uiState.asStateFlow()

    init {
        initializeData()
    }

    private fun initializeData() {
        val initialDisasters = DisasterRepository.getInitialDisasters()
        val initialShelters = DisasterRepository.getInitialShelters()
        val regions = IndiaGeoDataProvider.allRegions
        val defaultDisaster = initialDisasters.first()

        val plan = ShelterAllocationEngine.solveAllocation(
            incident = defaultDisaster,
            allShelters = initialShelters,
            displacedCountOverride = defaultDisaster.affectedPopulation,
            goal = OptimizationGoal.PRIORITIZE_VULNERABLE
        )

        val agentTeam = MultiAgentCoordinator.generateAgentTeam(defaultDisaster, plan)
        val messages = MultiAgentCoordinator.generateInterAgentMessages(defaultDisaster, plan)
        val actionPlan = MultiAgentCoordinator.synthesizeActionPlan(defaultDisaster, plan)

        _uiState.value = ShelterOpsUiState(
            disasters = initialDisasters,
            shelters = initialShelters,
            regions = regions,
            selectedDisaster = defaultDisaster,
            selectedRegion = regions.find { it.name.equals(defaultDisaster.stateName, ignoreCase = true) },
            allocationPlan = plan,
            agents = agentTeam,
            agentMessages = messages,
            actionPlan = actionPlan,
            simulatedPopulation = defaultDisaster.affectedPopulation
        )

        // Automatically fetch live real-time weather feed on launch
        syncLiveWeather(defaultDisaster)
    }

    fun setNavTab(tab: AppNavTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectDisaster(incident: DisasterIncident) {
        val currentShelters = _uiState.value.shelters
        val plan = ShelterAllocationEngine.solveAllocation(
            incident = incident,
            allShelters = currentShelters,
            displacedCountOverride = incident.affectedPopulation,
            goal = _uiState.value.simulationGoal
        )
        val agentTeam = MultiAgentCoordinator.generateAgentTeam(incident, plan)
        val messages = MultiAgentCoordinator.generateInterAgentMessages(incident, plan)
        val actionPlan = MultiAgentCoordinator.synthesizeActionPlan(incident, plan)

        _uiState.update {
            it.copy(
                selectedDisaster = incident,
                selectedShelter = null,
                selectedRegion = it.regions.find { r -> r.name.equals(incident.stateName, ignoreCase = true) },
                allocationPlan = plan,
                agents = agentTeam,
                agentMessages = messages,
                actionPlan = actionPlan,
                simulatedPopulation = incident.affectedPopulation
            )
        }

        // Fetch live weather feed for newly selected disaster location
        syncLiveWeather(incident)
    }

    fun syncLiveWeather(targetIncident: DisasterIncident? = null) {
        val incident = targetIncident ?: _uiState.value.selectedDisaster ?: return
        _uiState.update { it.copy(isSyncingWeather = true) }

        viewModelScope.launch {
            val result = WeatherRepository.fetchLiveWeather(
                lat = incident.lat,
                lon = incident.lon,
                districtName = incident.districtName
            )

            result.onSuccess { weatherData ->
                // Integrate real-time weather metrics into MeteorologicalMetrics
                val updatedMetrics = incident.metrics.copy(
                    tempCelsius = weatherData.temperatureCelsius,
                    humidityPercent = weatherData.relativeHumidityPercent,
                    rainfall24hMm = if (weatherData.rainfallMm > 0.0) weatherData.rainfallMm else incident.metrics.rainfall24hMm,
                    windSpeedKmh = if (weatherData.windSpeedKmh > 0.0) weatherData.windSpeedKmh else incident.metrics.windSpeedKmh,
                    pressureHpa = weatherData.surfacePressureHpa,
                    weatherConditionText = weatherData.conditionDescription,
                    lastSyncedAt = weatherData.fetchedAt,
                    isLiveTelemetry = weatherData.isLive
                )

                val updatedIncident = incident.copy(
                    metrics = updatedMetrics,
                    dataSources = if (incident.dataSources.none { it.contains("Open-Meteo") }) {
                        incident.dataSources + listOf(weatherData.dataSource)
                    } else incident.dataSources
                )

                // Re-evaluate prediction engine with live weather inputs
                val prediction = DisasterPredictionEngine.evaluateIncident(updatedIncident)
                val finalIncident = updatedIncident.copy(
                    probability = prediction.probabilityScore,
                    confidencePercent = prediction.confidencePercent,
                    severity = prediction.overallRiskLevel,
                    horizonPredictions = prediction.horizons,
                    contributors = prediction.contributors
                )

                // Update disaster in disasters list
                val updatedList = _uiState.value.disasters.map {
                    if (it.id == finalIncident.id) finalIncident else it
                }

                // Re-calculate allocation and agents with updated telemetry
                val currentShelters = _uiState.value.shelters
                val plan = ShelterAllocationEngine.solveAllocation(
                    incident = finalIncident,
                    allShelters = currentShelters,
                    displacedCountOverride = _uiState.value.simulatedPopulation,
                    goal = _uiState.value.simulationGoal
                )
                val agentTeam = MultiAgentCoordinator.generateAgentTeam(finalIncident, plan)
                val actionPlan = MultiAgentCoordinator.synthesizeActionPlan(finalIncident, plan)

                _uiState.update {
                    it.copy(
                        disasters = updatedList,
                        selectedDisaster = finalIncident,
                        allocationPlan = plan,
                        agents = agentTeam,
                        actionPlan = actionPlan,
                        isSyncingWeather = false,
                        lastWeatherReport = weatherData
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isSyncingWeather = false) }
            }
        }
    }

    fun selectShelter(shelter: Shelter?) {
        _uiState.update { it.copy(selectedShelter = shelter) }
    }

    fun selectRegion(region: IndiaRegion?) {
        _uiState.update {
            val matchingDisaster = it.disasters.find { d -> d.stateName.equals(region?.name, ignoreCase = true) }
            it.copy(
                selectedRegion = region,
                selectedDisaster = matchingDisaster ?: it.selectedDisaster
            )
        }
    }

    fun loadScenario(scenarioKey: String) {
        val targetId = when (scenarioKey) {
            "ASSAM_FLOOD" -> "DIS_ASSAM_01"
            "ODISHA_CYCLONE" -> "DIS_ODISHA_02"
            "UK_EARTHQUAKE" -> "DIS_UK_03"
            "HP_LANDSLIDE" -> "DIS_HP_04"
            "PUNJAB_FLOOD" -> "DIS_PB_05"
            else -> "DIS_ASSAM_01"
        }

        val incident = _uiState.value.disasters.find { it.id == targetId } ?: return
        _uiState.update { it.copy(activeScenarioKey = scenarioKey) }
        selectDisaster(incident)
    }

    fun updateSimulatedPopulation(count: Int) {
        _uiState.update { it.copy(simulatedPopulation = count) }
    }

    fun updateOptimizationGoal(goal: OptimizationGoal) {
        _uiState.update { it.copy(simulationGoal = goal) }
    }

    fun runAllocationEngine() {
        val incident = _uiState.value.selectedDisaster ?: return
        val shelters = _uiState.value.shelters
        val count = _uiState.value.simulatedPopulation
        val goal = _uiState.value.simulationGoal

        _uiState.update { it.copy(isSolvingAllocation = true) }

        viewModelScope.launch {
            delay(400)
            val updatedPlan = ShelterAllocationEngine.solveAllocation(
                incident = incident,
                allShelters = shelters,
                displacedCountOverride = count,
                goal = goal
            )
            val updatedAgents = MultiAgentCoordinator.generateAgentTeam(incident, updatedPlan)
            val updatedMsgs = MultiAgentCoordinator.generateInterAgentMessages(incident, updatedPlan)
            val updatedActionPlan = MultiAgentCoordinator.synthesizeActionPlan(incident, updatedPlan)

            _uiState.update {
                it.copy(
                    allocationPlan = updatedPlan,
                    agents = updatedAgents,
                    agentMessages = updatedMsgs,
                    actionPlan = updatedActionPlan,
                    isSolvingAllocation = false
                )
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setDisasterTypeFilter(type: DisasterType?) {
        _uiState.update { it.copy(disasterTypeFilter = type) }
    }

    fun setRiskFilter(risk: RiskLevel?) {
        _uiState.update { it.copy(riskFilter = risk) }
    }
}
