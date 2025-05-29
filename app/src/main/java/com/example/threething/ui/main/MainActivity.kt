package com.example.threething.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.threething.data.readTasks
import com.example.threething.notification.TaskForegroundService
import com.example.threething.ui.navigation.NavGraph
import com.example.threething.ui.theme.ThreeThingTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import com.example.threething.data.userPreferencesDataStore
import com.example.threething.ui.main.TaskViewModel
import com.example.threething.ui.main.TaskViewModelFactory



class MainActivity : ComponentActivity() {

    // Create ViewModel instance here, passing userPreferencesDataStore from Context
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(this.userPreferencesDataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check and request notification permission (Android 13+)
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

        // Set UI content
        setContent {
            ThreeThingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
    suspend fun saveTasks(context: Context, task1: String, task2: String, task3: String) {
        context.userPreferencesDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setTask1(task1)
                .setTask2(task2)
                .setTask3(task3)
                .build()
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
            val task1 = prefs.task1
            val task2 = prefs.task2
            val task3 = prefs.task3

            val intent = Intent(this@MainActivity, TaskForegroundService::class.java).apply {
                putExtra("task1", task1)
                putExtra("task2", task2)
                putExtra("task3", task3)
            }
            ContextCompat.startForegroundService(this@MainActivity, intent)
        }
    }
}
