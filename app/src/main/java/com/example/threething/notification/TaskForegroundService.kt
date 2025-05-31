package com.example.threething.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.threething.R
import com.example.threething.UserPreferences
import com.example.threething.data.userPreferencesDataStore
import com.example.threething.ui.main.MainActivity
import com.example.threething.MyThreeThingsWidget
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class TaskForegroundService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val TAG = "TaskForegroundService"
    private lateinit var dataStore: androidx.datastore.core.DataStore<UserPreferences>
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "task_channel"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        createNotificationChannel()
        dataStore = applicationContext.userPreferencesDataStore
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started with startId: $startId")

        // Show minimal notification immediately to satisfy foreground service requirement
        val loadingNotification = buildLoadingNotification()
        startForeground(NOTIFICATION_ID, loadingNotification)

        // Launch coroutine to observe data changes and update notification accordingly
        serviceScope.launch {
            dataStore.data.collectLatest { prefs ->
                val updatedNotification = buildTaskNotification(prefs)
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager?.notify(NOTIFICATION_ID, updatedNotification)
                Log.d(TAG, "Notification updated")
            }
        }

        return START_STICKY
    }

    private fun buildLoadingNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Loading your tasks...")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun buildTaskNotification(prefs: UserPreferences): Notification {
        val task1Text = prefs.task1.text
        val task2Text = prefs.task2.text
        val task3Text = prefs.task3.text

        val taskList = listOf(task1Text, task2Text, task3Text).filter { it.isNotBlank() }
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

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Your 3 Tasks Today")
            .setContentText(collapsedText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedText))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    /**
     * This function sends a notification that, when tapped, launches the system widget picker
     * focused on your widget so the user can add it manually.
     */
    fun sendAddWidgetNotification(context: Context) {
        val widgetManager = AppWidgetManager.getInstance(context)
        val myWidget = ComponentName(context, MyThreeThingsWidget::class.java)

        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, myWidget)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Add My Three Things Widget")
            .setContentText("Tap to add the widget to your home screen")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1002, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Daily Task Notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = "Shows your daily tasks"
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
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
