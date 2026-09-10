package com.attendance.app.domain.usecase

import com.attendance.app.domain.model.AttendanceStatus
import com.attendance.app.domain.repository.AttendanceRepository
import javax.inject.Inject

class MarkAttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(studentId: String, lessonId: String, status: AttendanceStatus, note: String? = null) {
        repository.markAttendance(studentId, lessonId, status, note)
    }
}
