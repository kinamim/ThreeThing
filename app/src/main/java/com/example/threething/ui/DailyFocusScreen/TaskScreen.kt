package com.example.threething.ui.DailyFocusScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    }
}
