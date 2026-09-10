package com.attendance.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    var selectedTab by remember { mutableStateOf(0) }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF1976D2),
            secondary = androidx.compose.ui.graphics.Color(0xFF00897B)
        )
    ) {
        Row(Modifier.fillMaxSize()) {
            NavigationRail(
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(Modifier.height(16.dp))
                NavigationRailItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.List, null) },
                    label = { Text("Журнал") }
                )
                NavigationRailItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.BarChart, null) },
                    label = { Text("Статистика") }
                )
                NavigationRailItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Group, null) },
                    label = { Text("Студенты") }
                )
                NavigationRailItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Настройки") }
                )
            }

            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))

            Box(Modifier.fillMaxSize().padding(24.dp)) {
                when (selectedTab) {
                    0 -> AttendanceTab()
                    1 -> StatisticsTab()
                    2 -> StudentsTab()
                    3 -> SettingsTab()
                }
            }
        }
    }
}
