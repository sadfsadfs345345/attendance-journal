package com.attendance.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.attendance.app.data.local.dao.AttendanceDao
import com.attendance.app.data.local.dao.LessonDao
import com.attendance.app.data.local.dao.StudentDao
import com.attendance.app.data.local.entity.AttendanceEntity
import com.attendance.app.data.local.entity.LessonEntity
import com.attendance.app.data.local.entity.StudentEntity

@Database(
    entities = [StudentEntity::class, LessonEntity::class, AttendanceEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun lessonDao(): LessonDao
    abstract fun attendanceDao(): AttendanceDao
}
