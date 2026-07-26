package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MachineEntity
import com.example.data.MachineStatus
import com.example.ui.theme.SlateCardBg
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusIdle
import com.example.ui.theme.StatusPaused
import com.example.ui.theme.StatusRunning
import java.util.Locale

@Composable
fun MachineCard(
    machine: MachineEntity,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = when (machine.status) {
            MachineStatus.RUNNING -> StatusRunning
            MachineStatus.PAUSED -> StatusPaused
            MachineStatus.IDLE -> StatusIdle
            MachineStatus.COMPLETED -> StatusCompleted
        },
        label = "status_color"
    )

    val statusText = when (machine.status) {
        MachineStatus.RUNNING -> "تعمل الآن"
        MachineStatus.PAUSED -> "متوقفة مؤقتاً"
        MachineStatus.IDLE -> "خالية"
        MachineStatus.COMPLETED -> "مكتملة"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("machine_card_${machine.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCardBg),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(statusColor.copy(alpha = 0.5f)))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Card Header: Machine Name & Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.2f))
                            .border(1.dp, statusColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${machine.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = machine.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "ماكينة رقم ${machine.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Status Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main 3 Required Fields: Size Spec (المقاس), Reel Number (رقم البكرة), Target Hours (ساعات العمل)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.6f))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Field 1: المقاس (Size)
                InfoBox(
                    title = "المقاس",
                    value = if (machine.sizeSpec.isNotBlank()) machine.sizeSpec else "غير محدد",
                    highlight = machine.sizeSpec.isNotBlank(),
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(SlateCardBorder)
                )

                // Field 2: رقم البكرة (Reel #)
                InfoBox(
                    title = "رقم البكرة",
                    value = if (machine.reelNumber.isNotBlank()) machine.reelNumber else "غير محدد",
                    highlight = machine.reelNumber.isNotBlank(),
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(SlateCardBorder)
                )

                // Field 3: عداد الساعات (Target Hours)
                InfoBox(
                    title = "الساعات المستهدفة",
                    value = if (machine.targetHours > 0) "${formatHours(machine.targetHours)} س" else "غير محدد",
                    highlight = machine.targetHours > 0,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timer & Progress Display
            val remSec = machine.remainingSeconds
            val progress = machine.progressFraction

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (machine.status == MachineStatus.COMPLETED) "اكتمل التشغيل بالكامل" else "الوقت المتبقي للعمل",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = formatDigitalTime(remSec),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = if (machine.status == MachineStatus.COMPLETED) StatusCompleted else if (remSec < 1800 && machine.status == MachineStatus.RUNNING) StatusPaused else Color.White
                    )
                    if (machine.status == MachineStatus.RUNNING && remSec > 0) {
                        Text(
                            text = formatEstimatedFinishTime(remSec),
                            style = MaterialTheme.typography.bodySmall,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}% مكتمل",
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = SlateCardBorder
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Control Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Start / Pause Primary Button
                val isRunning = machine.status == MachineStatus.RUNNING
                val canStart = machine.targetHours > 0 && machine.sizeSpec.isNotBlank() && machine.reelNumber.isNotBlank()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isRunning) StatusPaused else if (canStart) StatusRunning else StatusIdle.copy(alpha = 0.3f)
                        )
                        .clickable(enabled = canStart || isRunning) {
                            if (isRunning) onPause() else onStart()
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "إيقاف" else "تشغيل",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRunning) "إيقاف مؤقت" else "تشغيل",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Edit Settings Button (المقاس / البكرة / الساعات)
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("edit_machine_${machine.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "تعديل الخانات",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تعديل الخانات", fontSize = 12.sp, color = Color.White)
                }

                // Reset Button
                IconButton(
                    onClick = onReset,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A))
                        .testTag("reset_machine_${machine.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "إعادة ضبط",
                        tint = SlateTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoBox(
    title: String,
    value: String,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = SlateTextMuted,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (highlight) Color.White else SlateTextMuted,
            fontSize = 13.sp
        )
    }
}

private fun formatHours(hours: Double): String {
    return if (hours % 1.0 == 0.0) {
        hours.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", hours)
    }
}

private fun formatDigitalTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
}

private fun formatEstimatedFinishTime(remainingSeconds: Long): String {
    if (remainingSeconds <= 0) return ""
    val finishTimeMillis = System.currentTimeMillis() + (remainingSeconds * 1000L)
    val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale("ar"))
    return "الانتهاء المتوقع: ${sdf.format(java.util.Date(finishTimeMillis))}"
}
