package com.attendance.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
    PRESENT("Присутствует", "П",  Color(0xFF43A047)),
    ABSENT("Отсутствует", "НП", Color(0xFFE53935)),
    LATE("Опоздал",     "УП", Color(0xFFFB8C00))
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
                    Text("Журнал посещаемости", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("${role.label} · $userName", fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.80f))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White),
            actions = {
                IconButton(onClick = onLogout) {
                    Icon(Icons.Filled.Logout, null, tint = Color.White)
                }
            }
        )
    }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            TabRow(selectedTabIndex = tab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary) {
                listOf("Журнал" to Icons.Filled.List,
                    "Статистика" to Icons.Filled.BarChart,
                    "Настройки" to Icons.Filled.Settings)
                    .forEachIndexed { i, (title, icon) ->
                        Tab(selected = tab == i, onClick = { tab = i },
                            text = { Text(title, fontSize = 12.sp, fontWeight = if(tab==i) FontWeight.Bold else FontWeight.Normal) },
                            icon = { Icon(icon, null, Modifier.size(18.dp)) })
                    }
            }
            when (tab) {
                0 -> AttendanceList(
                    entries = entries,
                    canEdit = role == UserRole.HEADMAN || role == UserRole.TEACHER,
                    onStatusChange = { id, st -> entries = entries.map { if (it.id==id) it.copy(status=st) else it } }
                )
                1 -> ModernStatisticsTab(entries)
                2 -> AndroidSettingsScreen()
            }
        }
    }
}

/* ──────── Attendance List ──────── */

@Composable
fun AttendanceList(entries: List<AttendanceEntry>, canEdit: Boolean,
                   onStatusChange: (Long, AttendanceStatus) -> Unit) {
    val date = SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(Date())
    val present = entries.count { it.status == AttendanceStatus.PRESENT }

    Column {
        // Gradient header card
        Box(
            Modifier.fillMaxWidth()
                .background(Brush.horizontalGradient(
                    listOf(MaterialTheme.colorScheme.primary,
                           MaterialTheme.colorScheme.primary.copy(alpha = 0.75f))))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarToday, null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(date, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.25f)) {
                    Text("$present / ${entries.size} присут.",
                        color = Color.White, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal=12.dp, vertical=5.dp), fontSize=13.sp)
                }
            }
        }

        LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Avatar circle
            Box(
                Modifier.size(44.dp).clip(CircleShape)
                    .background(entry.status.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(entry.name.first().toString(), fontWeight = FontWeight.Bold,
                    color = entry.status.color, fontSize = 18.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(entry.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(entry.group, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f))
            }
            if (canEdit) {
                Box {
                    Surface(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(10.dp),
                        color = entry.status.color.copy(alpha = 0.12f)
                    ) {
                        Row(Modifier.padding(horizontal=12.dp, vertical=7.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(entry.status.short, fontSize = 12.sp,
                                fontWeight = FontWeight.Bold, color = entry.status.color)
                            Icon(Icons.Filled.KeyboardArrowDown, null,
                                Modifier.size(16.dp), tint = entry.status.color)
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        AttendanceStatus.values().forEach { st ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(Modifier.size(10.dp).clip(CircleShape)
                                            .background(st.color))
                                        Spacer(Modifier.width(10.dp))
                                        Text(st.label)
                                    }
                                },
                                onClick = { onStatusChange(entry.id, st); expanded = false }
                            )
                        }
                    }
                }
            } else {
                Surface(shape = RoundedCornerShape(10.dp), color = entry.status.color.copy(0.12f)) {
                    Text(entry.status.label, color = entry.status.color,
                        Modifier.padding(horizontal=10.dp, vertical=5.dp),
                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/* ──────── Statistics ──────── */

@Composable
fun ModernStatisticsTab(entries: List<AttendanceEntry>) {
    val total   = entries.size.coerceAtLeast(1)
    val present = entries.count { it.status == AttendanceStatus.PRESENT }
    val absent  = entries.count { it.status == AttendanceStatus.ABSENT }
    val late    = entries.count { it.status == AttendanceStatus.LATE }
    val presF   = present.toFloat() / total
    val absF    = absent.toFloat()  / total
    val lateF   = late.toFloat()    / total
    val avgF    = presF

    LazyColumn(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Общая статистика", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        // Big circular ring at top
        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(3.dp)) {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(
                            listOf(MaterialTheme.colorScheme.primary.copy(alpha=0.08f), Color.Transparent))),
                    contentAlignment = Alignment.Center
                ) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Посещаемость группы",
                            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(16.dp))
                        CircleRing(pct = presF, color = Color(0xFF43A047), size = 140.dp,
                            stroke = 14.dp, label = "%.0f%%".format(presF*100),
                            sub = "$present / $total")
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            LegendDot("Присут.", Color(0xFF43A047), present)
                            LegendDot("Отсут.", Color(0xFFE53935), absent)
                            LegendDot("Опоздал", Color(0xFFFB8C00), late)
                        }
                    }
                }
            }
        }
        // Detail rings row
        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(3.dp)) {
                Row(Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    CircleRing(absF,  Color(0xFFE53935), 88.dp, 10.dp, "%.0f%%".format(absF*100), "Отсутствует")
                    CircleRing(lateF, Color(0xFFFB8C00), 88.dp, 10.dp, "%.0f%%".format(lateF*100), "Опоздал")
                    CircleRing(if(presF>=0.75f)1f else presF/0.75f,
                        if(presF>=0.75f) Color(0xFF43A047) else Color(0xFFE53935),
                        88.dp, 10.dp,
                        if(presF>=0.75f) "✓ OK" else "⚠ < 75",
                        "Порог 75%")
                }
            }
        }
        // Per-student cards
        items(entries) { entry ->
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(1.dp)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(36.dp).clip(CircleShape)
                        .background(entry.status.color.copy(alpha=0.15f)),
                        contentAlignment = Alignment.Center) {
                        Text(entry.name.first().toString(),
                            fontWeight = FontWeight.Bold, color = entry.status.color)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(entry.name, Modifier.weight(1f), fontWeight = FontWeight.Medium)
                    Surface(shape = RoundedCornerShape(8.dp), color = entry.status.color.copy(0.12f)) {
                        Text(entry.status.label, color = entry.status.color,
                            Modifier.padding(horizontal=8.dp, vertical=3.dp),
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun CircleRing(
    pct: Float, color: Color,
    size: Dp = 100.dp, stroke: Dp = 10.dp,
    label: String = "", sub: String = ""
) {
    var target by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) { target = pct.coerceIn(0f, 1f) }
    val animated by animateFloatAsState(target, tween(1000), label = "ring")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(size), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val sw = stroke.toPx()
                drawArc(Color.Gray.copy(alpha=0.12f), 0f, 360f, false, style = Stroke(sw, cap = StrokeCap.Round))
                if (animated > 0f)
                    drawArc(color, -90f, 360f * animated, false, style = Stroke(sw, cap = StrokeCap.Round))
            }
            Text(label, fontWeight = FontWeight.Bold,
                fontSize = if (size >= 120.dp) 20.sp else 13.sp, color = color)
        }
        if (sub.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(sub, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun LegendDot(label: String, color: Color, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text("$label: $count", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.75f))
    }
}
