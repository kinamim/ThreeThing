package com.example.threething.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.threething.UserPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow

object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences =
        try {
            UserPreferences.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            defaultValue
        }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) = t.writeTo(output)
}

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
            .setTask1(task1)
            .setTask2(task2)
            .setTask3(task3)
            .build()
    }
}

fun readTasks(dataStore: DataStore<UserPreferences>): Flow<UserPreferences> = dataStore.data
