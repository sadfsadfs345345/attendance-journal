package com.attendance.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class UserRole(val label: String) {
    HEADMAN("Пользователь"),
    TEACHER("Учитель"),
    CURATOR("Куратор"),
    DIRECTOR("Директор")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    initialLogin: String = "",
    initialRole: UserRole = UserRole.HEADMAN,
    onLogin: (UserRole, String) -> Unit
) {
    var login       by remember { mutableStateOf(initialLogin) }
    var password    by remember { mutableStateOf("") }
    var pwdVisible  by remember { mutableStateOf(false) }
    var selRole     by remember { mutableStateOf(initialRole) }
    var expanded    by remember { mutableStateOf(false) }
    var error       by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Gradient header
        Box(
            Modifier.fillMaxWidth().height(220.dp).background(
                Brush.verticalGradient(listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.80f)
                ))
            ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.School, null, Modifier.size(64.dp), tint = Color.White)
                Spacer(Modifier.height(12.dp))
                Text("Журнал посещаемости",
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Система учёта успеваемости",
                    fontSize = 14.sp, color = Color.White.copy(alpha = 0.82f))
            }
        }

        Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Вход в систему", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

            OutlinedTextField(value = login, onValueChange = { login = it; error = "" },
                label = { Text("Логин") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = error.isNotEmpty() && login.isBlank(), singleLine = true)

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = if (pwdVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { pwdVisible = !pwdVisible }) {
                        Icon(if (pwdVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                    }
                },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(value = selRole.label, onValueChange = {}, readOnly = true,
                    label = { Text("Роль") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp))
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    UserRole.entries.forEach { role ->
                        DropdownMenuItem(text = { Text(role.label) },
                            onClick = { selRole = role; expanded = false })
                    }
                }
            }

            if (error.isNotEmpty())
                Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { if (login.isBlank()) error = "Введите логин" else onLogin(selRole, login) },
                modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(12.dp)
            ) { Text("Войти", fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
        }
    }
}
