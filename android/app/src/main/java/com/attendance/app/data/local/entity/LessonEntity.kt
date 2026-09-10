package com.attendance.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val teacherId: String,
    val groupId: String,
    val dateMillis: Long,
    val durationMinutes: Int = 90
)
