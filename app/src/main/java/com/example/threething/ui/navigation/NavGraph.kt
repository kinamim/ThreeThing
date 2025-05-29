package com.example.threething.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.threething.ui.DailyFocusScreen
import com.example.threething.ui.main.TaskViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    taskViewModel: TaskViewModel
) {
    NavHost(navController = navController, startDestination = "daily_focus_screen") {
        composable("daily_focus_screen") {
            DailyFocusScreen(taskViewModel = taskViewModel)
        }
    }
}
