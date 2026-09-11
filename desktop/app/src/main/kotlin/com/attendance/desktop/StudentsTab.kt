package com.attendance.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Save
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
    val students    = remember { mutableStateListOf(*AppSettings.getStudents().toTypedArray()) }
    var newName     by remember { mutableStateOf("") }
    var expelTarget by remember { mutableStateOf<String?>(null) }
    var saved       by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Список студентов", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("${students.size} чел.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = newName, onValueChange = { newName = it },
                label = { Text("Фамилия Имя Отчество") },
                modifier = Modifier.weight(1f), singleLine = true)
            Button(onClick = {
                if (newName.isNotBlank()) { students.add(newName.trim()); newName = ""; saved = false }
            }) {
                Icon(Icons.Default.PersonAdd, null); Spacer(Modifier.width(6.dp)); Text("Добавить")
            }
        }
        Spacer(Modifier.height(16.dp))

        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(students.size) { i ->
                Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primaryContainer) {
                                Text("${i+1}", Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(students[i], fontWeight = FontWeight.Medium)
                        }
                        IconButton(onClick = { expelTarget = students[i] }) {
                            Icon(Icons.Default.PersonRemove, null, tint = Color(0xFFD32F2F))
                        }
                    }
                }
            }
        }

        HorizontalDivider(); Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { AppSettings.saveStudents(students.toList()); saved = true }) {
                Icon(Icons.Default.Save, null); Spacer(Modifier.width(6.dp)); Text("Сохранить список")
            }
            if (saved) AssistChip(onClick = {}, label = { Text("✓ Список сохранён") })
        }
    }

    expelTarget?.let { name ->
        AlertDialog(onDismissRequest = { expelTarget = null },
            title = { Text("Отчислить студента?") },
            text = { Text("«$name» будет удалён. Не забудьте сохранить список.") },
            confirmButton = { TextButton(onClick = { students.remove(name); expelTarget = null; saved = false }) {
                Text("Отчислить", color = Color(0xFFD32F2F)) } },
            dismissButton = { TextButton(onClick = { expelTarget = null }) { Text("Отмена") } })
    }
}
