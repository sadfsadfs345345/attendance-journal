package com.attendance.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RequestsTab() {
    val requests = remember { mutableStateListOf("Зайцев Кирилл · Студент", "Громов Павел · Учитель", "Фёдоров Игорь · Куратор") }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Заявки на регистрацию", fontSize = 22.sp) }
        items(requests) { request -> Card(Modifier.fillMaxWidth()) { Row(Modifier.fillMaxWidth().padding(12.dp)) { Text(request, Modifier.weight(1f)); TextButton({ requests.remove(request) }) { Text("Одобрить") }; TextButton({ requests.remove(request) }) { Text("Отклонить") } } } }
        if (requests.isEmpty()) item { Text("Новых заявок нет", color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}
