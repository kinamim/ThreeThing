package com.example.threething.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.datastore.core.DataStore
import com.example.threething.UserPreferences
import com.example.threething.ui.main.TaskViewModel

class TaskViewModelFactory(
    private val application: Application,
    private val userPrefsDataStore: DataStore<UserPreferences>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel is TaskViewModel
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Create TaskViewModel with Application and DataStore injected
            return TaskViewModel(application, userPrefsDataStore) as T
        }
        // If the ViewModel class is not recognized, throw an exception
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

