package com.example.threething.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.threething.data.readTasks
import com.example.threething.data.userPreferencesDataStore
import com.example.threething.notification.TaskForegroundService
import com.example.threething.notification.WidgetPickerNotification
import com.example.threething.ui.navigation.AppNavHost
import com.example.threething.ui.theme.ThreeThingTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(application, userPreferencesDataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            ThreeThingTheme {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    taskViewModel = taskViewModel
                )
            }
        }

        // Delayed notification start after splash screen and UI set up
        lifecycleScope.launch {
            delay(4000L) // 4 seconds delay (adjust if you want)
            checkAndRequestNotificationPermission()
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            } else {
                startForegroundTaskNotification()
            }
        } else {
            startForegroundTaskNotification()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startForegroundTaskNotification()
        }
    }

    private fun startForegroundTaskNotification() {
        lifecycleScope.launch {
            val prefs = readTasks(userPreferencesDataStore).first()
            val task1Text = prefs.task1.text
            val task2Text = prefs.task2.text
            val task3Text = prefs.task3.text

            val intent = Intent(this@MainActivity, TaskForegroundService::class.java).apply {
                putExtra("task1", task1Text)
                putExtra("task2", task2Text)
                putExtra("task3", task3Text)
            }
            ContextCompat.startForegroundService(this@MainActivity, intent)

            // Show the widget picker notification prompting user to add the widget
            WidgetPickerNotification.showWidgetPickerNotification(this@MainActivity)

        }
    }
}
