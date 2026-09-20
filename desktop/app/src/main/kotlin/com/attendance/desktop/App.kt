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

                    NavigationRailItem(selected = selectedTab==0, onClick = { selectedTab=0 },
                        icon = { Icon(Icons.Default.List, null) }, label = { Text("Журнал") })
                    NavigationRailItem(selected = selectedTab==1, onClick = { selectedTab=1 },
                        icon = { Icon(Icons.Default.BarChart, null) }, label = { Text("Статистика") })
                    NavigationRailItem(selected = selectedTab==2, onClick = { selectedTab=2 },
                        icon = { Icon(Icons.Default.Group, null) }, label = { Text("Студенты") })
                    NavigationRailItem(selected = selectedTab==3, onClick = { selectedTab=3 },
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
