package com.attendance.app.data.local.dao

import androidx.room.*
import com.attendance.app.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE lessonId = :lessonId")
    fun observeByLesson(lessonId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE synced = 0")
    suspend fun getUnsynced(): List<AttendanceEntity>

    @Query("""
        SELECT
            s.id           AS studentId,
            s.fullName     AS studentName,
            COUNT(a.id)    AS totalLessons,
            SUM(CASE WHEN a.status = 'PRESENT'          THEN 1 ELSE 0 END) AS present,
            SUM(CASE WHEN a.status = 'ABSENT_EXCUSED'   THEN 1 ELSE 0 END) AS absentExcused,
            SUM(CASE WHEN a.status = 'ABSENT_UNEXCUSED' THEN 1 ELSE 0 END) AS absentUnexcused,
            SUM(CASE WHEN a.status = 'SICK'             THEN 1 ELSE 0 END) AS sick
        FROM students s
        LEFT JOIN attendance a ON a.studentId = s.id
        LEFT JOIN lessons l    ON l.id = a.lessonId
        WHERE s.groupId = :groupId
          AND s.isExpelled = 0
          AND (:from = 0 OR l.dateMillis >= :from)
          AND (:to   = 0 OR l.dateMillis <= :to)
        GROUP BY s.id
        ORDER BY s.fullName
    """)
    fun observeStatistics(
        groupId: String,
        from: Long = 0L,
        to: Long = 0L
    ): Flow<List<StatisticsRaw>>

    @Upsert
    suspend fun upsert(record: AttendanceEntity)

    @Upsert
    suspend fun upsertAll(records: List<AttendanceEntity>)

    @Query("UPDATE attendance SET synced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)
}

data class StatisticsRaw(
    val studentId: String,
    val studentName: String,
    val totalLessons: Int,
    val present: Int,
    val absentExcused: Int,
    val absentUnexcused: Int,
    val sick: Int
)
