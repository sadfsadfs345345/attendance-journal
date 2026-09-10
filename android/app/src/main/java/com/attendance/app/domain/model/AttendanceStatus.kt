package com.attendance.app.domain.model

enum class AttendanceStatus(val label: String, val emoji: String) {
    PRESENT("Присутствует", "✓"),
    ABSENT_EXCUSED("Отсутствует (УП)", "УП"),
    ABSENT_UNEXCUSED("Отсутствует (НП)", "НП"),
    SICK("Болен", "🤑")
}
