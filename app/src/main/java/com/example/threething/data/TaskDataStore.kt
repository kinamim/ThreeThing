package com.example.threething.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.threething.UserPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import com.example.threething.datastore.userPreferencesDataStore  // ✅ add this

// Proto serializer
object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return try {
            UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}

// Save tasks
suspend fun saveTasks(context: Context, task1: String, task2: String, task3: String) {
    context.userPreferencesDataStore.updateData { currentPrefs ->
        currentPrefs.toBuilder()
            .setTask1(task1)
            .setTask2(task2)
            .setTask3(task3)
            .build()
    }
}

// Read tasks
fun readTasks(context: Context) = context.userPreferencesDataStore.data
