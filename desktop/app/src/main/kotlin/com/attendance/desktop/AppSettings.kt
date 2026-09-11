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
}
