package com.attendance.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendance.app.presentation.theme.ErrorRed
import com.attendance.app.presentation.theme.SuccessGreen
import com.attendance.app.presentation.viewmodel.AttendanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    vm: AttendanceViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика посещаемости", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.statistics, key = { it.studentId }) { stats ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stats.studentName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (stats.isAtRisk) {
                                    Icon(Icons.Default.Warning, null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                }
                                Text(
                                    "%.0f%%".format(stats.attendancePercent),
                                    fontWeight = FontWeight.Bold,
                                    color = if (stats.isAtRisk) ErrorRed else SuccessGreen,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { stats.attendancePercent / 100f },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = if (stats.isAtRisk) ErrorRed else SuccessGreen
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                            StatChip("Всего", stats.totalLessons.toString())
                            StatChip("Прис.", stats.present.toString(), SuccessGreen)
                            StatChip("УП", stats.absentExcused.toString())
                            StatChip("НП", stats.absentUnexcused.toString(), ErrorRed)
                            StatChip("Бол.", stats.sick.toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: String, color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 16.sp)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
