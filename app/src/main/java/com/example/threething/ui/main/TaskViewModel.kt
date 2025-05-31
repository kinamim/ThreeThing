package com.example.threething.ui.main

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.threething.TaskProto
import com.example.threething.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log
import com.example.threething.widget.WidgetUtils


data class Task(val text: String, val isCompleted: Boolean = false)

class TaskViewModel(
    application: Application,
    private val dataStore: DataStore<UserPreferences>
) : AndroidViewModel(application) {

    private val TAG = "TaskViewModel"

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _startNotificationEvent = MutableStateFlow(false)
    val startNotificationEvent: StateFlow<Boolean> = _startNotificationEvent

    init {
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
        triggerStartNotification()
    }

    fun clearTasks() {
        _tasks.value = emptyList()
        persistTasks(emptyList())
        triggerStartNotification()
    }

    fun toggleTaskCompletion(index: Int) {
        val currentTasks = _tasks.value
        if (index !in currentTasks.indices) {
            Log.w(TAG, "toggleTaskCompletion: invalid index $index")
            return
        }
        val updated = currentTasks.toMutableList().apply {
            val task = this[index]
            this[index] = task.copy(isCompleted = !task.isCompleted)
        }
        _tasks.value = updated
        Log.d(TAG, "Task toggled: $updated")
        persistTasks(updated)
        triggerStartNotification()
    }

    fun removeTask(index: Int) {
        val currentTasks = _tasks.value
        if (index !in currentTasks.indices) return

        val updated = currentTasks.toMutableList().apply {
            removeAt(index)
        }
        _tasks.value = updated
        persistTasks(updated)
        triggerStartNotification()
    }

    private fun persistTasks(tasks: List<Task>) {
        viewModelScope.launch {
            try {
                dataStore.updateData { prefs ->
                    prefs.toBuilder()
                        .setTask1(
                            tasks.getOrNull(0)?.let {
                                TaskProto.newBuilder()
                                    .setText(it.text)
                                    .setIsCompleted(it.isCompleted)
                                    .build()
                            } ?: TaskProto.getDefaultInstance()
                        )
                        .setTask2(
                            tasks.getOrNull(1)?.let {
                                TaskProto.newBuilder()
                                    .setText(it.text)
                                    .setIsCompleted(it.isCompleted)
                                    .build()
                            } ?: TaskProto.getDefaultInstance()
                        )
                        .setTask3(
                            tasks.getOrNull(2)?.let {
                                TaskProto.newBuilder()
                                    .setText(it.text)
                                    .setIsCompleted(it.isCompleted)
                                    .build()
                            } ?: TaskProto.getDefaultInstance()
                        )
                        .build()
                }
                Log.d(TAG, "Tasks persisted, calling updateAllWidgets()")
                WidgetUtils.updateAllWidgets(getApplication())
                Log.d(TAG, "updateAllWidgets() called")
            } catch (e: Exception) {
                Log.e(TAG, "Error persisting tasks", e)
            }
        }
    }


    fun triggerStartNotification() {
        _startNotificationEvent.value = true
    }

    fun resetStartNotificationEvent() {
        _startNotificationEvent.value = false
    }
}
