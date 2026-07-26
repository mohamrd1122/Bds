package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MachineEntity
import com.example.data.MachineStatus
import com.example.ui.theme.IndustrialTeal
import com.example.ui.theme.SlateCardBg
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusIdle
import com.example.ui.theme.StatusPaused
import com.example.ui.theme.StatusRunning

@Composable
fun SummaryKpiHeader(
    machines: List<MachineEntity>,
    onStartAll: () -> Unit,
    onPauseAll: () -> Unit,
    onOpenLogs: () -> Unit,
    onBatchSetup: () -> Unit,
    onShareReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = machines.size
    val runningCount = machines.count { it.status == MachineStatus.RUNNING }
    val pausedCount = machines.count { it.status == MachineStatus.PAUSED }
    val idleCount = machines.count { it.status == MachineStatus.IDLE }
    val completedCount = machines.count { it.status == MachineStatus.COMPLETED }

    val activeOrConfigured = machines.count { it.targetHours > 0 }
    val overallProgress = if (activeOrConfigured > 0) {
        machines.map { it.progressFraction }.average().toFloat()
    } else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("summary_kpi_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCardBg),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ملخص خط الإنتاج (21 ماكينة)",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bunching 1 — Bunching 21",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onOpenLogs,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("view_logs_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "السجلات",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("السجلات", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // KPI Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiBadge(
                    label = "تعمل",
                    count = runningCount,
                    color = StatusRunning,
                    icon = Icons.Default.PlayArrow,
                    modifier = Modifier.weight(1f)
                )
                KpiBadge(
                    label = "متوقفة",
                    count = pausedCount,
                    color = StatusPaused,
                    icon = Icons.Default.PauseCircle,
                    modifier = Modifier.weight(1f)
                )
                KpiBadge(
                    label = "خالية",
                    count = idleCount,
                    color = StatusIdle,
                    icon = Icons.Default.Stop,
                    modifier = Modifier.weight(1f)
                )
                KpiBadge(
                    label = "مكتملة",
                    count = completedCount,
                    color = StatusCompleted,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Overall progress
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "نسبة الإنجاز الإجمالية للوردية",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(overallProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusRunning,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { overallProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = StatusRunning,
                    trackColor = SlateCardBorder
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bulk Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onStartAll,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("start_all_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRunning),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "تشغيل الكل",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تشغيل الجاهز", fontSize = 12.sp, color = Color.White)
                }

                OutlinedButton(
                    onClick = onPauseAll,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pause_all_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PauseCircle,
                        contentDescription = "إيقاف الكل",
                        tint = StatusPaused,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إيقاف الكل", fontSize = 12.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Batch & Share Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBatchSetup,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("batch_setup_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = IndustrialTeal),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "إعداد مجموعة ماكينات",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إعداد جماعي", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onShareReport,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_report_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "مشاركة التقرير",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مشاركة التقرير", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun KpiBadge(
    label: String,
    count: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
