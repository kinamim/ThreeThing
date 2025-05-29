package com.example.threething.ui.DailyFocusScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.threething.viewmodel.TaskViewModel

@Composable
fun TaskScreen() {
    val context = LocalContext.current
    val viewModel: TaskViewModel = viewModel(factory = TaskViewModel.Factory(context))

    val task1 by viewModel.task1.collectAsState()
    val task2 by viewModel.task2.collectAsState()
    val task3 by viewModel.task3.collectAsState()

    Column {
        TextField(value = task1, onValueChange = { viewModel.updateTask1(it) }, label = { Text("Task 1") })
        TextField(value = task2, onValueChange = { viewModel.updateTask2(it) }, label = { Text("Task 2") })
        TextField(value = task3, onValueChange = { viewModel.updateTask3(it) }, label = { Text("Task 3") })
    }
}
