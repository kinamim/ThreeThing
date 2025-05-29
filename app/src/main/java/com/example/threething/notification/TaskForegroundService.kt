package com.example.threething.notification

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.threething.R
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import com.example.threething.data.userPreferencesDataStore
import androidx.datastore.core.DataStore
import com.example.threething.TaskProto
import com.example.threething.UserPreferences // ✅ add this


class TaskForegroundService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val TAG = "TaskForegroundService"
    private lateinit var dataStore: DataStore<UserPreferences>


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        createNotificationChannel()

        dataStore = applicationContext.userPreferencesDataStore

        // Observe changes from DataStore and update notification
        serviceScope.launch {
            dataStore.data.collectLatest { prefs ->

                val task1Text = prefs.task1.text
                val task2Text = prefs.task2.text
                val task3Text = prefs.task3.text

                val notification: Notification = NotificationCompat.Builder(this@TaskForegroundService, "task_channel")
                    .setContentTitle("Your 3 Tasks Today")
                    .setContentText("$task1Text | $task2Text | $task3Text")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setOngoing(true)
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
            val channel = NotificationChannel(
                "task_channel",
                "Daily Task Notification",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        serviceScope.cancel()
    }
}
