package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
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
import com.example.data.ProductionLogEntity
import com.example.ui.theme.SlateCardBg
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.StatusCompleted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionLogsSheet(
    logs: List<ProductionLogEntity>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onClearLogs: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SlateDarkBg,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("production_logs_sheet")
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "سجل الإنتاج المكتمل",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "إجمالي ${logs.size} بكرة مكتملة",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (logs.isNotEmpty()) {
                        IconButton(
                            onClick = onClearLogs,
                            modifier = Modifier.testTag("clear_logs_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "مسح السجلات",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = SlateTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (logs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SlateTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا توجد بكرات مكتملة في السجل حتى الآن",
                            color = SlateTextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(400.dp)
                ) {
                    items(logs, key = { it.logId }) { log ->
                        LogCard(log = log)
                    }
                }
            }
        }
    }
}

@Composable
private fun LogCard(log: ProductionLogEntity) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale("ar"))
    val formattedDate = dateFormat.format(Date(log.completedTimestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCardBg),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = log.machineName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(StatusCompleted.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "بكرة رقم: ${log.reelNumber}",
                            color = StatusCompleted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "المقاس: ${log.sizeSpec}",
                        color = SlateTextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "مدة العمل: ${formatHours(log.actualHoursWorked)} ساعة",
                        color = SlateTextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formattedDate,
                    color = SlateTextMuted.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

private fun formatHours(hours: Double): String {
    return String.format(Locale.US, "%.1f", hours)
}
