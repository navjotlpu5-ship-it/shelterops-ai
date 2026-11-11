package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TopCommandBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppNavTab
import com.example.viewmodel.ShelterOpsViewModel

@Composable
fun ShelterOpsMainApp(
    viewModel: ShelterOpsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle back button: return to Overview if on a secondary screen
    BackHandler(enabled = uiState.currentTab != AppNavTab.OVERVIEW) {
        viewModel.setNavTab(AppNavTab.OVERVIEW)
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(CommandDarkBg)) {
        val isWideScreen = maxWidth >= 640.dp

        if (isWideScreen) {
            // Adaptive Tablet / Desktop Layout: Navigation Rail on Left
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = CommandSurface,
                    contentColor = TextPrimary,
                    modifier = Modifier.fillMaxHeight().testTag("app_navigation_rail")
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AppNavTab.values().forEach { tab ->
                        val isSelected = uiState.currentTab == tab
                        val icon = getTabIcon(tab)
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { viewModel.setNavTab(tab) },
                            icon = { Icon(icon, contentDescription = tab.title) },
                            label = { Text(tab.title, fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = CyberCyan,
                                indicatorColor = CyberCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }

                // Main Content Column
                Column(modifier = Modifier.fillMaxSize()) {
                    TopCommandBar(
                        activeScenarioKey = uiState.activeScenarioKey,
                        onScenarioSelected = { viewModel.loadScenario(it) }
                    )

                    Box(modifier = Modifier.fillMaxSize()) {
                        AppScreenContent(uiState = uiState, viewModel = viewModel)
                    }
                }
            }
        } else {
            // Mobile Layout: TopBar + Screen Content + M3 Bottom Navigation Bar
            Scaffold(
                topBar = {
                    TopCommandBar(
                        activeScenarioKey = uiState.activeScenarioKey,
                        onScenarioSelected = { viewModel.loadScenario(it) },
                        modifier = Modifier.statusBarsPadding()
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = CommandSurface,
                        contentColor = TextPrimary,
                        modifier = Modifier.navigationBarsPadding().testTag("app_bottom_nav_bar")
                    ) {
                        AppNavTab.values().take(5).forEach { tab ->
                            val isSelected = uiState.currentTab == tab
                            val icon = getTabIcon(tab)
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.setNavTab(tab) },
                                icon = { Icon(icon, contentDescription = tab.title, modifier = Modifier.size(20.dp)) },
                                label = { Text(tab.title, fontSize = 10.sp, maxLines = 1) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = CyberCyan,
                                    indicatorColor = CyberCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_item_${tab.name}")
                            )
                        }
                    }
                },
                containerColor = CommandDarkBg,
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    AppScreenContent(uiState = uiState, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun AppScreenContent(
    uiState: com.example.viewmodel.ShelterOpsUiState,
    viewModel: ShelterOpsViewModel
) {
    when (uiState.currentTab) {
        AppNavTab.OVERVIEW -> {
            CommandOverviewScreen(
                uiState = uiState,
                onNavigateTab = { viewModel.setNavTab(it) },
                onSelectDisaster = { viewModel.selectDisaster(it) },
                onSelectShelter = { viewModel.selectShelter(it) },
                onSelectRegion = { viewModel.selectRegion(it) },
                onRunAllocation = { viewModel.runAllocationEngine() }
            )
        }
        AppNavTab.INDIA_MAP -> {
            IndiaMapScreen(
                uiState = uiState,
                onSelectDisaster = { viewModel.selectDisaster(it) },
                onSelectShelter = { viewModel.selectShelter(it) },
                onSelectRegion = { viewModel.selectRegion(it) },
                onClearShelterSelection = { viewModel.selectShelter(null) },
                onNavigateTab = { viewModel.setNavTab(it) }
            )
        }
        AppNavTab.AI_PREDICTIONS -> {
            AIPredictionsScreen(
                uiState = uiState,
                onSelectDisaster = { viewModel.selectDisaster(it) },
                onSyncLiveWeather = { viewModel.syncLiveWeather() }
            )
        }
        AppNavTab.SMART_ALLOCATION -> {
            SmartAllocationScreen(
                uiState = uiState,
                onUpdatePopulation = { viewModel.updateSimulatedPopulation(it) },
                onUpdateGoal = { viewModel.updateOptimizationGoal(it) },
                onRunAllocation = { viewModel.runAllocationEngine() },
                onSelectShelter = { viewModel.selectShelter(it) }
            )
        }
        AppNavTab.EVACUATION_ROUTES -> {
            EvacuationRoutesScreen(uiState = uiState)
        }
        AppNavTab.MULTI_AGENT -> {
            MultiAgentScreen(uiState = uiState)
        }
        AppNavTab.AI_REASONING -> {
            AIReasoningScreen()
        }
    }
}

private fun getTabIcon(tab: AppNavTab): androidx.compose.ui.graphics.vector.ImageVector {
    return when (tab) {
        AppNavTab.OVERVIEW -> Icons.Default.Dashboard
        AppNavTab.INDIA_MAP -> Icons.Default.Map
        AppNavTab.AI_PREDICTIONS -> Icons.Default.Radar
        AppNavTab.SMART_ALLOCATION -> Icons.Default.Bolt
        AppNavTab.EVACUATION_ROUTES -> Icons.Default.AltRoute
        AppNavTab.MULTI_AGENT -> Icons.Default.Groups
        AppNavTab.AI_REASONING -> Icons.Default.Psychology
    }
}
