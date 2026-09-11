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
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        prefs = getSharedPreferences("attendance_prefs", MODE_PRIVATE)

        setContent {
            AttendanceTheme {
                val savedName = prefs.getString("last_user", null)
                val savedRole = prefs.getString("last_role", null)
                    ?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }

                var currentUser by remember {
                    mutableStateOf(
                        if (savedName != null && savedRole != null) Pair(savedRole, savedName)
                        else null
                    )
                }

                if (currentUser == null) {
                    LoginScreen(
                        initialLogin = savedName ?: "",
                        initialRole  = savedRole ?: UserRole.HEADMAN,
                        onLogin = { role, name ->
                            currentUser = Pair(role, name)
                            prefs.edit()
                                .putString("last_user", name)
                                .putString("last_role", role.name)
                                .apply()
                        }
                    )
                } else {
                    AttendanceScreen(
                        role     = currentUser!!.first,
                        userName = currentUser!!.second,
                        onLogout = {
                            currentUser = null
                            prefs.edit().remove("last_user").remove("last_role").apply()
                        }
                    )
                }
            }
        }
    }
}
