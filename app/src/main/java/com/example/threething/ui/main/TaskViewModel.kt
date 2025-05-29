package com.example.threething.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.threething.data.readTasks
import com.example.threething.data.saveTasks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskViewModel(private val context: Context) : ViewModel() {
    private val _task1 = MutableStateFlow("")
    private val _task2 = MutableStateFlow("")
    private val _task3 = MutableStateFlow("")

    val task1: StateFlow<String> = _task1
    val task2: StateFlow<String> = _task2
    val task3: StateFlow<String> = _task3

    init {
        viewModelScope.launch {
            val prefs = readTasks(context).first()
            _task1.value = prefs.task1
            _task2.value = prefs.task2
            _task3.value = prefs.task3
        }
    }

    fun updateTask1(value: String) {
        _task1.value = value
        saveCurrentTasks()
    }

    fun updateTask2(value: String) {
        _task2.value = value
        saveCurrentTasks()
    }

    fun updateTask3(value: String) {
        _task3.value = value
        saveCurrentTasks()
    }

    private fun saveCurrentTasks() {
        viewModelScope.launch {
            saveTasks(context, _task1.value, _task2.value, _task3.value)
        }
    }

    // ViewModel factory to pass Context safely
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                return TaskViewModel(context.applicationContext) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
