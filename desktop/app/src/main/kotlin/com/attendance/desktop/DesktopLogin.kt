package com.attendance.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.attendance.desktop.model.Role

@Composable
fun DesktopLoginScreen(onLogin: (Role) -> Unit) {
    var role by remember { mutableStateOf(Role.HEADMAN) }
    var expanded by remember { mutableStateOf(false) }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Журнал посещаемости", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Выберите роль для входа в демо", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            Box {
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.width(280.dp)) {
                    Text(role.displayName, modifier = Modifier.weight(1f))
                    Text("⌄", fontSize = 18.sp)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    Role.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = { role = option; expanded = false }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { onLogin(role) }, modifier = Modifier.width(280.dp)) {
                Text("Войти в демо")
            }
        }
    }
}
