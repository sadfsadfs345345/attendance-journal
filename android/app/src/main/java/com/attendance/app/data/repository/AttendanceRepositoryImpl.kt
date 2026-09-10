package com.attendance.app.data.repository

import com.attendance.app.data.local.dao.AttendanceDao
import com.attendance.app.data.local.dao.LessonDao
import com.attendance.app.data.local.dao.StudentDao
import com.attendance.app.data.local.entity.AttendanceEntity
import com.attendance.app.data.remote.api.AttendanceApi
import com.attendance.app.data.remote.dto.AttendanceDto
import com.attendance.app.data.remote.dto.SyncRequest
import com.attendance.app.domain.model.*
import com.attendance.app.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val studentDao: StudentDao,
    private val lessonDao: LessonDao,
    private val api: AttendanceApi
) : AttendanceRepository {

    override fun observeStudents(groupId: String) =
        studentDao.observeActiveStudents(groupId).map { list ->
            list.map { e -> Student(e.id, e.fullName, e.groupId, e.isExpelled, e.expelledAt) }
        }

    override fun observeAttendance(lessonId: String): Flow<List<AttendanceRecord>> =
        attendanceDao.observeByLesson(lessonId).map { list ->
            list.map { e ->
                AttendanceRecord(
                    id = e.id,
                    studentId = e.studentId,
                    lessonId = e.lessonId,
                    status = AttendanceStatus.valueOf(e.status),
                    markedByRole = Role.valueOf(e.markedByRole),
                    note = e.note,
                    timestampMillis = e.timestampMillis
                )
            }
        }

    override fun observeStatistics(groupId: String, from: Long, to: Long): Flow<List<Statistics>> =
        attendanceDao.observeStatistics(groupId, from, to).map { list ->
            list.map { raw ->
                Statistics(
                    studentId = raw.studentId,
                    studentName = raw.studentName,
                    totalLessons = raw.totalLessons,
                    present = raw.present,
                    absentExcused = raw.absentExcused,
                    absentUnexcused = raw.absentUnexcused,
                    sick = raw.sick
                )
            }
        }

    override suspend fun markAttendance(
        studentId: String,
        lessonId: String,
        status: AttendanceStatus,
        note: String?
    ) {
        val entity = AttendanceEntity(
            id = UUID.randomUUID().toString(),
            studentId = studentId,
            lessonId = lessonId,
            status = status.name,
            markedByRole = Role.HEADMAN.name,
            note = note,
            synced = false
        )
        attendanceDao.upsert(entity)
    }

    override suspend fun copyFromPreviousLesson(currentLessonId: String, groupId: String) {
        val previous = lessonDao.getLatest(groupId) ?: return
        val previousRecords = attendanceDao.getUnsynced()
            .filter { it.lessonId == previous.id }
        val copies = previousRecords.map { it.copy(id = UUID.randomUUID().toString(), lessonId = currentLessonId, synced = false) }
        attendanceDao.upsertAll(copies)
    }

    override suspend fun syncPending() {
        val unsynced = attendanceDao.getUnsynced()
        if (unsynced.isEmpty()) return
        val dtos = unsynced.map { e ->
            AttendanceDto(
                id = e.id, studentId = e.studentId, lessonId = e.lessonId,
                status = e.status, markedByRole = e.markedByRole,
                note = e.note, timestampMillis = e.timestampMillis
            )
        }
        val response = api.sync(SyncRequest(dtos))
        attendanceDao.markSynced(response.syncedIds)
    }

    override suspend fun expelStudent(studentId: String) {
        studentDao.markExpelled(studentId, System.currentTimeMillis())
        api.deleteStudent(studentId)
    }

    override suspend fun getLesson(lessonId: String) =
        lessonDao.getById(lessonId)?.let { Lesson(it.id, it.subject, it.teacherId, it.groupId, it.dateMillis, it.durationMinutes) }

    override suspend fun getPreviousLesson(groupId: String) =
        lessonDao.getLatest(groupId)?.let { Lesson(it.id, it.subject, it.teacherId, it.groupId, it.dateMillis, it.durationMinutes) }
}
