package com.attendance.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class DemoStudent(val id: Int, val name: String)

val STATUSES    = listOf("Присут.", "Уваж.", "Неуваж.", "Болен")
val STATUS_FULL = listOf("Присутствует", "Уважительная", "Неуважительная", "Болезнь")
val STATUS_COLORS = listOf(Color(0xFF388E3C), Color(0xFFFFA000), Color(0xFFD32F2F), Color(0xFF7B1FA2))

@Composable
fun AttendanceTab() {
    val today   = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
    var subject by remember { mutableStateOf("Математика") }
    var selDate by remember { mutableStateOf(today) }
    var saveMsg by remember { mutableStateOf("") }
    val students = remember { AppSettings.getStudents().mapIndexed { i, n -> DemoStudent(i + 1, n) } }
    val records  = remember { mutableStateMapOf<Int, Int>() }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Журнал посещаемости", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            AssistChip(onClick = {},
                label = { Text("Присут: ${records.values.count { it == 0 }} / ${students.size}") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, null, Modifier.size(16.dp)) })
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = subject, onValueChange = { subject = it },
                label = { Text("Предмет") }, modifier = Modifier.width(220.dp), singleLine = true)
            OutlinedTextField(value = selDate, onValueChange = { selDate = it },
                label = { Text("Дата") }, modifier = Modifier.width(160.dp), singleLine = true)
        }
        Spacer(Modifier.height(16.dp))

        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("#", Modifier.width(32.dp), fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 13.sp)
                Text("Студент", Modifier.weight(1f), fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                STATUSES.forEachIndexed { i, s ->
                    Text(s, Modifier.width(80.dp), fontWeight = FontWeight.SemiBold,
                        color = STATUS_COLORS[i], fontSize = 12.sp)
                }
            }
        }
        HorizontalDivider()

        LazyColumn(Modifier.weight(1f)) {
            itemsIndexed(students) { idx, student ->
                val bg = if (idx % 2 == 0) MaterialTheme.colorScheme.surface
                         else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                Box(Modifier.background(bg)) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("${idx + 1}", Modifier.width(32.dp), fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(student.name, Modifier.weight(1f), fontSize = 14.sp)
                        STATUSES.forEachIndexed { si, _ ->
                            val sel = records[student.id] == si
                            FilterChip(
                                selected = sel,
                                onClick = { if (sel) records.remove(student.id) else records[student.id] = si; saveMsg = "" },
                                label = { Text(STATUSES[si], fontSize = 11.sp) },
                                modifier = Modifier.width(80.dp).padding(horizontal = 2.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = STATUS_COLORS[si].copy(alpha = 0.2f),
                                    selectedLabelColor = STATUS_COLORS[si])
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider()
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                runCatching {
                    val dir = File(System.getProperty("user.home"), "AttendanceJournal").also { it.mkdirs() }
                    val file = File(dir, "journal_${selDate.replace(".","-")}_${subject.replace(" ","_")}.csv")
                    val sb = StringBuilder("Дата;Предмет;ФИО;Статус\n")
                    students.forEach { st ->
                        val si = records[st.id]
                        sb.append("$selDate;$subject;${st.name};${if (si != null) STATUS_FULL[si] else "—"}\n")
                    }
                    file.writeText(sb.toString(), Charsets.UTF_8)
                    saveMsg = "✓ ${file.absolutePath}"
                }.onFailure { saveMsg = "Ошибка: ${it.message}" }
            }) { Text("Сохранить") }

            OutlinedButton(onClick = {
                runCatching {
                    val chooser = javax.swing.JFileChooser()
                    chooser.dialogTitle = "Экспорт CSV"
                    chooser.selectedFile = File("attendance_${selDate.replace(".","-")}.csv")
                    if (chooser.showSaveDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                        val f = chooser.selectedFile.let { if (!it.name.endsWith(".csv")) File(it.absolutePath + ".csv") else it }
                        val sb = StringBuilder("Дата;Предмет;ФИО;Статус\n")
                        students.forEach { st ->
                            val si = records[st.id]
                            sb.append("$selDate;$subject;${st.name};${if (si != null) STATUS_FULL[si] else "—"}\n")
                        }
                        f.writeText(sb.toString(), Charsets.UTF_8)
                        saveMsg = "✓ Экспортировано"
                    }
                }.onFailure { saveMsg = "Ошибка: ${it.message}" }
            }) { Text("Экспорт CSV") }

            if (saveMsg.isNotEmpty())
                Text(saveMsg, fontSize = 12.sp,
                    color = if (saveMsg.startsWith("✓")) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error)
        }
    }
}
