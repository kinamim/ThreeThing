package com.example.threething.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.threething.datastore.readTasks
import com.example.threething.datastore.saveTasks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            readTasks(context).collect {
                _task1.value = it.task1
                _task2.value = it.task2
                _task3.value = it.task3
            }
        }
    }

    fun updateTask1(value: String) {
        _task1.value = value
        saveAll()
    }

    fun updateTask2(value: String) {
        _task2.value = value
        saveAll()
    }

    fun updateTask3(value: String) {
        _task3.value = value
        saveAll()
    }

    private fun saveAll() {
        viewModelScope.launch {
            saveTasks(context, _task1.value, _task2.value, _task3.value)
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskViewModel(context) as T
        }
    }
}
