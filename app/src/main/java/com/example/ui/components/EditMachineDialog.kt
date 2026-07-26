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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.MachineEntity
import com.example.ui.theme.IndustrialTeal
import com.example.ui.theme.SlateCardBg
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.StatusRunning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditMachineDialog(
    machine: MachineEntity,
    onDismiss: () -> Unit,
    onSave: (sizeSpec: String, reelNumber: String, targetHours: Double, notes: String, autoStart: Boolean) -> Unit
) {
    var sizeSpec by remember { mutableStateOf(machine.sizeSpec) }
    var reelNumber by remember { mutableStateOf(machine.reelNumber) }
    var targetHours by remember { mutableDoubleStateOf(if (machine.targetHours > 0) machine.targetHours else 8.0) }
    var notes by remember { mutableStateOf(machine.notes) }

    val presetSizes = listOf("0.5 مم²", "0.75 مم²", "1.0 مم²", "1.5 مم²", "2.5 مم²", "4.0 مم²", "6.0 مم²", "10 مم²")
    val presetHours = listOf(2.0, 4.0, 6.0, 8.0, 12.0, 16.0, 24.0)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .testTag("edit_machine_dialog"),
            color = SlateCardBg,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "إعدادات ${machine.name}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "تحديد المقاس ورقم البكرة وساعات العمل",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextMuted
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_edit_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = SlateTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Field 1: المقاس (Wire Size Specification)
                Text(
                    text = "1. المقاس الذي يعمل في الماكينة",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = sizeSpec,
                    onValueChange = { sizeSpec = it },
                    placeholder = { Text("أدخل المقاس (مثال: 1.5 مم²)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("size_spec_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndustrialTeal,
                        unfocusedBorderColor = SlateCardBorder,
                        focusedContainerColor = SlateDarkBg,
                        unfocusedContainerColor = SlateDarkBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Preset Size Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetSizes.forEach { chip ->
                        val isSelected = sizeSpec == chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) IndustrialTeal else SlateDarkBg)
                                .border(1.dp, if (isSelected) IndustrialTeal else SlateCardBorder, RoundedCornerShape(8.dp))
                                .clickable { sizeSpec = chip }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = chip,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else SlateTextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Field 2: رقم البكرة (Reel Number)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. رقم البكرة",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    // Auto Next Reel suggestion
                    TextButton(
                        onClick = {
                            val regex = "(\\d+)".toRegex()
                            val match = regex.find(reelNumber)
                            if (match != null) {
                                val currentNum = match.value.toIntOrNull() ?: 0
                                reelNumber = reelNumber.replaceFirst(match.value, "${currentNum + 1}")
                            } else {
                                reelNumber = "R-101"
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("البكرة التالية", fontSize = 11.sp)
                    }
                }
                OutlinedTextField(
                    value = reelNumber,
                    onValueChange = { reelNumber = it },
                    placeholder = { Text("أدخل رقم البكرة (مثال: R-204)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reel_number_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndustrialTeal,
                        unfocusedBorderColor = SlateCardBorder,
                        focusedContainerColor = SlateDarkBg,
                        unfocusedContainerColor = SlateDarkBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Field 3: عداد عدد الساعات المستهدفة للعمل (Hours Counter)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3. عدد ساعات العمل المستهدفة",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$targetHours ساعة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = IndustrialTeal
                    )
                }

                Slider(
                    value = targetHours.toFloat(),
                    onValueChange = { targetHours = (it * 2).toInt() / 2.0 },
                    valueRange = 0.5f..24f,
                    steps = 46,
                    colors = SliderDefaults.colors(
                        thumbColor = IndustrialTeal,
                        activeTrackColor = IndustrialTeal,
                        inactiveTrackColor = SlateCardBorder
                    ),
                    modifier = Modifier.testTag("hours_slider")
                )

                // Quick preset hours
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetHours.forEach { hrs ->
                        val isSel = targetHours == hrs
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) IndustrialTeal else SlateDarkBg)
                                .border(1.dp, if (isSel) IndustrialTeal else SlateCardBorder, RoundedCornerShape(8.dp))
                                .clickable { targetHours = hrs }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${hrs.toInt()} س",
                                fontSize = 12.sp,
                                color = if (isSel) Color.White else SlateTextMuted,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notes / Operator Field
                Text(
                    text = "ملاحظات إضافية (اختياري)",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("مثال: اسم المشغل / رقم الأمر") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notes_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndustrialTeal,
                        unfocusedBorderColor = SlateCardBorder,
                        focusedContainerColor = SlateDarkBg,
                        unfocusedContainerColor = SlateDarkBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Save & Start immediately
                    Button(
                        onClick = {
                            onSave(sizeSpec, reelNumber, targetHours, notes, true)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_and_start_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRunning),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حفظ وتشغيل", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Save Config only
                    Button(
                        onClick = {
                            onSave(sizeSpec, reelNumber, targetHours, notes, false)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_config_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = IndustrialTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حفظ فقط", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
