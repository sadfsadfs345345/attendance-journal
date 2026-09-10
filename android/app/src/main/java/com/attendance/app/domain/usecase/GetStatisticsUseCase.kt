package com.attendance.app.domain.usecase

import com.attendance.app.domain.repository.AttendanceRepository
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    operator fun invoke(groupId: String, from: Long = 0L, to: Long = 0L) =
        repository.observeStatistics(groupId, from, to)
}
