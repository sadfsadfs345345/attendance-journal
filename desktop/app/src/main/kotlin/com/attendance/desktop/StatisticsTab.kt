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
    val students = remember { AppSettings.getStudents() }
    val threshold = 75f
    val percents = remember {
        val base = listOf(92f,68f,85f,55f,97f,78f,60f,91f,73f,88f)
        List(students.size) { i -> base.getOrElse(i) { (65..98).random().toFloat() } }
    }
    val avg = if (percents.isNotEmpty()) percents.average().toFloat() else 0f
    val atRisk = percents.count { it < threshold }

    Column(Modifier.fillMaxSize()) {
        Text("Статистика посещаемости", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Средняя", "%.0f%%".format(avg),
                if (avg >= threshold) Color(0xFF388E3C) else Color(0xFFD32F2F), Modifier.weight(1f))
            SummaryCard("Под угрозой", "$atRisk чел.",
                if (atRisk == 0) Color(0xFF388E3C) else Color(0xFFF57C00), Modifier.weight(1f))
            SummaryCard("Всего", "${students.size} чел.",
                MaterialTheme.colorScheme.primary, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(students.size) { i ->
                val name = students[i].split(" ").take(2).joinToString(" ")
                val pct  = percents.getOrElse(i) { 80f }
                val risk = pct < threshold
                Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(name, fontWeight = FontWeight.SemiBold)
                            Text("%.0f%%".format(pct), fontWeight = FontWeight.Bold, fontSize = 18.sp,
                                color = when { pct >= 85 -> Color(0xFF388E3C); pct >= threshold -> Color(0xFFFFA000); else -> Color(0xFFD32F2F) })
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(progress = { pct / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = when { pct >= 85 -> Color(0xFF388E3C); pct >= threshold -> Color(0xFFFFA000); else -> Color(0xFFD32F2F) })
                        if (risk) { Spacer(Modifier.height(4.dp)); Text("⚠ Ниже порога 75%", color = Color(0xFFD32F2F), fontSize = 12.sp) }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(modifier, elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}
