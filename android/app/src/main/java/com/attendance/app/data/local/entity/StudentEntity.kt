package com.attendance.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val groupId: String,
    val isExpelled: Boolean = false,
    val expelledAt: Long? = null
)
