package com.example.threething.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.threething.TaskProto
import com.example.threething.UserPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow

val Context.userPreferencesDataStore: DataStore<UserPreferences> by dataStore(
    fileName = "user_prefs.pb",
    serializer = UserPreferencesSerializer
)

suspend fun saveTasks(
    dataStore: DataStore<UserPreferences>,
    task1: String,
    task2: String,
    task3: String
) {
    dataStore.updateData { prefs ->
        prefs.toBuilder()
            .setTask1(TaskProto.newBuilder().setText(task1).setIsCompleted(false).build())
            .setTask2(TaskProto.newBuilder().setText(task2).setIsCompleted(false).build())
            .setTask3(TaskProto.newBuilder().setText(task3).setIsCompleted(false).build())
            .build()
    }
}

fun readTasks(dataStore: DataStore<UserPreferences>): Flow<UserPreferences> = dataStore.data
