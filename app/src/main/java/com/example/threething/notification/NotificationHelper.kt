package com.example.threething.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.threething.R
import com.example.threething.ui.main.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "task_channel_id"
    private const val NOTIFICATION_ID = 1

    fun showTaskNotification(context: Context, task1: String, task2: String, task3: String) {
        // ❗ If all tasks are empty, cancel the notification
        if (task1.isBlank() && task2.isBlank() && task3.isBlank()) {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
            return
        }

        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val content = listOf(task1, task2, task3)
            .filter { it.isNotBlank() }
            .joinToString("\n")

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // use your icon
            .setContentTitle("Today's Tasks")
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setContentIntent(pendingIntent)
            .setOngoing(true) // makes it persistent
            .setPriority(NotificationCompat.PRIORITY_LOW)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Daily Task Channel",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows daily focus tasks"
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }
}
