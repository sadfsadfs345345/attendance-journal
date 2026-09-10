package com.attendance.app.presentation.screen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendance.app.domain.model.AttendanceStatus
import com.attendance.app.domain.model.Student
import com.attendance.app.presentation.viewmodel.AttendanceViewModel
import com.attendance.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkAttendanceScreen(
    vm: AttendanceViewModel = hiltViewModel(),
    onOpenStats: (groupId: String) -> Unit
) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current
    var expandedStudentId by remember { mutableStateOf<String?>(null) }
    var showExpelDialog by remember { mutableStateOf<Student?>(null) }

    // Share CSV
    LaunchedEffect(state.exportUri) {
        state.exportUri?.let { uri ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Экспорт посещаемости"))
            vm.clearExportUri()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Журнал посещаемости", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { vm.copyPrevious() }) {
                        Icon(Icons.Default.ContentCopy, "Скопировать с прошлого занятия")
                    }
                    IconButton(onClick = { vm.exportToCsv() }) {
                        Icon(Icons.Default.FileDownload, "Экспорт CSV")
                    }
                    IconButton(onClick = { onOpenStats("group_demo") }) {
                        Icon(Icons.Default.BarChart, "Статистика")
                    }
                }
            )
        },
        snackbarHost = {
            state.error?.let { err ->
                Snackbar(action = { TextButton(onClick = vm::clearError) { Text("OK") } }) {
                    Text(err)
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(state.students, key = { it.id }) { student ->
                    val record = state.attendance[student.id]
                    val stats  = state.statistics.find { it.studentId == student.id }

                    StudentAttendanceCard(
                        student   = student,
                        status    = record?.status,
                        percent   = stats?.attendancePercent ?: 100f,
                        isAtRisk  = stats?.isAtRisk ?: false,
                        expanded  = expandedStudentId == student.id,
                        onExpand  = {
                            expandedStudentId = if (expandedStudentId == student.id) null else student.id
                        },
                        onMark    = { status -> vm.mark(student.id, status) },
                        onExpel   = { showExpelDialog = student }
                    )
                }
            }
        }
    }

    showExpelDialog?.let { student ->
        AlertDialog(
            onDismissRequest = { showExpelDialog = null },
            title = { Text("Отчислить студента?") },
            text  = { Text("${student.fullName} будет помечен как отчисленный. Данные удалятся через 30 дней.") },
            confirmButton = {
                TextButton(onClick = { vm.expel(student.id); showExpelDialog = null }) {
                    Text("Отчислить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExpelDialog = null }) { Text("Отмена") }
            }
        )
    }
}

@Composable
fun StudentAttendanceCard(
    student:  Student,
    status:   AttendanceStatus?,
    percent:  Float,
    isAtRisk: Boolean,
    expanded: Boolean,
    onExpand: () -> Unit,
    onMark:   (AttendanceStatus) -> Unit,
    onExpel:  () -> Unit
) {
    val bgColor = when (status) {
        AttendanceStatus.PRESENT          -> SuccessGreen.copy(alpha = 0.08f)
        AttendanceStatus.ABSENT_EXCUSED   -> WarningAmber.copy(alpha = 0.08f)
        AttendanceStatus.ABSENT_UNEXCUSED -> ErrorRed.copy(alpha = 0.08f)
        AttendanceStatus.SICK             -> Color(0xFF7B1FA2).copy(alpha = 0.08f)
        null -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onExpand() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(bgColor)
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f)) {
                    Text(student.fullName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "%.0f%%".format(percent),
                            fontSize = 12.sp,
                            color = if (isAtRisk) ErrorRed else SuccessGreen
                        )
                        if (isAtRisk) {
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Warning, "В зоне риска",
                                tint = ErrorRed, modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
                Text(
                    text = status?.emoji ?: "—",
                    fontSize = 22.sp
                )
            }

            if (expanded) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider()
                Spacer(Modifier.height(10.dp))
                Text("Отметить:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AttendanceStatus.entries.forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick  = { onMark(s) },
                            label    = { Text(s.emoji, fontSize = 14.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = onExpel,
                    colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                ) {
                    Icon(Icons.Default.PersonRemove, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Отчислить", fontSize = 13.sp)
                }
            }
        }
    }
}
