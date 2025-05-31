package com.example.threething.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.threething.ui.DailyFocusScreen
import com.example.threething.ui.main.TaskViewModel
import com.example.threething.ui.main.WidgetAddScreen
import com.example.threething.ui.splash.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    taskViewModel: TaskViewModel
) {
    NavHost(navController = navController, startDestination = "splash_screen") {

        composable("splash_screen") {
            SplashScreen(
                onNavigateNext = {
                    navController.navigate("daily_focus_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                },
                onPostSplashAction = {
                    // Use the event-driven start notification approach
                    taskViewModel.triggerStartNotification()
                }
            )
        }

        composable("daily_focus_screen") {
            DailyFocusScreen(
                taskViewModel = taskViewModel,
                startNotification = { /* your start notification lambda here */ }
            )
        }

        composable("widget_add_screen") {
            WidgetAddScreen()
        }
    }
}
