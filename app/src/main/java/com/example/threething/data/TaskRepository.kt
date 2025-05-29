package com.example.threething.data

import android.content.Context
import com.example.threething.UserPreferences
import com.example.threething.notification.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import com.example.threething.datastore.userPreferencesDataStore

class TaskRepository(private val context: Context) {

    private val dataStore = context.userPreferencesDataStore

    val defaultPreferences: UserPreferences = UserPreferences.getDefaultInstance()

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(defaultPreferences) // Emit default if corrupted or empty
            } else {
                throw exception
            }
        }

    suspend fun updateTask1(newValue: String) {
        dataStore.updateData { current ->
            val updated = current.toBuilder().setTask1(newValue).build()
            NotificationHelper.showTaskNotification(context, updated.task1, updated.task2, updated.task3)
            updated
        }
    }

    suspend fun updateTask2(newValue: String) {
        dataStore.updateData { current ->
            val updated = current.toBuilder().setTask2(newValue).build()
            NotificationHelper.showTaskNotification(context, updated.task1, updated.task2, updated.task3)
            updated
        }
    }

    suspend fun updateTask3(newValue: String) {
        dataStore.updateData { current ->
            val updated = current.toBuilder().setTask3(newValue).build()
            NotificationHelper.showTaskNotification(context, updated.task1, updated.task2, updated.task3)
            updated
        }
    }
}
