package com.example.threething.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.threething.data.userPreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.example.threething.data.readTasks

class ToggleTaskReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskIndex = intent.getIntExtra("task_index", -1)
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (taskIndex !in 1..3) {
                    Log.w("ToggleTaskReceiver", "Invalid task index: $taskIndex")
                    return@launch
                }

                // Read current preferences
                val currentPrefs = readTasks(context.userPreferencesDataStore).first()

                // Toggle the task's completion state
                val updatedPrefs = currentPrefs.toBuilder().apply {
                    when (taskIndex) {
                        1 -> task1 = task1.toBuilder()
                            .setIsCompleted(!task1.isCompleted)
                            .build()
                        2 -> task2 = task2.toBuilder()
                            .setIsCompleted(!task2.isCompleted)
                            .build()
                        3 -> task3 = task3.toBuilder()
                            .setIsCompleted(!task3.isCompleted)
                            .build()
                    }
                }.build()

                // Persist the update
                context.userPreferencesDataStore.updateData { updatedPrefs }

                Log.d("ToggleTaskReceiver", "Task $taskIndex toggled, widget update triggered")

                // Refresh widgets
                WidgetUtils.updateAllWidgets(context)

            } catch (e: Exception) {
                Log.e("ToggleTaskReceiver", "Error toggling task", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
