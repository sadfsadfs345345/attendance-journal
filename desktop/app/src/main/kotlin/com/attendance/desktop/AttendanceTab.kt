package com.attendance.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.attendance.desktop.model.AttendanceStatus
import java.text.SimpleDateFormat
import java.util.*

data class DesktopEntry(val id: Int, val name: String, val group: String, val status: AttendanceStatus = AttendanceStatus.PRESENT, val headman: AttendanceStatus = AttendanceStatus.PRESENT, val teacher: AttendanceStatus = AttendanceStatus.PRESENT)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceTab(onClosed: (String, String, Int) -> Unit = { _, _, _ -> }) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val students = remember { AppSettings.getStudents().mapIndexed { i, name -> DesktopEntry(i + 1, name, "ИС-21") } }
    var entries by remember { mutableStateOf(students) }; var subject by remember { mutableStateOf("Математика") }; var date by remember { mutableStateOf(today) }; var closed by remember { mutableStateOf(false) }; var saved by remember { mutableStateOf(false) }; var resolved by remember { mutableStateOf(setOf<Int>()) }
    val conflicts = entries.count { it.headman != it.teacher && !resolved.contains(it.id) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("Журнал посещаемости", fontSize = 22.sp, fontWeight = FontWeight.Bold); AssistChip(onClick = {}, label = { Text("${entries.count { it.status == AttendanceStatus.PRESENT }} / ${entries.size} прис.") }) }
        Spacer(Modifier.height(16.dp)); Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { OutlinedTextField(subject, { subject = it; closed = false }, label = { Text("Предмет") }, modifier = Modifier.width(220.dp), singleLine = true); OutlinedTextField(date, { date = it; closed = false }, label = { Text("Дата") }, modifier = Modifier.width(160.dp), singleLine = true) }
        if (conflicts > 0 && !closed) Card(Modifier.fillMaxWidth().padding(vertical = 12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) { Text("Расхождений: $conflicts — разрешите каждую строку", Modifier.padding(12.dp), fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(4.dp)); LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(entries, key = { it.id }) { entry -> DesktopAttendanceRow(entry, closed, conflicts > 0 && !resolved.contains(entry.id), onStatus = { status -> entries = entries.map { if (it.id == entry.id) it.copy(status = status) else it }; saved = false }, onResolve = { useTeacher -> entries = entries.map { if (it.id == entry.id) it.copy(status = if (useTeacher) it.teacher else it.headman) else it }; resolved = resolved + entry.id }) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (conflicts == 0 && !closed) Button(onClick = { closed = true; onClosed(date, subject, entries.size) }) { Text("Закрыть занятие") } else if (closed) AssistChip(onClick = {}, label = { Text("✓ Занятие в архиве") }) else Button(onClick = {}, enabled = false) { Text("Разрешите конфликты") }
            if (!closed) OutlinedButton(onClick = { saved = true }) { Text(if (saved) "Сохранено" else "Сохранить") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DesktopAttendanceRow(entry: DesktopEntry, closed: Boolean, conflict: Boolean, onStatus: (AttendanceStatus) -> Unit, onResolve: (Boolean) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (conflict) MaterialTheme.colorScheme.errorContainer.copy(.3f) else MaterialTheme.colorScheme.surface)) { Column(Modifier.padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { Text(entry.name, Modifier.weight(1f), fontWeight = FontWeight.SemiBold); if (!closed) Box { AssistChip(onClick = { expanded = true }, label = { Text(entry.status.short) }); DropdownMenu(expanded, { expanded = false }) { AttendanceStatus.values().forEach { s -> DropdownMenuItem(text = { Text(s.label) }, onClick = { onStatus(s); expanded = false }) } } } else Text(entry.status.label, color = entry.status.color()) }
        if (conflict) Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { Text("Староста: ${entry.headman.short}", Modifier.weight(1f), fontSize = 12.sp); TextButton({ onResolve(false) }) { Text("Принять") }; Text("Учитель: ${entry.teacher.short}", Modifier.weight(1f), fontSize = 12.sp); TextButton({ onResolve(true) }) { Text("Принять") } }
    } }
}
private fun AttendanceStatus.color(): Color = when (this) { AttendanceStatus.PRESENT -> Color(0xFF2E8B57); AttendanceStatus.EXCUSED -> Color(0xFFB7791F); AttendanceStatus.UNEXCUSED -> Color(0xFFC2413B) }
