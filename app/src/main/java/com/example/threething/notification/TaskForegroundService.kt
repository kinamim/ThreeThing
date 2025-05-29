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

class TaskForegroundService : Service() {

    private val TAG = "TaskForegroundService"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started with startId: $startId")

        val task1 = intent?.getStringExtra("task1") ?: ""
        val task2 = intent?.getStringExtra("task2") ?: ""
        val task3 = intent?.getStringExtra("task3") ?: ""

        Log.d(TAG, "Tasks received: $task1, $task2, $task3")

        val notification: Notification = NotificationCompat.Builder(this, "task_channel")
            .setContentTitle("Your 3 Tasks Today")
            .setContentText("$task1 | $task2 | $task3")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()

        Log.d(TAG, "Starting foreground with notification")
        startForeground(1, notification)

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
    }
}
