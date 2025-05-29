package com.example.threething.ui.main

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.threething.UserPreferences
import com.example.threething.TaskProto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class Task(val text: String, val isCompleted: Boolean = false)

class TaskViewModel(
    private val dataStore: DataStore<UserPreferences>
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        // Load tasks including isCompleted from DataStore on ViewModel start
        viewModelScope.launch {
            val prefs = dataStore.data.first()
            val restoredTasks = listOfNotNull(
                prefs.task1.takeIf { it.text.isNotBlank() }?.let { Task(it.text, it.isCompleted) },
                prefs.task2.takeIf { it.text.isNotBlank() }?.let { Task(it.text, it.isCompleted) },
                prefs.task3.takeIf { it.text.isNotBlank() }?.let { Task(it.text, it.isCompleted) }
            )
            _tasks.value = restoredTasks
        }
    }

    fun addTask(text: String) {
        val updated = _tasks.value.toMutableList().apply {
            if (size < 3) add(Task(text))
        }
        _tasks.value = updated
        persistTasks(updated)
    }

    fun clearTasks() {
        _tasks.value = emptyList()
        persistTasks(emptyList())
    }

    fun toggleTaskCompletion(index: Int) {
        val updated = _tasks.value.toMutableList().apply {
            val task = this[index]
            this[index] = task.copy(isCompleted = !task.isCompleted)
        }
        _tasks.value = updated
        persistTasks(updated)
    }

    fun removeTask(index: Int) {
        val updated = _tasks.value.toMutableList().apply {
            removeAt(index)
        }
        _tasks.value = updated
        persistTasks(updated)
    }

    private fun persistTasks(tasks: List<Task>) {
        viewModelScope.launch {
            dataStore.updateData { prefs ->
                prefs.toBuilder()
                    .setTask1(tasks.getOrNull(0)?.let { TaskProto.newBuilder().setText(it.text).setIsCompleted(it.isCompleted).build() }
                        ?: TaskProto.getDefaultInstance())
                    .setTask2(tasks.getOrNull(1)?.let { TaskProto.newBuilder().setText(it.text).setIsCompleted(it.isCompleted).build() }
                        ?: TaskProto.getDefaultInstance())
                    .setTask3(tasks.getOrNull(2)?.let { TaskProto.newBuilder().setText(it.text).setIsCompleted(it.isCompleted).build() }
                        ?: TaskProto.getDefaultInstance())
                    .build()
            }
        }
    }
}
