package com.mykid.reports.ui.navigation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mykid.reports.ui.components.ThemeToggleButton
import com.mykid.reports.ui.screens.analysis.AnalysisScreen
import com.mykid.reports.ui.screens.dashboard.DashboardScreen
import com.mykid.reports.ui.screens.lessons.LessonsScreen
import com.mykid.reports.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch
import com.mykid.reports.data.localization.LocalizationManager

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
                                label = { Text(LocalizationManager.getString(screen.labelKey)) },
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

                    // 🔹 Theme toggle updates correctly
                    ThemeToggleButton(
                        isDarkTheme = isDarkTheme,
                        onToggle = { onThemeChange(!isDarkTheme) },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    ) {
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

