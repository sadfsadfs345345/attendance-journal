package com.attendance.app.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class UserRole(val label: String) { STUDENT("Студент"), HEADMAN("Староста"), TEACHER("Учитель"), CURATOR("Куратор"), DIRECTOR("Директор") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(initialLogin: String = "", initialRole: UserRole = UserRole.HEADMAN, onLogin: (UserRole, String) -> Unit) {
    var login by remember { mutableStateOf(initialLogin) }; var password by remember { mutableStateOf("") }; var visible by remember { mutableStateOf(false) }; var role by remember { mutableStateOf(initialRole) }; var expanded by remember { mutableStateOf(false) }; var error by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.School, "Журнал посещаемости", Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp)); Text("Журнал посещаемости", fontSize = 24.sp, fontWeight = FontWeight.Bold); Text("Система учёта посещаемости студентов", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(28.dp)); Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            AssistChip(onClick = {}, label = { Text("ДЕМО-РЕЖИМ") })
            OutlinedTextField(login, { login = it; error = "" }, label = { Text("Email или ФИО") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(password, { password = it }, label = { Text("Пароль") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton({ visible = !visible }) { Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Показать пароль") } })
            ExposedDropdownMenuBox(expanded, { expanded = !expanded }) { OutlinedTextField(role.label, {}, readOnly = true, label = { Text("Роль для демо-входа") }, modifier = Modifier.menuAnchor().fillMaxWidth()); ExposedDropdownMenu(expanded, { expanded = false }) { UserRole.entries.forEach { r -> DropdownMenuItem(text = { Text(r.label) }, onClick = { role = r; expanded = false }) } } }
            if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
            Button(onClick = { if (login.isBlank() || password.isBlank()) error = "Введите email и пароль" else onLogin(role, login) }, Modifier.fillMaxWidth().height(52.dp)) { Text("Войти в систему") }
        } }
    }
}
