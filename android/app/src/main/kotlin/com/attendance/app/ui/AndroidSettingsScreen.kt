package com.attendance.app.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AndroidSettingsScreen() {
    val ctx   = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }

    var serverUrl     by remember { mutableStateOf(prefs.getString("server_url", "https://your-backend.example.com/api/v1/") ?: "") }
    var groupName     by remember { mutableStateOf(prefs.getString("group_name", "ИС-21") ?: "") }
    var notifications by remember { mutableStateOf(prefs.getBoolean("notifications", true)) }
    var saved         by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Настройки", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Учебная группа", style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(value = groupName, onValueChange = { groupName = it; saved = false },
                    label = { Text("Номер группы (напр. ИС-21)") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true)
            }
        }

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Подключение к серверу", style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(value = serverUrl, onValueChange = { serverUrl = it; saved = false },
                    label = { Text("URL бэкенда") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true)
            }
        }

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Уведомления", fontWeight = FontWeight.Medium)
                    Text("Push-уведомления об изменениях",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = notifications, onCheckedChange = { notifications = it; saved = false })
            }
        }

        Spacer(Modifier.height(4.dp))
        Button(
            onClick = {
                prefs.edit()
                    .putString("server_url", serverUrl)
                    .putString("group_name", groupName)
                    .putBoolean("notifications", notifications)
                    .apply()
                saved = true
            },
            modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp)
        ) { Text("Сохранить настройки", fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }

        if (saved) {
            Card(Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(10.dp)) {
                Text("✓ Настройки сохранены",
                    Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium)
            }
        }
    }
}
