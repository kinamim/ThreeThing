package com.example.threething.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.threething.ui.splash.SplashScreen
import com.example.threething.ui.DailyFocusScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object DailyFocus : Screen("daily_focus")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateNext = {
                navController.navigate(Screen.DailyFocus.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.DailyFocus.route) {
            DailyFocusScreen()
        }
    }
}
