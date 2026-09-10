package com.attendance.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DemoStudent(val id: Int, val name: String)
data class DemoRecord(val studentId: Int, var status: String = "")

val STATUSES = listOf("✓ Прис.", "УП", "НП", "🤑 Бол.")
val STATUS_COLORS = listOf(
    Color(0xFF388E3C), Color(0xFFFFA000),
    Color(0xFFD32F2F), Color(0xFF7B1FA2)
)

@Composable
fun AttendanceTab() {
    val students = remember {
        listOf(
            DemoStudent(1, "Иванов Иван Иванович"),
            DemoStudent(2, "Петрова Мария Сергеевна"),
            DemoStudent(3, "Сидоров Алексей Павлович"),
            DemoStudent(4, "Козлова Екатерина Дмитриевна"),
            DemoStudent(5, "Новиков Дмитрий Александрович")
        )
    }
    val records = remember { mutableStateMapOf<Int, String>() }
    var selectedDate by remember { mutableStateOf("Сегодня") }
    var subject by remember { mutableStateOf("Математика") }

    Column(Modifier.fillMaxSize()) {
        Text("Журнал посещаемости", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Предмет") },
                modifier = Modifier.width(250.dp)
            )
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { selectedDate = it },
                label = { Text("Дата") },
                modifier = Modifier.width(180.dp)
            )
        }
        Spacer(Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth().padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Студент", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            STATUSES.forEach { s -> Text(s, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(80.dp)) }
        }
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(students) { _, student ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(student.name, modifier = Modifier.weight(1f))
                    STATUSES.forEachIndexed { idx, status ->
                        val selected = records[student.id] == status
                        FilterChip(
                            selected = selected,
                            onClick = { records[student.id] = if (selected) "" else status },
                            label = { Text(status, fontSize = 12.sp) },
                            modifier = Modifier.width(80.dp).padding(horizontal = 2.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = STATUS_COLORS[idx].copy(alpha = 0.2f),
                                selectedLabelColor = STATUS_COLORS[idx]
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { /* TODO: save */ }) { Text("Сохранить") }
            OutlinedButton(onClick = { /* TODO: export CSV */ }) { Text("Экспорт CSV") }
        }
    }
}
