package com.example.threething.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "tasks_prefs")

object TaskKeys {
    val TASK_1 = stringPreferencesKey("task1")
    val TASK_2 = stringPreferencesKey("task2")
    val TASK_3 = stringPreferencesKey("task3")
}

class DataStoreManager(private val context: Context) {
    val task1Flow: Flow<String> = context.dataStore.data
        .map { it[TaskKeys.TASK_1] ?: "" }

    val task2Flow: Flow<String> = context.dataStore.data
        .map { it[TaskKeys.TASK_2] ?: "" }

    val task3Flow: Flow<String> = context.dataStore.data
        .map { it[TaskKeys.TASK_3] ?: "" }

    suspend fun saveTasks(t1: String, t2: String, t3: String) {
        context.dataStore.edit { preferences ->
            preferences[TaskKeys.TASK_1] = t1
            preferences[TaskKeys.TASK_2] = t2
            preferences[TaskKeys.TASK_3] = t3
        }
    }
}
