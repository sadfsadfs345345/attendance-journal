package com.attendance.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

data class AttendanceEntry(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val group: String,
    val status: AttendanceStatus = AttendanceStatus.PRESENT
)

enum class AttendanceStatus(val label: String, val short: String, val color: Color) {
    PRESENT("Присутствует", "П",  Color(0xFF388E3C)),
    ABSENT("Отсутствует", "НП", Color(0xFFD32F2F)),
    LATE("Опоздал",     "УП", Color(0xFFF57C00))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(role: UserRole, userName: String, onLogout: () -> Unit) {
    val students = remember {
        listOf("Иванов Иван","Петрова Анна","Сидоров Пётр",
            "Козлова Мария","Новиков Алексей","Фёдорова Елена",
            "Морозов Дмитрий","Волкова Ольга")
    }
    var entries by remember {
        mutableStateOf(students.mapIndexed { i, n ->
            AttendanceEntry(id = i.toLong(), name = n, group = "ИС-21")
        })
    }
    var tab by remember { mutableIntStateOf(0) }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Column {
                    Text("Журнал посещаемости", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("${role.label} · $userName", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White),
            actions = {
                IconButton(onClick = onLogout) {
                    Icon(Icons.Filled.Logout, "Выход", tint = Color.White)
                }
            }
        )
    }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            TabRow(selectedTabIndex = tab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                listOf("Журнал", "Статистика", "Настройки")
                    .forEachIndexed { i, title ->
                        Tab(selected = tab == i, onClick = { tab = i },
                            text = { Text(title, fontSize = 13.sp) },
                            icon = {
                                Icon(when(i) { 0->Icons.Filled.List; 1->Icons.Filled.BarChart
                                    else->Icons.Filled.Settings }, null, Modifier.size(18.dp))
                            })
                    }
            }
            when (tab) {
                0 -> AttendanceList(
                    entries = entries,
                    canEdit = role == UserRole.HEADMAN || role == UserRole.TEACHER,
                    onStatusChange = { id, st -> entries = entries.map { if (it.id==id) it.copy(status=st) else it } }
                )
                1 -> StatisticsTab(entries)
                2 -> AndroidSettingsScreen()
            }
        }
    }
}

@Composable
fun AttendanceList(entries: List<AttendanceEntry>, canEdit: Boolean,
                   onStatusChange: (Long, AttendanceStatus) -> Unit) {
    val date = SimpleDateFormat("dd MMMM yyyy, EEEE", Locale("ru")).format(Date())
    Column {
        Card(Modifier.fillMaxWidth().padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarToday, null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(date, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primary) {
                    val present = entries.count { it.status == AttendanceStatus.PRESENT }
                    Text("$present/${entries.size}", fontWeight = FontWeight.Bold, color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 13.sp)
                }
            }
        }
        LazyColumn(contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
            items(entries, key = { it.id }) { entry ->
                AttendanceRow(entry, canEdit, onStatusChange)
            }
        }
    }
}

@Composable
fun AttendanceRow(entry: AttendanceEntry, canEdit: Boolean,
                  onStatusChange: (Long, AttendanceStatus) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth().padding(vertical = 3.dp),
        shape = RoundedCornerShape(10.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(20.dp), color = entry.status.color.copy(alpha = 0.15f)) {
                Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    Text(entry.name.first().toString(), fontWeight = FontWeight.Bold,
                        color = entry.status.color, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(entry.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(entry.group, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (canEdit) {
                Box {
                    FilledTonalButton(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = entry.status.color.copy(alpha = 0.12f),
                            contentColor = entry.status.color),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(entry.status.short, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Filled.ArrowDropDown, null, Modifier.size(14.dp))
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        AttendanceStatus.values().forEach { st ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = RoundedCornerShape(4.dp),
                                            color = st.color.copy(alpha = 0.15f)) {
                                            Text(st.short, Modifier.padding(horizontal=6.dp,vertical=2.dp),
                                                color=st.color, fontSize=12.sp, fontWeight=FontWeight.Bold)
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Text(st.label, color = st.color)
                                    }
                                },
                                onClick = { onStatusChange(entry.id, st); expanded = false }
                            )
                        }
                    }
                }
            } else {
                Surface(shape = RoundedCornerShape(8.dp), color = entry.status.color.copy(alpha = 0.12f)) {
                    Text(entry.status.label, color = entry.status.color,
                        modifier = Modifier.padding(horizontal=8.dp, vertical=4.dp),
                        fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun StatisticsTab(entries: List<AttendanceEntry>) {
    val present = entries.count { it.status == AttendanceStatus.PRESENT }
    val absent  = entries.count { it.status == AttendanceStatus.ABSENT }
    val late    = entries.count { it.status == AttendanceStatus.LATE }
    val total   = entries.size
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Статистика за сегодня", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        StatCard("✅ Присутствует", present, total, Color(0xFF388E3C))
        Spacer(Modifier.height(8.dp))
        StatCard("❌ Отсутствует", absent, total, Color(0xFFD32F2F))
        Spacer(Modifier.height(8.dp))
        StatCard("⏰ Опоздал", late, total, Color(0xFFF57C00))
    }
}

@Composable
fun StatCard(label: String, count: Int, total: Int, color: Color) {
    val pct = if (total > 0) count.toFloat() / total else 0f
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row {
                Text(label, Modifier.weight(1f), fontWeight = FontWeight.Medium)
                Text("$count / $total", fontWeight = FontWeight.Bold, color = color)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = { pct }, modifier = Modifier.fillMaxWidth().height(8.dp), color = color)
            Text("%.0f%%".format(pct * 100), fontSize = 12.sp, color = color, fontWeight = FontWeight.Medium)
        }
    }
}
