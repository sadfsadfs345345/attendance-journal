package com.attendance.app.data

import android.content.Context

/** Small offline-first store used by the native demo until a backend is connected. */
class NativeAttendanceStore(context: Context) {
    private val prefs = context.getSharedPreferences("attendance_records", Context.MODE_PRIVATE)

    private fun key(date: String, subject: String, student: String) =
        "${date.trim()}|${subject.trim()}|${student.trim()}"

    fun status(date: String, subject: String, student: String, fallback: String): String =
        prefs.getString(key(date, subject, student), fallback) ?: fallback

    fun saveStatus(date: String, subject: String, student: String, value: String) {
        prefs.edit().putString(key(date, subject, student), value).apply()
    }

    fun clearSession(date: String, subject: String, students: List<String>) {
        val editor = prefs.edit()
        students.forEach { editor.remove(key(date, subject, it)) }
        editor.apply()
    }
}
