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

@Composable
fun App() {
    var selectedTab by remember { mutableStateOf(0) }
    var darkTheme   by remember { mutableStateOf(AppSettings.darkTheme) }

    val lightColors = lightColorScheme(
        primary          = Color(0xFF1565C0),
        onPrimary        = Color.White,
        primaryContainer = Color(0xFFE3F2FD),
        onPrimaryContainer = Color(0xFF0D47A1),
        secondary        = Color(0xFF00695C),
        surface          = Color(0xFFFAFAFA),
        background       = Color(0xFFF1F5F9),
        surfaceVariant   = Color(0xFFECEFF1),
        outlineVariant   = Color(0xFFCFD8DC)
    )
    val darkColors = darkColorScheme(
        primary          = Color(0xFF90CAF9),
        onPrimary        = Color(0xFF0D47A1),
        primaryContainer = Color(0xFF1565C0),
        secondary        = Color(0xFF80CBC4),
        surface          = Color(0xFF1E1E2E),
        background       = Color(0xFF12121E),
        surfaceVariant   = Color(0xFF282838),
        outlineVariant   = Color(0xFF3A3A4A)
    )

    MaterialTheme(colorScheme = if (darkTheme) darkColors else lightColors) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Row(Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Spacer(Modifier.height(24.dp))
                    NavigationRailItem(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.List, null) }, label = { Text("Журнал") })
                    NavigationRailItem(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.BarChart, null) }, label = { Text("Статистика") })
                    NavigationRailItem(selected = selectedTab == 2, onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Group, null) }, label = { Text("Студенты") })
                    NavigationRailItem(selected = selectedTab == 3, onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Настройки") })
                }
                Box(Modifier.fillMaxHeight().width(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant))
                Box(Modifier.fillMaxSize().padding(28.dp)) {
                    when (selectedTab) {
                        0 -> AttendanceTab()
                        1 -> StatisticsTab()
                        2 -> StudentsTab()
                        3 -> SettingsTab(onThemeChange = { darkTheme = it })
                    }
                }
            }
        }
    }
}
