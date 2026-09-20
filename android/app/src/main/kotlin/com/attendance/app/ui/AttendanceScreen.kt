package com.attendance.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

enum class AttendanceStatus(val label: String, val short: String, val color: Color) {
    PRESENT("Присутствует", "П", Color(0xFF2E8B57)),
    EXCUSED("Уважительная причина", "УП", Color(0xFFB7791F)),
    UNEXCUSED("Неуважительная причина", "НП", Color(0xFFC2413B))
}

data class AttendanceEntry(
    val id: Long,
    val name: String,
    val group: String,
    val headman: AttendanceStatus = AttendanceStatus.PRESENT,
    val teacher: AttendanceStatus = AttendanceStatus.PRESENT,
    val final: AttendanceStatus = AttendanceStatus.PRESENT,
    val unexcused: Int = 0,
    val excused: Int = 0
)

data class ArchivedLesson(
    val date: String,
    val subject: String,
    val group: String,
    val students: Int,
    val curator: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(role: UserRole, userName: String, onLogout: () -> Unit) {
    val allStudents = remember {
        listOf("Козлова Мария", "Иванов Иван", "Морозов Дмитрий", "Новиков Алексей",
            "Петрова Анна", "Сидоров Пётр", "Фёдорова Елена", "Волкова Ольга")
            .mapIndexed { i, name -> AttendanceEntry(i.toLong(), name, "ИС-21", unexcused = i % 4, excused = i % 3) }
    }
    val initial = remember(role, userName) {
        val visible = if (role == UserRole.STUDENT) {
            allStudents.filter { it.name.equals(userName, ignoreCase = true) }.ifEmpty { allStudents.take(1) }
        } else allStudents
        visible.mapIndexed { i, e ->
            if (role == UserRole.CURATOR && i == 0) e.copy(headman = AttendanceStatus.PRESENT, teacher = AttendanceStatus.UNEXCUSED)
            else if (role == UserRole.CURATOR && i == 3) e.copy(headman = AttendanceStatus.EXCUSED, teacher = AttendanceStatus.PRESENT)
            else e
        }
    }
    var entries by remember { mutableStateOf(initial) }
    var selectedTab by remember { mutableStateOf(0) }
    var subject by remember { mutableStateOf("Математика") }
    var lessonDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var closed by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    var resolved by remember { mutableStateOf(setOf<Long>()) }
    var archive by remember { mutableStateOf(listOf<ArchivedLesson>()) }
    var notice by remember { mutableStateOf<String?>(null) }

    val tabs = buildList {
        add("Журнал")
        add("Статистика")
        if (role == UserRole.CURATOR || role == UserRole.DIRECTOR) add("Заявки")
        if (role == UserRole.CURATOR || role == UserRole.DIRECTOR) add("Архив")
        add("Настройки")
    }
    val conflicts = entries.count { it.headman != it.teacher && !resolved.contains(it.id) }
    val current = tabs.getOrElse(selectedTab) { "Журнал" }

    Scaffold(topBar = {
        TopAppBar(
            title = { Column {
                Text("Журнал посещаемости", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("${role.label} · ${userName.ifBlank { "Демо-пользователь" }}", fontSize = 12.sp)
            } },
            actions = { IconButton(onClick = onLogout) { Icon(Icons.Default.Logout, "Выйти") } }
        )
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 8.dp) {
                tabs.forEachIndexed { index, label ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index },
                        text = { Text(label, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) })
                }
            }
            notice?.let { message ->
                AssistChip(onClick = { notice = null }, label = { Text(message) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            when (current) {
                "Журнал" -> JournalView(role, entries, subject, lessonDate, closed, conflicts, resolved,
                    onSubject = { subject = it; closed = false; entries = initial; resolved = emptySet() },
                    onDate = { lessonDate = it; closed = false; entries = initial; resolved = emptySet() },
                    onMark = { id, status ->
                        entries = entries.map { if (it.id == id) it.copy(final = status,
                            headman = if (role == UserRole.HEADMAN) status else it.headman,
                            teacher = if (role == UserRole.TEACHER) status else it.teacher) else it }
                        saved = false
                    },
                    onResolve = { id, useTeacher ->
                        entries = entries.map { if (it.id == id) it.copy(final = if (useTeacher) it.teacher else it.headman) else it }
                        resolved = resolved + id
                    },
                    onMarkAll = { entries = entries.map { it.copy(final = AttendanceStatus.PRESENT) }; saved = false },
                    onSave = { saved = true; notice = "Изменения сохранены локально" },
                    onClose = {
                        if (conflicts == 0) { archive = archive + ArchivedLesson(lessonDate, subject, "ИС-21", entries.size, userName); closed = true; notice = "Занятие закрыто и отправлено в архив" }
                    }, saved = saved)
                "Статистика" -> StatisticsView(entries)
                "Заявки" -> RequestsView(role, notice = { notice = it })
                "Архив" -> ArchiveView(archive)
                "Настройки" -> AndroidSettingsScreen()
            }
        }
    }
}

@Composable
private fun JournalView(
    role: UserRole, entries: List<AttendanceEntry>, subject: String, date: String,
    closed: Boolean, conflicts: Int, resolved: Set<Long>,
    onSubject: (String) -> Unit, onDate: (String) -> Unit,
    onMark: (Long, AttendanceStatus) -> Unit, onResolve: (Long, Boolean) -> Unit,
    onMarkAll: () -> Unit, onSave: () -> Unit, onClose: () -> Unit, saved: Boolean
) {
    var subjectText by remember(subject) { mutableStateOf(subject) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(subjectText, { subjectText = it; onSubject(it) }, label = { Text("Предмет") }, modifier = Modifier.weight(1f), singleLine = true)
            OutlinedTextField(date, onDate, label = { Text("Дата") }, modifier = Modifier.width(150.dp), singleLine = true)
        }
        if (role == UserRole.CURATOR && conflicts > 0 && !closed)
            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text("Расхождений: $conflicts — разрешите каждую строку", Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
            }
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("ИС-21", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("${entries.count { it.final == AttendanceStatus.PRESENT }} / ${entries.size} присутствуют", color = MaterialTheme.colorScheme.primary)
        }
        if (role == UserRole.HEADMAN && !closed) OutlinedButton(onClick = onMarkAll, Modifier.padding(horizontal = 16.dp)) { Text("Отметить всех присутствующими") }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(entries, key = { it.id }) { entry ->
                StudentAttendanceRow(entry, role, closed, entry.headman != entry.teacher && !resolved.contains(entry.id), onMark, onResolve)
            }
        }
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (role == UserRole.CURATOR) {
                Button(onClick = onClose, enabled = !closed && conflicts == 0, modifier = Modifier.weight(1f)) { Text(if (closed) "Занятие закрыто" else "Закрыть занятие") }
            } else if (role != UserRole.STUDENT) {
                Button(onClick = onSave, enabled = !closed, modifier = Modifier.weight(1f)) { Text(if (saved) "Сохранено" else "Сохранить") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentAttendanceRow(entry: AttendanceEntry, role: UserRole, closed: Boolean, conflict: Boolean,
    onMark: (Long, AttendanceStatus) -> Unit, onResolve: (Long, Boolean) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val editable = !closed && (role == UserRole.HEADMAN || role == UserRole.TEACHER || role == UserRole.CURATOR)
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = if (conflict) MaterialTheme.colorScheme.errorContainer.copy(alpha = .35f) else MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(entry.final.color.copy(alpha = .14f)), contentAlignment = Alignment.Center) { Text(entry.name.first().toString(), color = entry.final.color, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(entry.name, fontWeight = FontWeight.SemiBold); Text(entry.group, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                if (editable) Box {
                    AssistChip(onClick = { expanded = true }, label = { Text(entry.final.short) }, leadingIcon = { Icon(Icons.Default.Edit, "Изменить", Modifier.size(16.dp)) })
                    DropdownMenu(expanded, { expanded = false }) { AttendanceStatus.values().forEach { s -> DropdownMenuItem(text = { Text(s.label) }, onClick = { onMark(entry.id, s); expanded = false }) } }
                } else Text(entry.final.label, color = entry.final.color, fontWeight = FontWeight.Bold)
            }
            if (conflict && role == UserRole.CURATOR) Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Староста: ${entry.headman.short}", Modifier.weight(1f), fontSize = 12.sp)
                TextButton(onClick = { onResolve(entry.id, false) }) { Text("Принять") }
                Text("Учитель: ${entry.teacher.short}", Modifier.weight(1f), fontSize = 12.sp)
                TextButton(onClick = { onResolve(entry.id, true) }) { Text("Принять") }
            }
        }
    }
}

@Composable
private fun StatisticsView(entries: List<AttendanceEntry>) {
    val present = entries.count { it.final == AttendanceStatus.PRESENT }; val excused = entries.count { it.final == AttendanceStatus.EXCUSED }; val unexcused = entries.count { it.final == AttendanceStatus.UNEXCUSED }; val total = entries.size.coerceAtLeast(1)
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Статистика посещаемости", fontSize = 22.sp, fontWeight = FontWeight.Bold) }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { StatCard("Присутствуют", present, Color(0xFF2E8B57), Modifier.weight(1f)); StatCard("Уважительная", excused, Color(0xFFB7791F), Modifier.weight(1f)); StatCard("Неуважительная", unexcused, Color(0xFFC2413B), Modifier.weight(1f)) } }
        item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text("Посещаемость группы", fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); LinearProgressIndicator({ present.toFloat() / total }, Modifier.fillMaxWidth(), color = Color(0xFF2E8B57)); Text("${present * 100 / total}% · порог 75%", Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) } } }
        items(entries) { e -> ListItem(headlineContent = { Text(e.name) }, supportingContent = { Text("${e.unexcused} неув. · ${e.excused} уваж.") }, trailingContent = { Text(e.final.label, color = e.final.color, fontWeight = FontWeight.Bold) }) }
    }
}

