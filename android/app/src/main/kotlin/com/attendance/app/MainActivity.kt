package com.attendance.app

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AttendanceTheme {
                var currentUser by remember { mutableStateOf<Pair<UserRole, String>?>(null) }
                if (currentUser == null) {
                    LoginScreen(onLogin = { role, name -> currentUser = Pair(role, name) })
                } else {
                    AttendanceScreen(
                        role = currentUser!!.first,
                        userName = currentUser!!.second,
                        onLogout = { currentUser = null }
                    )
                }
            }
        }
    }
}
