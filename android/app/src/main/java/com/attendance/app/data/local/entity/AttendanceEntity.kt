package com.attendance.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.attendance.app.data.local.converter.Converters

@Entity(tableName = "attendance")
@TypeConverters(Converters::class)
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val lessonId: String,
    val status: String,         // AttendanceStatus.name
    val markedByRole: String,   // Role.name
    val approvalChainJson: String = "[]",
    val note: String? = null,
    val timestampMillis: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)
