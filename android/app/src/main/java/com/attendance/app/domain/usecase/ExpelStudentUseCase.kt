package com.attendance.app.domain.usecase

import com.attendance.app.domain.repository.AttendanceRepository
import javax.inject.Inject

class ExpelStudentUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(studentId: String) {
        repository.expelStudent(studentId)
    }
}
