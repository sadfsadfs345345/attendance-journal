package com.attendance.desktop.model

enum class Role(val displayName: String) {
    HEADMAN("Староста"),
    TEACHER("Учитель"),
    CURATOR("Куратор"),
    DIRECTOR("Директор")
}

enum class AttendanceStatus(val label: String, val emoji: String) {
    PRESENT("Присутствует", "✓"),
    ABSENT_EXCUSED("Отсутствует (УП)", "УП"),
    ABSENT_UNEXCUSED("Отсутствует (НП)", "НП"),
    SICK("Болен", "🤑")
}

data class Student(
    val id: String,
    val fullName: String,
    val groupId: String,
    val isExpelled: Boolean = false
)

data class AttendanceRecord(
    val studentId: String,
    val lessonId: String,
    val status: AttendanceStatus,
    val markedByRole: Role,
    val note: String? = null,
    val timestampMillis: Long = System.currentTimeMillis()
)

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
        get() = if (totalLessons == 0) 100f else (present.toFloat() / totalLessons) * 100f
    val isAtRisk: Boolean get() = attendancePercent < 75f
}
