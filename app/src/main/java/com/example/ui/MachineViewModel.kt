package com.example.ui

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.MachineEntity
import com.example.data.MachineRepository
import com.example.data.MachineStatus
import com.example.data.ProductionLogEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class StatusFilter {
    ALL,
    RUNNING,
    PAUSED,
    IDLE,
    COMPLETED
}

class MachineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MachineRepository
    
    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow(StatusFilter.ALL)
    
    private val _rawMachines = MutableStateFlow<List<MachineEntity>>(emptyList())
    val rawMachines: StateFlow<List<MachineEntity>> = _rawMachines.asStateFlow()

    val filteredMachines: StateFlow<List<MachineEntity>> = combine(
        _rawMachines,
        searchQuery,
        selectedFilter
    ) { machines, query, filter ->
        machines.filter { machine ->
            val matchesQuery = query.isBlank() ||
                    machine.name.contains(query, ignoreCase = true) ||
                    machine.sizeSpec.contains(query, ignoreCase = true) ||
                    machine.reelNumber.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                StatusFilter.ALL -> true
                StatusFilter.RUNNING -> machine.status == MachineStatus.RUNNING
                StatusFilter.PAUSED -> machine.status == MachineStatus.PAUSED
                StatusFilter.IDLE -> machine.status == MachineStatus.IDLE
                StatusFilter.COMPLETED -> machine.status == MachineStatus.COMPLETED
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val productionLogs: StateFlow<List<ProductionLogEntity>> = AppDatabase.getDatabase(application)
        .machineDao().getAllLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MachineRepository(db.machineDao())

        viewModelScope.launch {
            repository.checkAndSeedInitialMachines()
            repository.allMachines.collect { list ->
                _rawMachines.value = list
            }
        }

        startTimerTicker()
    }

    private fun startTimerTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                val currentList = _rawMachines.value
                if (currentList.isEmpty()) continue

                val updatedList = mutableListOf<MachineEntity>()
                var hasChanges = false

                for (machine in currentList) {
                    if (machine.status == MachineStatus.RUNNING) {
                        val newElapsed = machine.elapsedSeconds + 1
                        val totalTargetSec = machine.totalTargetSeconds

                        if (totalTargetSec > 0 && newElapsed >= totalTargetSec) {
                            // Machine completed its run
                            val completedMachine = machine.copy(
                                elapsedSeconds = totalTargetSec,
                                status = MachineStatus.COMPLETED,
                                lastUpdatedTimestamp = System.currentTimeMillis()
                            )
                            updatedList.add(completedMachine)
                            hasChanges = true
                            playCompletionAlertSound()

                            // Create log entry
                            val actualHours = totalTargetSec.toDouble() / 3600.0
                            repository.insertLog(
                                ProductionLogEntity(
                                    machineId = machine.id,
                                    machineName = machine.name,
                                    sizeSpec = machine.sizeSpec,
                                    reelNumber = machine.reelNumber,
                                    targetHours = machine.targetHours,
                                    actualHoursWorked = actualHours,
                                    notes = machine.notes
                                )
                            )
                        } else {
                            val runningMachine = machine.copy(
                                elapsedSeconds = newElapsed,
                                lastUpdatedTimestamp = System.currentTimeMillis()
                            )
                            updatedList.add(runningMachine)
                            hasChanges = true
                        }
                    } else {
                        updatedList.add(machine)
                    }
                }

                if (hasChanges) {
                    _rawMachines.value = updatedList
                    // Save running state periodically to DB
                    val runningChanged = updatedList.filter { it.status == MachineStatus.RUNNING || it.status == MachineStatus.COMPLETED }
                    if (runningChanged.isNotEmpty()) {
                        repository.updateMachines(runningChanged)
                    }
                }
            }
        }
    }

    fun startMachine(id: Int) {
        viewModelScope.launch {
            val machine = _rawMachines.value.find { it.id == id } ?: return@launch
            if (machine.targetHours <= 0) return@launch // Need target hours set

            val updated = machine.copy(
                status = MachineStatus.RUNNING,
                lastStartedTimestamp = if (machine.elapsedSeconds == 0L) System.currentTimeMillis() else machine.lastStartedTimestamp,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            repository.updateMachine(updated)
        }
    }

    fun pauseMachine(id: Int) {
        viewModelScope.launch {
            val machine = _rawMachines.value.find { it.id == id } ?: return@launch
            val updated = machine.copy(
                status = MachineStatus.PAUSED,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            repository.updateMachine(updated)
        }
    }

    fun resetMachine(id: Int) {
        viewModelScope.launch {
            val machine = _rawMachines.value.find { it.id == id } ?: return@launch
            val updated = machine.copy(
                elapsedSeconds = 0L,
                status = MachineStatus.IDLE,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            repository.updateMachine(updated)
        }
    }

    fun updateMachineConfig(
        id: Int,
        sizeSpec: String,
        reelNumber: String,
        targetHours: Double,
        notes: String,
        autoStart: Boolean
    ) {
        viewModelScope.launch {
            val machine = _rawMachines.value.find { it.id == id } ?: return@launch
            val newStatus = if (autoStart && targetHours > 0) MachineStatus.RUNNING else if (machine.status == MachineStatus.COMPLETED) MachineStatus.IDLE else machine.status

            val updated = machine.copy(
                sizeSpec = sizeSpec,
                reelNumber = reelNumber,
                targetHours = targetHours,
                notes = notes,
                elapsedSeconds = if (autoStart) 0L else machine.elapsedSeconds,
                status = newStatus,
                lastStartedTimestamp = if (autoStart) System.currentTimeMillis() else machine.lastStartedTimestamp,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            repository.updateMachine(updated)
        }
    }

    fun startAllConfigured() {
        viewModelScope.launch {
            val toStart = _rawMachines.value.filter {
                (it.status == MachineStatus.IDLE || it.status == MachineStatus.PAUSED) &&
                        it.targetHours > 0 &&
                        it.sizeSpec.isNotBlank() &&
                        it.reelNumber.isNotBlank()
            }.map {
                it.copy(
                    status = MachineStatus.RUNNING,
                    lastStartedTimestamp = if (it.elapsedSeconds == 0L) System.currentTimeMillis() else it.lastStartedTimestamp,
                    lastUpdatedTimestamp = System.currentTimeMillis()
                )
            }
            if (toStart.isNotEmpty()) {
                repository.updateMachines(toStart)
            }
        }
    }

    fun pauseAllRunning() {
        viewModelScope.launch {
            val toPause = _rawMachines.value.filter { it.status == MachineStatus.RUNNING }
                .map {
                    it.copy(
                        status = MachineStatus.PAUSED,
                        lastUpdatedTimestamp = System.currentTimeMillis()
                    )
                }
            if (toPause.isNotEmpty()) {
                repository.updateMachines(toPause)
            }
        }
    }

    fun clearHistoryLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }

    fun applyBatchSetup(
        startId: Int,
        endId: Int,
        sizeSpec: String,
        reelPrefix: String,
        startReelNum: Int,
        targetHours: Double,
        autoStart: Boolean
    ) {
        viewModelScope.launch {
            val current = _rawMachines.value
            val toUpdate = mutableListOf<MachineEntity>()
            var currentReel = startReelNum

            for (m in current) {
                if (m.id in startId..endId) {
                    val reelStr = if (reelPrefix.isNotBlank()) "$reelPrefix$currentReel" else "$currentReel"
                    currentReel++
                    val newStatus = if (autoStart && targetHours > 0) MachineStatus.RUNNING else MachineStatus.IDLE

                    toUpdate.add(
                        m.copy(
                            sizeSpec = sizeSpec,
                            reelNumber = reelStr,
                            targetHours = targetHours,
                            elapsedSeconds = 0L,
                            status = newStatus,
                            lastStartedTimestamp = if (autoStart) System.currentTimeMillis() else 0L,
                            lastUpdatedTimestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
            if (toUpdate.isNotEmpty()) {
                repository.updateMachines(toUpdate)
            }
        }
    }

    fun generateShiftReportText(): String {
        val machines = _rawMachines.value
        val running = machines.count { it.status == MachineStatus.RUNNING }
        val paused = machines.count { it.status == MachineStatus.PAUSED }
        val completed = machines.count { it.status == MachineStatus.COMPLETED }
        val idle = machines.count { it.status == MachineStatus.IDLE }

        val dateFormat = java.text.SimpleDateFormat("yyyy/MM/dd - hh:mm a", java.util.Locale("ar"))
        val dateStr = dateFormat.format(java.util.Date())

        val sb = StringBuilder()
        sb.appendLine("📋 *تقرير وردية ماكينات Bunching (1 - 21)*")
        sb.appendLine("📅 الوقت: $dateStr")
        sb.appendLine("----------------------------------")
        sb.appendLine("📊 ملخص الحالة:")
        sb.appendLine("▶ ماكينات تعمل: $running")
        sb.appendLine("⏸ ماكينات متوقفة: $paused")
        sb.appendLine("✅ بكرات مكتملة: $completed")
        sb.appendLine("⚪ ماكينات خالية: $idle")
        sb.appendLine("----------------------------------")
        sb.appendLine("⚙ تفاصيل الماكينات النشطة:")

        machines.forEach { m ->
            if (m.status == MachineStatus.RUNNING || m.status == MachineStatus.PAUSED || m.status == MachineStatus.COMPLETED) {
                val statusSymbol = when (m.status) {
                    MachineStatus.RUNNING -> "▶"
                    MachineStatus.PAUSED -> "⏸"
                    MachineStatus.COMPLETED -> "✅"
                    else -> "⚪"
                }
                sb.appendLine("$statusSymbol ماكينة #${m.id} (${m.name}):")
                sb.appendLine("   - المقاس: ${m.sizeSpec.ifBlank { "غير محدد" }}")
                sb.appendLine("   - رقم البكرة: ${m.reelNumber.ifBlank { "غير محدد" }}")
                sb.appendLine("   - الساعات المستهدفة: ${m.targetHours}س | الإنجاز: ${(m.progressFraction * 100).toInt()}%")
                if (m.notes.isNotBlank()) {
                    sb.appendLine("   - ملاحظات: ${m.notes}")
                }
            }
        }
        return sb.toString()
    }

    private fun playCompletionAlertSound() {
        try {
            val toneG = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 500)

            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
