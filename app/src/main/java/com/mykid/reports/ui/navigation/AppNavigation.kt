package com.mykid.reports

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.mykid.reports.data.localization.LocalizationManager
import com.mykid.reports.ui.components.ThemeToggleButton
import com.mykid.reports.ui.screens.analysis.AnalysisScreen
import com.mykid.reports.ui.screens.dashboard.DashboardScreen
import com.mykid.reports.ui.screens.lessons.LessonsScreen
import com.mykid.reports.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch


@Composable
fun ThemeToggleButton(isDarkTheme: Boolean, onToggle: () -> Unit) {
    Button(onClick = onToggle) {
        Text(if (isDarkTheme) "Switch to Light Theme" else "Switch to Dark Theme")
    }
}

sealed class Screen(val route: String, val icon: ImageVector, val labelKey: String) {
    object Dashboard : Screen("dashboard", Icons.Default.Dashboard, "Dash Board")
    object Lessons : Screen("lessons", Icons.Default.Book, "lessons")
    object Analysis : Screen("analysis", Icons.Default.Analytics, "analysis")
    object Settings : Screen("settings", Icons.Default.Settings, "settings")

    fun getLocalizedLabel(): String = LocalizationManager.getString(labelKey)

    companion object {
        fun fromRoute(route: String?): Screen {
            return when (route) {
                Dashboard.route -> Dashboard
                Lessons.route -> Lessons
                Analysis.route -> Analysis
                Settings.route -> Settings
                else -> Dashboard
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Dashboard.route,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val locale by LocalizationManager.currentLocale.collectAsState()
    val screens = remember(locale) {
        listOf(Screen.Dashboard, Screen.Lessons, Screen.Analysis, Screen.Settings)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Spacer(Modifier.height(12.dp))
                        screens.forEach { screen ->
                            NavigationDrawerItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { Text(screen.getLocalizedLabel()) },
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Dashboard.route) { saveState = true }
                                        launchSingleTop = true
                                    }
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                    ThemeToggleButton(
                        isDarkTheme = isDarkTheme,
                        onToggle = { onThemeChange(!isDarkTheme) },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Topbar {
                    scope.launch { drawerState.open() }
                }
            },
            content = { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                            )
                        }
                        composable(Screen.Lessons.route) {
                            LessonsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Analysis.route) {
                            AnalysisScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Topbar(openDrawer: () -> Unit) {
    TopAppBar(
        title = { Text("Reports") },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(Icons.Default.Menu, contentDescription = "Open drawer")
            }
        }
    )
}
