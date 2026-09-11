package com.attendance.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsTab(onThemeChange: (Boolean) -> Unit = {}) {
    var serverUrl     by remember { mutableStateOf(AppSettings.serverUrl) }
    var retentionDays by remember { mutableStateOf(AppSettings.retentionDays) }
    var darkTheme     by remember { mutableStateOf(AppSettings.darkTheme) }
    var notifications by remember { mutableStateOf(AppSettings.notifications) }
    var saved         by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Text("Настройки", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        Card(Modifier.fillMaxWidth(0.65f), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Подключение к серверу",
                    style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(value = serverUrl, onValueChange = { serverUrl = it; saved = false },
                    label = { Text("URL бэкенда") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth(0.65f), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Хранение данных",
                    style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(value = retentionDays, onValueChange = { retentionDays = it; saved = false },
                    label = { Text("Удалить данные отчисленных через X дней") },
                    modifier = Modifier.width(260.dp), singleLine = true)
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth(0.65f), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Интерфейс",
                    style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = darkTheme, onCheckedChange = { darkTheme = it; saved = false })
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Тёмная тема")
                        Text("Аппликуется после сохранения",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = notifications, onCheckedChange = { notifications = it; saved = false })
                    Spacer(Modifier.width(12.dp))
                    Text("Push-уведомления")
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                AppSettings.serverUrl     = serverUrl
                AppSettings.retentionDays = retentionDays
                AppSettings.darkTheme     = darkTheme
                AppSettings.notifications  = notifications
                onThemeChange(darkTheme)
                saved = true
            }, modifier = Modifier.height(44.dp)) { Text("Сохранить настройки") }

            if (saved) AssistChip(onClick = {}, label = { Text("✓ Сохранено") },
                colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.primary))
        }
    }
}
