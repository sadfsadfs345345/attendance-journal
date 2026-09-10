package com.attendance.app.domain.model

data class Statistics(
    val studentId: String,
    val studentName: String,
    val totalLessons: Int,
    val present: Int,
    val absentExcused: Int,
    val absentUnexcused: Int,
    val sick: Int
) {
    val attendancePercent: Float
        get() = if (totalLessons == 0) 100f
                else (present.toFloat() / totalLessons) * 100f

    val isAtRisk: Boolean get() = attendancePercent < 75f
}
