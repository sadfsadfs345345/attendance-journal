package com.attendance.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val PrimaryBlue   = Color(0xFF1976D2)
val SecondaryTeal = Color(0xFF00897B)
val ErrorRed      = Color(0xFFD32F2F)
val WarningAmber  = Color(0xFFFFA000)
val SuccessGreen  = Color(0xFF388E3C)

private val LightColors = lightColorScheme(
    primary   = PrimaryBlue,
    secondary = SecondaryTeal,
    error     = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary   = Color(0xFF90CAF9),
    secondary = Color(0xFF80CBC4),
    error     = Color(0xFFEF9A9A)
)

@Composable
fun AttendanceTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
