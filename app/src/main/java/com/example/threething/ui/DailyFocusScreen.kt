package com.example.threething.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.threething.ui.main.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DailyFocusScreen(
    taskViewModel: TaskViewModel,
    startNotification: () -> Unit // pass a lambda to start notification from outside
) {
    // Collect tasks state
    val tasks by taskViewModel.tasks.collectAsState()

    // Collect start notification event state
    val startNotificationEvent by taskViewModel.startNotificationEvent.collectAsState()

    var input by remember { mutableStateOf("") }

    val currentDate = remember {
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
    }

    // Trigger notification start when event becomes true
    LaunchedEffect(startNotificationEvent) {
        if (startNotificationEvent) {
            startNotification()
            taskViewModel.resetStartNotificationEvent()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "My Three Things",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color(0xFF0D47A1),
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = currentDate,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Three important things you need to get done today",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Enter task") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (input.isNotBlank() && tasks.size < 3) {
                            taskViewModel.addTask(input)
                            input = ""
                        }
                    }
                ),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    taskViewModel.clearTasks()
                    input = ""
                }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Clear all")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        tasks.forEachIndexed { index, task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .shadow(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (task.isCompleted) Color(0xFFD0F0C0) else Color.White
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${index + 1}. ${task.text}",
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        taskViewModel.toggleTaskCompletion(index)
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Complete")
                    }
                    IconButton(onClick = {
                        taskViewModel.removeTask(index)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }

        if (tasks.isNotEmpty() && tasks.all { it.isCompleted }) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "🎉 Congratulations, You did it! Keep going!",
                fontSize = 20.sp,
                color = Color(0xFF0D47A1),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
