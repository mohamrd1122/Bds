package com.example.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MachineEntity
import com.example.data.MachineStatus
import com.example.ui.components.BatchSetupDialog
import com.example.ui.components.EditMachineDialog
import com.example.ui.components.MachineCard
import com.example.ui.components.ProductionLogsSheet
import com.example.ui.components.SummaryKpiHeader
import com.example.ui.theme.IndustrialTeal
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.StatusCompleted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MachineViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val rawMachines by viewModel.rawMachines.collectAsStateWithLifecycle()
    val machines by viewModel.filteredMachines.collectAsStateWithLifecycle()
    val logs by viewModel.productionLogs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    var editingMachine by remember { mutableStateOf<MachineEntity?>(null) }
    var showLogsSheet by remember { mutableStateOf(false) }
    var showBatchDialog by remember { mutableStateOf(false) }
    val logsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val completedList = rawMachines.filter { it.status == MachineStatus.COMPLETED }

    fun shareReport() {
        val reportText = viewModel.generateShiftReportText()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, reportText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "مشاركة تقرير الوردية")
        context.startActivity(shareIntent)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SlateDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(IndustrialTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PrecisionManufacturing,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "متابعة ماكينات الإنتاج",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "خطوط Bunching (1 - 21)",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateDarkBg)
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Visual Alert Banner for Completed Machines
            if (completedList.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, StatusCompleted, RoundedCornerShape(16.dp))
                            .testTag("completion_alert_banner"),
                        colors = CardDefaults.cardColors(containerColor = StatusCompleted.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(StatusCompleted),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "تنبيه",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "تنبيه: اكتمل عمل ${completedList.size} ماكينات!",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "الماكينات المكتملة: ${completedList.joinToString(", ") { "#${it.id}" }} - جاهزة لتنزيل البكرات",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StatusCompleted)
                                    .clickable { viewModel.selectedFilter.value = StatusFilter.COMPLETED }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "عرض المكتملة",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // KPI Summary Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                SummaryKpiHeader(
                    machines = rawMachines,
                    onStartAll = { viewModel.startAllConfigured() },
                    onPauseAll = { viewModel.pauseAllRunning() },
                    onOpenLogs = { showLogsSheet = true },
                    onBatchSetup = { showBatchDialog = true },
                    onShareReport = { shareReport() }
                )
            }

            // Search Bar & Filter Chips
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        placeholder = { Text("بحث برقم الماكينة أو المقاس أو رقم البكرة...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SlateTextMuted)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح", tint = SlateTextMuted)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("machine_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndustrialTeal,
                            unfocusedBorderColor = SlateCardBorder,
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Filter Chips Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(StatusFilter.entries) { filter ->
                            val label = when (filter) {
                                StatusFilter.ALL -> "الكل (${rawMachines.size})"
                                StatusFilter.RUNNING -> "تعمل (${rawMachines.count { it.status == MachineStatus.RUNNING }})"
                                StatusFilter.PAUSED -> "متوقفة (${rawMachines.count { it.status == MachineStatus.PAUSED }})"
                                StatusFilter.IDLE -> "خالية (${rawMachines.count { it.status == MachineStatus.IDLE }})"
                                StatusFilter.COMPLETED -> "مكتملة (${rawMachines.count { it.status == MachineStatus.COMPLETED }})"
                            }

                            val isSelected = selectedFilter == filter

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) IndustrialTeal else Color(0xFF1E293B))
                                    .border(1.dp, if (isSelected) IndustrialTeal else SlateCardBorder, RoundedCornerShape(20.dp))
                                    .clickable { viewModel.selectedFilter.value = filter }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("filter_chip_${filter.name.lowercase()}")
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else SlateTextMuted,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Grid of Machine Cards (1 to 21)
            items(machines, key = { it.id }) { machine ->
                MachineCard(
                    machine = machine,
                    onStart = { viewModel.startMachine(machine.id) },
                    onPause = { viewModel.pauseMachine(machine.id) },
                    onReset = { viewModel.resetMachine(machine.id) },
                    onEdit = { editingMachine = machine }
                )
            }
        }
    }

    // Edit Single Machine Dialog
    editingMachine?.let { machine ->
        EditMachineDialog(
            machine = machine,
            onDismiss = { editingMachine = null },
            onSave = { sizeSpec, reelNumber, targetHours, notes, autoStart ->
                viewModel.updateMachineConfig(
                    id = machine.id,
                    sizeSpec = sizeSpec,
                    reelNumber = reelNumber,
                    targetHours = targetHours,
                    notes = notes,
                    autoStart = autoStart
                )
                editingMachine = null
            }
        )
    }

    // Batch Machine Setup Dialog
    if (showBatchDialog) {
        BatchSetupDialog(
            totalMachines = rawMachines.size,
            onDismiss = { showBatchDialog = false },
            onApplyBatch = { startId, endId, sizeSpec, reelPrefix, startReelNum, targetHours, autoStart ->
                viewModel.applyBatchSetup(
                    startId = startId,
                    endId = endId,
                    sizeSpec = sizeSpec,
                    reelPrefix = reelPrefix,
                    startReelNum = startReelNum,
                    targetHours = targetHours,
                    autoStart = autoStart
                )
                showBatchDialog = false
            }
        )
    }

    // Production Logs Sheet
    if (showLogsSheet) {
        ProductionLogsSheet(
            logs = logs,
            sheetState = logsSheetState,
            onDismiss = { showLogsSheet = false },
            onClearLogs = { viewModel.clearHistoryLogs() }
        )
    }
}

