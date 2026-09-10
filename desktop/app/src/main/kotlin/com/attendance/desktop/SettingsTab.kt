package com.attendance.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsTab() {
    var serverUrl       by remember { mutableStateOf("https://your-backend.example.com/api/v1/") }
    var retentionDays   by remember { mutableStateOf("30") }
    var darkTheme       by remember { mutableStateOf(false) }
    var notifications   by remember { mutableStateOf(true) }
    var saved           by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Text("Настройки", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        Text("Сервер", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = serverUrl,
            onValueChange = { serverUrl = it; saved = false },
            label = { Text("URL бэкенда") },
            modifier = Modifier.fillMaxWidth(0.6f)
        )
        Spacer(Modifier.height(20.dp))

        Text("Хранение данных", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = retentionDays,
            onValueChange = { retentionDays = it; saved = false },
            label = { Text("Удаление отчисленных через X дней") },
            modifier = Modifier.width(200.dp)
        )
        Spacer(Modifier.height(20.dp))

        Text("Интерфейс", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Switch(checked = darkTheme, onCheckedChange = { darkTheme = it; saved = false })
            Spacer(Modifier.width(12.dp))
            Text("Тёмная тема")
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Switch(checked = notifications, onCheckedChange = { notifications = it; saved = false })
            Spacer(Modifier.width(12.dp))
            Text("Уведомления")
        }
        Spacer(Modifier.height(24.dp))

        Button(onClick = { saved = true }) { Text("Сохранить настройки") }
        if (saved) {
            Spacer(Modifier.height(8.dp))
            Text("Настройки сохранены ✓", color = androidx.compose.ui.graphics.Color(0xFF388E3C))
        }
    }
}
