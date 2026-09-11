package com.attendance.desktop

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatisticsTab() {
    val students  = remember { AppSettings.getStudents() }
    val threshold = 0.75f
    val percents  = remember {
        val base = listOf(.92f,.68f,.85f,.55f,.97f,.78f,.60f,.91f,.73f,.88f)
        List(students.size) { i -> base.getOrElse(i) { (65..98).random() / 100f } }
    }
    val avgPct = if (percents.isEmpty()) 0f else percents.average().toFloat()
    val atRisk = percents.count { it < threshold }
    val above  = percents.count { it >= threshold }

    Column(Modifier.fillMaxSize()) {
        Text("Статистика посещаемости",
            fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        // Top ring dashboard row
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Big avg ring
            Card(
                Modifier.weight(1.2f).aspectRatio(1f),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Средняя",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        DesktopRing(
                            pct   = avgPct,
                            color = if (avgPct >= threshold) Color(0xFF43A047) else Color(0xFFE53935),
                            size  = 110.dp,
                            stroke= 12.dp,
                            label = "%.0f%%".format(avgPct * 100)
                        )
                    }
                }
            }
            // Small rings column
            Column(Modifier.weight(2f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MiniRingCard(
                        pct = above.toFloat() / students.size.coerceAtLeast(1),
                        color = Color(0xFF43A047),
                        title = "Норма (≥ 75%)",
                        value = "$above чел.",
                        modifier = Modifier.weight(1f)
                    )
                    MiniRingCard(
                        pct = atRisk.toFloat() / students.size.coerceAtLeast(1),
                        color = Color(0xFFE53935),
                        title = "Под угрозой",
                        value = "$atRisk чел.",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MiniRingCard(
                        pct = 1f,
                        color = MaterialTheme.colorScheme.primary,
                        title = "Всего",
                        value = "${students.size} чел.",
                        modifier = Modifier.weight(1f)
                    )
                    MiniRingCard(
                        pct = if (avgPct >= threshold) 1f else 0f,
                        color = if (avgPct >= threshold) Color(0xFF43A047) else Color(0xFFFB8C00),
                        title = "Порог 75%",
                        value = if (avgPct >= threshold) "✓ Выполнен" else "⚠ Недостаток",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))
        Text("Детализация по студентам",
            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(students.size) { i ->
                val name  = students[i].split(" ").take(2).joinToString(" ")
                val pct   = percents.getOrElse(i) { 0.80f }
                val risk  = pct < threshold
                val color = when { pct >= .85f -> Color(0xFF43A047); pct >= threshold -> Color(0xFFFB8C00); else -> Color(0xFFE53935) }
                var target by remember { mutableFloatStateOf(0f) }
                LaunchedEffect(Unit) { target = pct }
                val anim by animateFloatAsState(target, tween(900 + i * 80), label = "bar")

                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        DesktopRing(pct, color, 56.dp, 6.dp, "%.0f%%".format(pct*100), fontSize = 11)
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(name, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { anim },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = color,
                                trackColor = color.copy(alpha = 0.12f)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        if (risk) Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE53935).copy(0.1f)) {
                            Text("⚠ < 75%", color = Color(0xFFE53935),
                                Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        } else Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF43A047).copy(0.1f)) {
                            Text("✓ OK", color = Color(0xFF43A047),
                                Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DesktopRing(
    pct: Float, color: Color,
    size: Dp = 80.dp, stroke: Dp = 8.dp,
    label: String = "", fontSize: Int = 14
) {
    var target by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) { target = pct.coerceIn(0f, 1f) }
    val anim by animateFloatAsState(target, tween(1000), label = "ring")

    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = stroke.toPx()
            drawArc(Color.Gray.copy(alpha = 0.12f), 0f, 360f, false, style = Stroke(sw, cap = StrokeCap.Round))
            if (anim > 0f)
                drawArc(color, -90f, 360f * anim, false, style = Stroke(sw, cap = StrokeCap.Round))
        }
        Text(label, fontWeight = FontWeight.Bold, fontSize = fontSize.sp, color = color, textAlign = TextAlign.Center)
    }
}

@Composable
fun MiniRingCard(pct: Float, color: Color, title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier.height(90.dp), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.fillMaxSize().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            DesktopRing(pct, color, 50.dp, 5.dp, "")
            Spacer(Modifier.width(8.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
            }
        }
    }
}

@Composable
fun SummaryCard(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(modifier, elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}
