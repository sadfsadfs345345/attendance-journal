package com.attendance.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.attendance.desktop.model.ArchivedLesson

@Composable
fun App() {
    var selectedTab by remember { mutableStateOf(0) }; var darkTheme by remember { mutableStateOf(AppSettings.darkTheme) }; var archive by remember { mutableStateOf(listOf<ArchivedLesson>()) }
    val lightColors = lightColorScheme(primary = Color(0xFF4F46E5), primaryContainer = Color(0xFFEEF2FF), secondary = Color(0xFF2E8B57), background = Color(0xFFF4F6FB), surfaceVariant = Color(0xFFF0F2F8), outlineVariant = Color(0xFFE2E6F0))
    val darkColors = darkColorScheme(primary = Color(0xFF818CF8), primaryContainer = Color(0xFF1C1F3D), secondary = Color(0xFF72BC8F), background = Color(0xFF0D0F1C), surface = Color(0xFF141729), surfaceVariant = Color(0xFF1A1E35), outlineVariant = Color(0xFF252A48))
    MaterialTheme(colorScheme = if (darkTheme) darkColors else lightColors) { Surface(Modifier.fillMaxSize()) { Row(Modifier.fillMaxSize()) {
        NavigationRail(containerColor = MaterialTheme.colorScheme.surfaceVariant) { Spacer(Modifier.height(18.dp)); Icon(Icons.Default.List, "Журнал", Modifier.padding(10.dp).size(34.dp), tint = MaterialTheme.colorScheme.primary); HorizontalDivider(Modifier.padding(12.dp)); listOf(Icons.Default.List to "Журнал", Icons.Default.BarChart to "Статистика", Icons.Default.Group to "Студенты", Icons.Default.Mail to "Заявки", Icons.Default.Folder to "Архив", Icons.Default.Settings to "Настройки").forEachIndexed { i, pair -> NavigationRailItem(selected = selectedTab == i, onClick = { selectedTab = i }, icon = { Icon(pair.first, pair.second) }, label = { Text(pair.second) }) } }
        Box(Modifier.fillMaxHeight().width(1.dp).background(MaterialTheme.colorScheme.outlineVariant)); Box(Modifier.fillMaxSize().padding(28.dp)) { when (selectedTab) { 0 -> AttendanceTab { date, subject, students -> archive = archive + ArchivedLesson(date, subject, "ИС-21", students, "Куратор") }; 1 -> StatisticsTab(); 2 -> StudentsTab(); 3 -> RequestsTab(); 4 -> ArchiveTab(archive); 5 -> SettingsTab { darkTheme = it } } }
    } } }
}
