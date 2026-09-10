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
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val time: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
)

enum class AttendanceStatus(val label: String, val color: Color) {
    PRESENT("Присутствует", Color(0xFF388E3C)),
    ABSENT("Отсутствует", Color(0xFFD32F2F)),
    LATE("Опоздал", Color(0xFFF57C00))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(role: UserRole, userName: String, onLogout: () -> Unit) {
    val sampleStudents = remember {
        listOf(
            "Иванов Иван", "Петрова Анна", "Сидоров Пётр",
            "Козлова Мария", "Новиков Алексей", "Фёдорова Елена",
            "Морозов Дмитрий", "Волкова Ольга"
        )
    }
    var entries by remember {
        mutableStateOf(sampleStudents.mapIndexed { i, name ->
            AttendanceEntry(id = i.toLong(), name = name, group = "ИС-21")
        })
    }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Журнал", "Статистика")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Журнал посещаемости", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text("${role.name} · $userName", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, contentDescription = "Выход", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> AttendanceList(
                    entries = entries,
                    canEdit = role == UserRole.HEADMAN || role == UserRole.TEACHER,
                    onStatusChange = { id, status ->
                        entries = entries.map { if (it.id == id) it.copy(status = status) else it }
                    }
                )
                1 -> StatisticsTab(entries = entries)
            }
        }
    }
}

@Composable
fun AttendanceList(
    entries: List<AttendanceEntry>,
    canEdit: Boolean,
    onStatusChange: (Long, AttendanceStatus) -> Unit
) {
    val date = SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(Date())
    Column {
        Card(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(date, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                val present = entries.count { it.status == AttendanceStatus.PRESENT }
                Text("$present/${entries.size}", fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
        LazyColumn(contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
            items(entries, key = { it.id }) { entry ->
                AttendanceRow(entry = entry, canEdit = canEdit, onStatusChange = onStatusChange)
            }
        }
    }
}

@Composable
fun AttendanceRow(
    entry: AttendanceEntry,
    canEdit: Boolean,
    onStatusChange: (Long, AttendanceStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = entry.status.color.copy(alpha = 0.15f)
            ) {
                Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    Text(entry.name.first().toString(), fontWeight = FontWeight.Bold,
                        color = entry.status.color, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(entry.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(entry.group, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (canEdit) {
                Box {
                    FilledTonalButton(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = entry.status.color.copy(alpha = 0.12f),
                            contentColor = entry.status.color
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(entry.status.label, fontSize = 11.sp)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        AttendanceStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.label, color = status.color) },
                                onClick = { onStatusChange(entry.id, status); expanded = false }
                            )
                        }
                    }
                }
            } else {
                Text(entry.status.label, color = entry.status.color,
                    fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun StatisticsTab(entries: List<AttendanceEntry>) {
    val present = entries.count { it.status == AttendanceStatus.PRESENT }
    val absent = entries.count { it.status == AttendanceStatus.ABSENT }
    val late = entries.count { it.status == AttendanceStatus.LATE }
    val total = entries.size

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
    val percent = if (total > 0) count.toFloat() / total else 0f
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Text(label, Modifier.weight(1f), fontWeight = FontWeight.Medium)
                Text("$count / $total", fontWeight = FontWeight.Bold, color = color)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { percent },
                modifier = Modifier.fillMaxWidth(),
                color = color
            )
        }
    }
}
