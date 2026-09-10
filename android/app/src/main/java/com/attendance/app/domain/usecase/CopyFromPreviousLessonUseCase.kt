package com.attendance.app.domain.usecase

import com.attendance.app.domain.repository.AttendanceRepository
import javax.inject.Inject

class CopyFromPreviousLessonUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(currentLessonId: String, groupId: String) {
        repository.copyFromPreviousLesson(currentLessonId, groupId)
    }
}
