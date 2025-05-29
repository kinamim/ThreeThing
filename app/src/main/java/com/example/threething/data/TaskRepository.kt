package com.example.threething.data

import android.content.Context
import com.example.threething.TaskProto
import com.example.threething.UserPreferences
import com.example.threething.notification.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

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
            val updated = current.toBuilder()
                .setTask1(
                    TaskProto.newBuilder()
                        .setText(newValue)
                        .setIsCompleted(false) // or preserve previous completed state if you want
                        .build()
                )
                .build()

            NotificationHelper.showTaskNotification(
                context,
                updated.task1.text,
                updated.task2.text,
                updated.task3.text
            )
            updated
        }
    }

    suspend fun updateTask2(newValue: String) {
        dataStore.updateData { current ->
            val updated = current.toBuilder()
                .setTask2(
                    TaskProto.newBuilder()
                        .setText(newValue)
                        .setIsCompleted(false)
                        .build()
                )
                .build()

            NotificationHelper.showTaskNotification(
                context,
                updated.task1.text,
                updated.task2.text,
                updated.task3.text
            )
            updated
        }
    }

    suspend fun updateTask3(newValue: String) {
        dataStore.updateData { current ->
            val updated = current.toBuilder()
                .setTask3(
                    TaskProto.newBuilder()
                        .setText(newValue)
                        .setIsCompleted(false)
                        .build()
                )
                .build()

            NotificationHelper.showTaskNotification(
                context,
                updated.task1.text,
                updated.task2.text,
                updated.task3.text
            )
            updated
        }
    }
}