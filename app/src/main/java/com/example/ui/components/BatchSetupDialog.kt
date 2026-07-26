package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.IndustrialTeal
import com.example.ui.theme.SlateCardBg
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.StatusRunning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BatchSetupDialog(
    totalMachines: Int = 21,
    onDismiss: () -> Unit,
    onApplyBatch: (startId: Int, endId: Int, sizeSpec: String, reelPrefix: String, startReelNum: Int, targetHours: Double, autoStart: Boolean) -> Unit
) {
    var startId by remember { mutableStateOf("1") }
    var endId by remember { mutableStateOf("$totalMachines") }
    var sizeSpec by remember { mutableStateOf("1.5 مم²") }
    var reelPrefix by remember { mutableStateOf("B-") }
    var startReelNum by remember { mutableStateOf("101") }
    var targetHours by remember { mutableDoubleStateOf(8.0) }

    val presetSizes = listOf("0.5 مم²", "0.75 مم²", "1.0 مم²", "1.5 مم²", "2.5 مم²", "4.0 مم²", "6.0 مم²")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .testTag("batch_setup_dialog"),
            color = SlateCardBg,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "إعداد سريع لمجموعة ماكينات",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "تطبيق نفس المقاس والساعات مع ترقيم بكرات متسلسل",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextMuted
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = SlateTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Range selection
                Text(
                    text = "1. نطاق الماكينات (من - إلى)",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startId,
                        onValueChange = { startId = it },
                        label = { Text("من ماكينة") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndustrialTeal,
                            unfocusedBorderColor = SlateCardBorder,
                            focusedContainerColor = SlateDarkBg,
                            unfocusedContainerColor = SlateDarkBg,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = endId,
                        onValueChange = { endId = it },
                        label = { Text("إلى ماكينة") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndustrialTeal,
                            unfocusedBorderColor = SlateCardBorder,
                            focusedContainerColor = SlateDarkBg,
                            unfocusedContainerColor = SlateDarkBg,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Size selection
                Text(
                    text = "2. المقاس المشترك",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = sizeSpec,
                    onValueChange = { sizeSpec = it },
                    placeholder = { Text("المقاس") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndustrialTeal,
                        unfocusedBorderColor = SlateCardBorder,
                        focusedContainerColor = SlateDarkBg,
                        unfocusedContainerColor = SlateDarkBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetSizes.forEach { size ->
                        val isSel = sizeSpec == size
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) IndustrialTeal else SlateDarkBg)
                                .border(1.dp, if (isSel) IndustrialTeal else SlateCardBorder, RoundedCornerShape(8.dp))
                                .clickable { sizeSpec = size }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = size,
                                fontSize = 12.sp,
                                color = if (isSel) Color.White else SlateTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Serial Reel Number Setup
                Text(
                    text = "3. ترقيم البكرات المتسلسل",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = reelPrefix,
                        onValueChange = { reelPrefix = it },
                        label = { Text("بادئة البكرة") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndustrialTeal,
                            unfocusedBorderColor = SlateCardBorder,
                            focusedContainerColor = SlateDarkBg,
                            unfocusedContainerColor = SlateDarkBg,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = startReelNum,
                        onValueChange = { startReelNum = it },
                        label = { Text("رقم البداية") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndustrialTeal,
                            unfocusedBorderColor = SlateCardBorder,
                            focusedContainerColor = SlateDarkBg,
                            unfocusedContainerColor = SlateDarkBg,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Target Hours
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "4. عدد ساعات العمل لكل ماكينة",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$targetHours س",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = IndustrialTeal
                    )
                }
                Slider(
                    value = targetHours.toFloat(),
                    onValueChange = { targetHours = (it * 2).toInt() / 2.0 },
                    valueRange = 1f..24f,
                    steps = 45,
                    colors = SliderDefaults.colors(
                        thumbColor = IndustrialTeal,
                        activeTrackColor = IndustrialTeal,
                        inactiveTrackColor = SlateCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val s = startId.toIntOrNull() ?: 1
                            val e = endId.toIntOrNull() ?: totalMachines
                            val reelNum = startReelNum.toIntOrNull() ?: 100
                            onApplyBatch(s, e, sizeSpec, reelPrefix, reelNum, targetHours, true)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("apply_batch_and_start"),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRunning),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تطبيق وتشغيل الكل", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = {
                            val s = startId.toIntOrNull() ?: 1
                            val e = endId.toIntOrNull() ?: totalMachines
                            val reelNum = startReelNum.toIntOrNull() ?: 100
                            onApplyBatch(s, e, sizeSpec, reelPrefix, reelNum, targetHours, false)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("apply_batch_save_only"),
                        colors = ButtonDefaults.buttonColors(containerColor = IndustrialTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تطبيق فقط", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
