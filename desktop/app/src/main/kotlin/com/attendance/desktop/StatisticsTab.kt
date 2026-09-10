package com.attendance.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatisticsTab() {
    val demo = listOf(
        Triple("Иванов И.И.", 92f, false),
        Triple("Петрова М.С.", 68f, true),
        Triple("Сидоров А.П.", 85f, false),
        Triple("Козлова Е.Д.", 55f, true),
        Triple("Новиков Д.А.", 97f, false)
    )

    Column(Modifier.fillMaxSize()) {
        Text("Статистика посещаемости", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(demo.size) { i ->
                val (name, percent, atRisk) = demo[i]
                Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, fontWeight = FontWeight.SemiBold)
                            Text(
                                "%.0f%%".format(percent),
                                fontWeight = FontWeight.Bold,
                                color = if (atRisk) Color(0xFFD32F2F) else Color(0xFF388E3C),
                                fontSize = 18.sp
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { percent / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = if (atRisk) Color(0xFFD32F2F) else Color(0xFF388E3C)
                        )
                        if (atRisk) {
                            Spacer(Modifier.height(4.dp))
                            Text("⚠ Ниже порога 75%", color = Color(0xFFD32F2F), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