@Composable private fun StatCard(label: String, value: Int, color: Color, modifier: Modifier) { Card(modifier) { Column(Modifier.padding(12.dp)) { Text(value.toString(), color = color, fontSize = 24.sp, fontWeight = FontWeight.Bold); Text(label, fontSize = 11.sp) } } }

@Composable private fun RequestsView(role: UserRole, notice: (String) -> Unit) { LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { item { Text("Заявки на регистрацию", fontSize = 22.sp, fontWeight = FontWeight.Bold) }; items(listOf("Зайцев Кирилл · Студент", "Громов Павел · Учитель", "Фёдоров Игорь · Куратор")) { request -> Card { Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Text(request, Modifier.weight(1f)); TextButton(onClick = { notice("Заявка одобрена") }) { Text("Одобрить") } } } } } }

@Composable private fun ArchiveView(archive: List<ArchivedLesson>) { LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { item { Text("Архив занятий", fontSize = 22.sp, fontWeight = FontWeight.Bold) }; if (archive.isEmpty()) item { Text("Архив пуст — закрытые занятия появятся здесь", color = MaterialTheme.colorScheme.onSurfaceVariant) }; items(archive) { lesson -> Card { ListItem(headlineContent = { Text("${lesson.date} · ${lesson.subject}") }, supportingContent = { Text("${lesson.group} · ${lesson.students} студентов · Куратор: ${lesson.curator}") }) } } } }
