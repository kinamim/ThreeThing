package com.example.threething.ui.DailyFocusScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import com.example.threething.UserPreferences
import com.example.threething.ui.main.TaskViewModel

@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val task1 = viewModel.task1.collectAsState().value
    val task2 = viewModel.task2.collectAsState().value
    val task3 = viewModel.task3.collectAsState().value

    Column {
        TextField(
            value = task1,
            onValueChange = { viewModel.updateTask1(it) },
            label = { Text("Task 1") }
        )
        TextField(
            value = task2,
            onValueChange = { viewModel.updateTask2(it) },
            label = { Text("Task 2") }
        )
        TextField(
            value = task3,
            onValueChange = { viewModel.updateTask3(it) },
            label = { Text("Task 3") }
        )

        // Debug UI: show saved tasks in DataStore live
        DebugDataStoreContent(viewModel.dataStore)
    }
}

@Composable
fun DebugDataStoreContent(dataStore: DataStore<UserPreferences>) {
    val prefsFlow = dataStore.data.collectAsState(initial = UserPreferences.getDefaultInstance())
    Text(
        text = "Stored tasks:\n1: ${prefsFlow.value.task1}\n2: ${prefsFlow.value.task2}\n3: ${prefsFlow.value.task3}"
    )
}
