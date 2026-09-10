package com.attendance.app.data.local.dao

import androidx.room.*
import com.attendance.app.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE groupId = :groupId AND isExpelled = 0 ORDER BY fullName")
    fun observeActiveStudents(groupId: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE isExpelled = 1 AND expelledAt < :threshold")
    suspend fun getExpiredExpelledStudents(threshold: Long): List<StudentEntity>

    @Upsert
    suspend fun upsertAll(students: List<StudentEntity>)

    @Query("UPDATE students SET isExpelled = 1, expelledAt = :timestamp WHERE id = :studentId")
    suspend fun markExpelled(studentId: String, timestamp: Long)

    @Query("DELETE FROM students WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)
}
