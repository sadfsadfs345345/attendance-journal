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
import com.attendance.desktop.model.Role

@Composable
fun App() {
    var activeRole by remember { mutableStateOf<Role?>(null) }
    if (activeRole == null) {
        DesktopLoginScreen(onLogin = { activeRole = it })
        return
    }
    val role = activeRole!!
    var selectedTab by remember(role) { mutableStateOf(0) }
    var darkTheme   by remember { mutableStateOf(AppSettings.darkTheme) }
    var archive     by remember { mutableStateOf(AppSettings.getArchivedLessons()) }

    val lightColors = lightColorScheme(
        primary            = Color(0xFF3F51B5),   // Indigo
        onPrimary          = Color.White,
        primaryContainer   = Color(0xFFE8EAF6),
        onPrimaryContainer = Color(0xFF283593),
        secondary          = Color(0xFF00897B),   // Teal
        tertiary           = Color(0xFFEC407A),   // Pink accent
        surface            = Color(0xFFFFFFFF),
        background         = Color(0xFFF4F5F9),
        surfaceVariant     = Color(0xFFEEEFF5),
        outlineVariant     = Color(0xFFD1D5E8)
    )
    // Dark: inspired by the dashboard reference - deep navy/purple
    val darkColors = darkColorScheme(
        primary            = Color(0xFF7986CB),   // Indigo 300
        onPrimary          = Color(0xFF1A237E),
        primaryContainer   = Color(0xFF3949AB),
        onPrimaryContainer = Color(0xFFE8EAF6),
        secondary          = Color(0xFF80CBC4),
        tertiary           = Color(0xFFF48FB1),
        surface            = Color(0xFF1E1E3A),
        background         = Color(0xFF141428),
        surfaceVariant     = Color(0xFF252545),
        outlineVariant     = Color(0xFF353560)
    )

    val tabLabels = when (role) {
        Role.STUDENT -> listOf("Журнал", "Статистика", "Настройки")
        else -> listOf("Журнал", "Статистика", "Студенты", "Архив", "Настройки")
    }

    MaterialTheme(colorScheme = if (darkTheme) darkColors else lightColors) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Row(Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Spacer(Modifier.height(20.dp))
                    // App icon placeholder
                    Box(
                        Modifier.padding(horizontal = 8.dp)
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primary,
                                androidx.compose.foundation.shape.CircleShape)
                    )
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(Modifier.padding(horizontal = 12.dp))
                    Spacer(Modifier.height(8.dp))

                    tabLabels.forEachIndexed { index, label ->
                        val icon = when (label) {
                            "Журнал" -> Icons.Default.List
                            "Статистика" -> Icons.Default.BarChart
                            "Студенты" -> Icons.Default.Group
                            "Архив" -> Icons.Default.Archive
                            else -> Icons.Default.Settings
                        }
                        NavigationRailItem(selected = selectedTab == index, onClick = { selectedTab = index },
                            icon = { Icon(icon, label) }, label = { Text(label) })
                    }
                }

                Box(Modifier.fillMaxHeight().width(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant))

                Column(Modifier.fillMaxSize().padding(28.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Демо-вход · ${role.displayName}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = { activeRole = null }) { Text("Сменить роль") }
                    }
                    Box(Modifier.fillMaxSize().padding(top = 12.dp)) {
                        when (tabLabels.getOrNull(selectedTab)) {
                            "Журнал" -> AttendanceTab(role, onLessonClosed = { lesson ->
                                AppSettings.saveArchivedLesson(lesson)
                                archive = AppSettings.getArchivedLessons()
                            })
                            "Статистика" -> StatisticsTab()
                            "Студенты" -> StudentsTab()
                            "Архив" -> ArchiveTab(archive)
                            "Настройки" -> SettingsTab(onThemeChange = { darkTheme = it })
                        }
                    }
                }
            }
        }
    }
}
