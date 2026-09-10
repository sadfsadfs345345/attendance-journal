package com.attendance.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendance.app.domain.model.AttendanceRecord
import com.attendance.app.domain.model.AttendanceStatus
import com.attendance.app.domain.model.Statistics
import com.attendance.app.domain.model.Student
import com.attendance.app.domain.usecase.*
import com.attendance.app.domain.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AttendanceUiState(
    val students: List<Student> = emptyList(),
    val attendance: Map<String, AttendanceRecord> = emptyMap(),
    val statistics: List<Statistics> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val exportUri: android.net.Uri? = null
)

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val markAttendance: MarkAttendanceUseCase,
    private val copyFromPrevious: CopyFromPreviousLessonUseCase,
    private val getStatistics: GetStatisticsUseCase,
    private val expelStudent: ExpelStudentUseCase,
    private val exportCsv: ExportCsvUseCase,
    private val repository: AttendanceRepository,
    savedState: SavedStateHandle
) : ViewModel() {

    private val lessonId: String = checkNotNull(savedState["lessonId"])
    private val groupId: String = checkNotNull(savedState["groupId"])

    private val _uiState = MutableStateFlow(AttendanceUiState(isLoading = true))
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.observeStudents(groupId),
                repository.observeAttendance(lessonId),
                repository.observeStatistics(groupId)
            ) { students, records, stats ->
                AttendanceUiState(
                    students = students,
                    attendance = records.associateBy { it.studentId },
                    statistics = stats,
                    isLoading = false
                )
            }.catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun mark(studentId: String, status: AttendanceStatus, note: String? = null) {
        viewModelScope.launch {
            markAttendance(studentId, lessonId, status, note)
        }
    }

    fun copyPrevious() {
        viewModelScope.launch {
            copyFromPrevious(lessonId, groupId)
        }
    }

    fun expel(studentId: String) {
        viewModelScope.launch {
            expelStudent(studentId)
        }
    }

    fun exportToCsv() {
        viewModelScope.launch {
            val stats = _uiState.value.statistics
            val uri = exportCsv.invoke(stats, groupId)
            _uiState.update { it.copy(exportUri = uri) }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
    fun clearExportUri() = _uiState.update { it.copy(exportUri = null) }
}
