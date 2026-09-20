package com.attendance.desktop

import java.util.prefs.Preferences

object AppSettings {
    private val prefs: Preferences = Preferences.userRoot().node("com/attendance/journal")

    var serverUrl: String
        get() = prefs.get("server_url", "https://your-backend.example.com/api/v1/")
        set(v) { prefs.put("server_url", v) }

    var retentionDays: String
        get() = prefs.get("retention_days", "30")
        set(v) { prefs.put("retention_days", v) }

    var darkTheme: Boolean
        get() = prefs.getBoolean("dark_theme", false)
        set(v) { prefs.putBoolean("dark_theme", v) }

    var notifications: Boolean
        get() = prefs.getBoolean("notifications", true)
        set(v) { prefs.putBoolean("notifications", v) }

    fun getStudents(): List<String> {
        val csv = prefs.get("students_csv", "")
        return if (csv.isBlank()) listOf(
            "Иванов Иван Иванович",
            "Петрова Мария Сергеевна",
            "Сидоров Алексей Павлович",
            "Козлова Екатерина Дмитриевна",
            "Новиков Дмитрий Александрович"
        ) else csv.split("\n").filter { it.isNotBlank() }
    }

    fun saveStudents(list: List<String>) {
        prefs.put("students_csv", list.joinToString("\n"))
    }

    /** Offline-first attendance cache. The backend can replace this repository later without changing the UI. */
    fun attendanceKey(date: String, subject: String): String =
        "attendance_${date.trim()}_${subject.trim().replace(Regex("\\s+"), "_")}"

    fun getAttendance(key: String, studentId: Int): Int? {
        val raw = prefs.get("$key.$studentId", "")
        return raw.toIntOrNull()
    }

    fun saveAttendance(key: String, studentId: Int, status: Int?) {
        if (status == null) prefs.remove("$key.$studentId")
        else prefs.putInt("$key.$studentId", status)
    }

    /** Closed lessons are stored locally so the archive survives app restarts. */
    fun getArchivedLessons(): List<ArchivedLesson> {
        val raw = prefs.get("archived_lessons", "")
        if (raw.isBlank()) return emptyList()
        return raw.lineSequence().mapNotNull { line ->
            val parts = line.split("\u001F")
            if (parts.size != 5) null else parts[0].let { date ->
                ArchivedLesson(date, parts[1], parts[2], parts[3].toIntOrNull() ?: 0, parts[4])
            }
        }.toList()
    }

    fun saveArchivedLesson(lesson: ArchivedLesson) {
        val encoded = listOf(lesson.date, lesson.subject, lesson.group, lesson.students.toString(), lesson.curator)
            .joinToString("\u001F")
        val existing = prefs.get("archived_lessons", "")
        if (existing.lineSequence().none { it == encoded }) {
            prefs.put("archived_lessons", listOf(existing, encoded).filter { it.isNotBlank() }.joinToString("\n"))
        }
    }
}
