package com.example.threething.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.threething.UserPreferences
import java.io.InputStream
import java.io.OutputStream


object TasksSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return try {
            UserPreferences.parseFrom(input)
        } catch (e: Exception) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}
