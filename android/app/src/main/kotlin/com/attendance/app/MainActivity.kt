package com.attendance.app

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.attendance.app.ui.AttendanceScreen
import com.attendance.app.ui.LoginScreen
import com.attendance.app.ui.UserRole
import com.attendance.app.ui.theme.AttendanceTheme

class MainActivity : ComponentActivity() {
    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); enableEdgeToEdge(); prefs = getSharedPreferences("attendance_prefs", MODE_PRIVATE)
        setContent { AttendanceTheme {
            var current by remember { mutableStateOf<Pair<UserRole, String>?>(null) }
            if (current == null) LoginScreen { role, name -> current = role to name }
            else AttendanceScreen(current!!.first, current!!.second) { current = null; prefs.edit().clear().apply() }
        } }
    }
}
