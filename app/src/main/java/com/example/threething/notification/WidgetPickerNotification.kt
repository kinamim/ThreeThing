package com.example.threething.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.threething.R

object WidgetPickerNotification {

    private const val CHANNEL_ID = "widget_picker_channel"
    private const val NOTIFICATION_ID = 1002

    fun showWidgetPickerNotification(context: Context) {
        // Thêm log và toast để debug
        Log.d("WidgetNotification", "Stay Focused: Update Your Tasks Now")
        Toast.makeText(context, "Stay Focused: Update Your Tasks Now", Toast.LENGTH_SHORT).show()

        createNotificationChannel(context)

        // Intent đưa user về màn hình chính để họ có thể thêm widget bằng cách long-press
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            homeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Widget Picker"
            val descriptionText = "Notification channel for widget picker"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
