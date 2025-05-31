package com.example.threething.data

import android.content.Context
import com.example.threething.TaskProto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

suspend fun Context.updateTask(taskIndex: Int, updatedTask: TaskProto) {
    userPreferencesDataStore.updateData { prefs ->
        when (taskIndex) {
            1 -> prefs.toBuilder().setTask1(updatedTask).build()
            2 -> prefs.toBuilder().setTask2(updatedTask).build()
            3 -> prefs.toBuilder().setTask3(updatedTask).build()
            else -> prefs
        }
    }
}
