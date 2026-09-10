package com.attendance.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StudentsTab() {
    val students = remember {
        mutableStateListOf(
            "Иванов Иван Иванович",
            "Петрова Мария Сергеевна",
            "Сидоров Алексей Павлович"
        )
    }
    var newName by remember { mutableStateOf("") }
    var expelTarget by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        Text("Список студентов", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Ф.И.О. студента") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { if (newName.isNotBlank()) { students.add(newName.trim()); newName = "" } }) {
                Icon(Icons.Default.PersonAdd, null)
                Spacer(Modifier.width(6.dp))
                Text("Добавить")
            }
        }
        Spacer(Modifier.height(20.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(students.size) { i ->
                Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(students[i], fontWeight = FontWeight.Medium)
                        IconButton(onClick = { expelTarget = students[i] }) {
                            Icon(Icons.Default.PersonRemove, null, tint = Color(0xFFD32F2F))
                        }
                    }
                }
            }
        }
    }

    expelTarget?.let { name ->
        AlertDialog(
            onDismissRequest = { expelTarget = null },
            title = { Text("Отчислить?") },
            text = { Text("$name будет отчислен. Данные удалятся через 30 дней.") },
            confirmButton = {
                TextButton(onClick = { students.remove(name); expelTarget = null }) {
                    Text("Отчислить", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = { TextButton(onClick = { expelTarget = null }) { Text("Отмена") } }
        )
    }
}
