package com.attendance.app.domain.model

data class Lesson(
    val id: String,
    val subject: String,
    val teacherId: String,
    val groupId: String,
    val dateMillis: Long,
    val durationMinutes: Int = 90
)
