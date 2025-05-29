package com.example.threething.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskInputScreen(
    task1: String,
    task2: String,
    task3: String,
    onTask1Change: (String) -> Unit,
    onTask2Change: (String) -> Unit,
    onTask3Change: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = task1,
            onValueChange = onTask1Change,
            label = { Text("Task 1") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = task2,
            onValueChange = onTask2Change,
            label = { Text("Task 2") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = task3,
            onValueChange = onTask3Change,
            label = { Text("Task 3") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
