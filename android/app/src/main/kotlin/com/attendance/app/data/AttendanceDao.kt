package com.attendance.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAll(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getForStudent(studentId: Long): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: AttendanceRecord)

    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :id AND status = 'ABSENT'")
    suspend fun countAbsences(id: Long): Int
}
