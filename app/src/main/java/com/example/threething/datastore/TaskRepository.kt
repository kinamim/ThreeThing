package com.example.threething.datastore

import android.content.Context
import com.example.threething.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

suspend fun saveTasks(context: Context, task1: String, task2: String, task3: String) {
    context.userPreferencesDataStore.updateData { prefs ->
        prefs.toBuilder()
            .setTask1(task1)
            .setTask2(task2)
            .setTask3(task3)
            .build()
    }
}

fun readTasks(context: Context): Flow<UserPreferences> {
    return context.userPreferencesDataStore.data
}
