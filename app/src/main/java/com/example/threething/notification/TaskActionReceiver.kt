package com.example.threething.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.threething.data.userPreferencesDataStore
import com.example.threething.UserPreferences
import kotlinx.coroutines.*
import androidx.datastore.core.DataStore

class TaskActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra("task_id") ?: return
        val dataStore: DataStore<UserPreferences> = context.userPreferencesDataStore
        Log.d("TaskActionReceiver", "Received action for $taskId")

        CoroutineScope(Dispatchers.IO).launch {
            dataStore.updateData { prefs ->
                val builder = prefs.toBuilder()
                when (taskId) {
                    "task1" -> builder.task1 = builder.task1.toBuilder().setIsCompleted(true).build()
                    "task2" -> builder.task2 = builder.task2.toBuilder().setIsCompleted(true).build()
                    "task3" -> builder.task3 = builder.task3.toBuilder().setIsCompleted(true).build()
                }
                builder.build()
            }
        }
    }
}
