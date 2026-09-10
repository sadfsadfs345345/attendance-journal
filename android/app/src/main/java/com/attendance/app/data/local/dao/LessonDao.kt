package com.attendance.app.data.local.dao

import androidx.room.*
import com.attendance.app.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE groupId = :groupId ORDER BY dateMillis DESC")
    fun observeLessons(groupId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): LessonEntity?

    @Query("SELECT * FROM lessons WHERE groupId = :groupId ORDER BY dateMillis DESC LIMIT 1")
    suspend fun getLatest(groupId: String): LessonEntity?

    @Upsert
    suspend fun upsert(lesson: LessonEntity)

    @Upsert
    suspend fun upsertAll(lessons: List<LessonEntity>)
}
