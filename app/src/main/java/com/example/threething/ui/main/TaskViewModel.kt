package com.example.threething.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.datastore.core.DataStore
import com.example.threething.UserPreferences
import com.example.threething.data.readTasks
import com.example.threething.data.saveTasks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val dataStore: DataStore<UserPreferences>
) : ViewModel() {

    private val _task1 = MutableStateFlow("")
    private val _task2 = MutableStateFlow("")
    private val _task3 = MutableStateFlow("")

    val task1: StateFlow<String> = _task1
    val task2: StateFlow<String> = _task2
    val task3: StateFlow<String> = _task3

    init {
        // Load saved tasks once when ViewModel is created
        viewModelScope.launch {
            readTasks(dataStore).collect { prefs ->
                _task1.value = prefs.task1
                _task2.value = prefs.task2
                _task3.value = prefs.task3
            }
        }
    }

    fun updateTask1(newTask1: String) {
        _task1.value = newTask1
        persistAll()
    }

    fun updateTask2(newTask2: String) {
        _task2.value = newTask2
        persistAll()
    }

    fun updateTask3(newTask3: String) {
        _task3.value = newTask3
        persistAll()
    }

    private fun persistAll() {
        viewModelScope.launch {
            saveTasks(
                dataStore,
                _task1.value,
                _task2.value,
                _task3.value
            )
        }
    }

    /** Factory to provide DataStore<UserPreferences> into this ViewModel */
    class Factory(
        private val dataStore: DataStore<UserPreferences>
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                return TaskViewModel(dataStore) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
