package com.attendance.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "attendance")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val date: Date,
    val status: String, // PRESENT, ABSENT, LATE
    val subject: String,
    val markedBy: String
)
