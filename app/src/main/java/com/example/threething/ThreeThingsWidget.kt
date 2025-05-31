package com.example.threething

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.threething.data.readTasks
import com.example.threething.data.userPreferencesDataStore
import com.example.threething.ui.main.MainActivity
import com.example.threething.widget.ToggleTaskReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyThreeThingsWidget : AppWidgetProvider() {

    companion object {
        private const val TAG = "MyThreeThingsWidget"
        private const val ACTION_TOGGLE_TASK = "com.example.threething.TOGGLE_TASK"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = readTasks(context.userPreferencesDataStore).first()

                appWidgetIds.forEach { appWidgetId ->
                    val views = RemoteViews(context.packageName, R.layout.widget_my_three_things)
                    views.setTextViewText(R.id.widget_title, "My Three Things")

                    val allTasksEmpty = prefs.task1.text.isBlank() && prefs.task2.text.isBlank() && prefs.task3.text.isBlank()

                    if (allTasksEmpty) {
                        // Show empty message
                        views.setViewVisibility(R.id.widget_empty_message, View.VISIBLE)
                        // Hide all task containers
                        views.setViewVisibility(R.id.task1_container, View.GONE)
                        views.setViewVisibility(R.id.task2_container, View.GONE)
                        views.setViewVisibility(R.id.task3_container, View.GONE)
                    } else {
                        // Hide empty message
                        views.setViewVisibility(R.id.widget_empty_message, View.GONE)

                        fun updateTask(
                            taskIndex: Int,
                            taskText: String,
                            isCompleted: Boolean
                        ) {
                            val containerId = when (taskIndex) {
                                1 -> R.id.task1_container
                                2 -> R.id.task2_container
                                3 -> R.id.task3_container
                                else -> return
                            }
                            val textId = when (taskIndex) {
                                1 -> R.id.task1
                                2 -> R.id.task2
                                3 -> R.id.task3
                                else -> return
                            }
                            val iconId = when (taskIndex) {
                                1 -> R.id.task1_icon
                                2 -> R.id.task2_icon
                                3 -> R.id.task3_icon
                                else -> return
                            }

                            if (taskText.isNotBlank()) {
                                views.setViewVisibility(containerId, View.VISIBLE)
                                views.setTextViewText(textId, taskText)
                                views.setImageViewResource(
                                    iconId,
                                    if (isCompleted) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked
                                )

                                // Set checkbox tint: green if completed, black if not
                                val checkboxColor = if (isCompleted) {
                                    context.getColor(R.color.checkboxGreen)
                                } else {
                                    context.getColor(android.R.color.black)
                                }
                                views.setInt(iconId, "setColorFilter", checkboxColor)

                                // Set text color: green if completed, black if not
                                val textColor = if (isCompleted) {
                                    context.getColor(R.color.checkboxGreen)
                                } else {
                                    context.getColor(android.R.color.black)
                                }
                                views.setTextColor(textId, textColor)

                                // Set strikethrough paint flag for completed tasks
                                views.setInt(
                                    textId,
                                    "setPaintFlags",
                                    if (isCompleted)
                                        Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                                    else
                                        Paint.ANTI_ALIAS_FLAG
                                )

                                // Set click listener on checkbox icon to toggle
                                val toggleIntent = Intent(context, ToggleTaskReceiver::class.java).apply {
                                    action = ACTION_TOGGLE_TASK
                                    putExtra("task_index", taskIndex)
                                }
                                val pendingToggleIntent = PendingIntent.getBroadcast(
                                    context,
                                    taskIndex,
                                    toggleIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                )
                                views.setOnClickPendingIntent(iconId, pendingToggleIntent)
                            } else {
                                views.setViewVisibility(containerId, View.GONE)
                            }
                        }

                        updateTask(1, prefs.task1.text, prefs.task1.isCompleted)
                        updateTask(2, prefs.task2.text, prefs.task2.isCompleted)
                        updateTask(3, prefs.task3.text, prefs.task3.isCompleted)
                    }

                    // Root layout click opens the app
                    val openAppIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val pendingOpenAppIntent = PendingIntent.getActivity(
                        context, 0, openAppIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_root_layout, pendingOpenAppIntent)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating widget", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
