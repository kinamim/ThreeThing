package com.example.threething.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.threething.R
import com.example.threething.UserPreferences
import com.example.threething.data.userPreferencesDataStore
import com.example.threething.ui.main.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class TaskForegroundService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val TAG = "TaskForegroundService"
    private lateinit var dataStore: androidx.datastore.core.DataStore<UserPreferences>

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        createNotificationChannel()

        dataStore = applicationContext.userPreferencesDataStore

        serviceScope.launch {
            dataStore.data.collectLatest { prefs ->

                val task1Text = prefs.task1.text
                val task2Text = prefs.task2.text
                val task3Text = prefs.task3.text

                val taskList = listOf(task1Text, task2Text, task3Text)
                    .filter { it.isNotBlank() }

                val collapsedText = taskList.joinToString(" | ")

                fun taskStatusIcon(isCompleted: Boolean) = if (isCompleted) "✅" else "☐"

                val taskLines = mutableListOf<String>()

                if (task1Text.isNotBlank()) {
                    taskLines.add("${taskStatusIcon(prefs.task1.isCompleted)} $task1Text")
                }
                if (task2Text.isNotBlank()) {
                    taskLines.add("${taskStatusIcon(prefs.task2.isCompleted)} $task2Text")
                }
                if (task3Text.isNotBlank()) {
                    taskLines.add("${taskStatusIcon(prefs.task3.isCompleted)} $task3Text")
                }

                val expandedText = taskLines.joinToString("\n")

                val intent = Intent(this@TaskForegroundService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                val pendingIntent = PendingIntent.getActivity(
                    this@TaskForegroundService,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification: Notification = NotificationCompat.Builder(this@TaskForegroundService, "task_channel")
                    .setContentTitle("Your 3 Tasks Today")
                    .setContentText(collapsedText)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(expandedText))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // for Android 7 and below
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // for lock screen
                    .build()

                Log.d(TAG, "Updating foreground notification")
                startForeground(1, notification)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started with startId: $startId")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "task_channel"
            val channelName = "Daily Task Notification"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Shows your daily tasks"
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC // <-- this is key for lock screen
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        serviceScope.cancel()
    }
}
