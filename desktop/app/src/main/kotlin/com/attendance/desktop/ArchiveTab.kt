package com.attendance.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ArchivedLesson(
    val date: String,
    val subject: String,
    val group: String,
    val students: Int,
    val curator: String
)

@Composable
fun ArchiveTab(archive: List<ArchivedLesson>) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Архив занятий", fontSize = 22.sp) }
        if (archive.isEmpty()) {
            item { Text("Архив пуст — закрытые занятия появятся здесь", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(archive) { lesson ->
            Card(Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("${lesson.date} · ${lesson.subject}") },
                    supportingContent = { Text("${lesson.group} · ${lesson.students} студентов · Куратор: ${lesson.curator}") }
                )
            }
        }
    }
}
