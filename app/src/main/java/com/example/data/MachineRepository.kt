package com.example.data

import kotlinx.coroutines.flow.Flow

class MachineRepository(private val machineDao: MachineDao) {

    val allMachines: Flow<List<MachineEntity>> = machineDao.getAllMachines()
    val allLogs: Flow<List<ProductionLogEntity>> = machineDao.getAllLogs()

    suspend fun checkAndSeedInitialMachines() {
        if (machineDao.getMachineCount() == 0) {
            val defaultMachines = (1..21).map { index ->
                val sampleSizes = listOf("0.5 مم²", "0.75 مم²", "1.0 مم²", "1.5 مم²", "2.5 مم²", "4.0 مم²", "6.0 مم²")
                val defaultSize = sampleSizes[(index - 1) % sampleSizes.size]
                val defaultReel = "B-${100 + index}"
                val defaultHours = when {
                    index % 3 == 0 -> 8.0
                    index % 3 == 1 -> 12.0
                    else -> 6.0
                }
                
                MachineEntity(
                    id = index,
                    name = "Bunching $index",
                    sizeSpec = defaultSize,
                    reelNumber = defaultReel,
                    targetHours = defaultHours,
                    elapsedSeconds = if (index <= 3) 3600L * (index % 4) else 0L,
                    status = if (index <= 2) MachineStatus.RUNNING else MachineStatus.IDLE,
                    lastStartedTimestamp = if (index <= 2) System.currentTimeMillis() - (3600L * index * 1000) else 0L,
                    lastUpdatedTimestamp = System.currentTimeMillis(),
                    notes = "وردية صباحية"
                )
            }
            machineDao.insertAll(defaultMachines)
        } else {
            // Restore running state catch-up for time spent while app was closed
            val now = System.currentTimeMillis()
            val allMachines = machineDao.getAllMachinesOnce()
            val updatedList = mutableListOf<MachineEntity>()
            for (m in allMachines) {
                if (m.status == MachineStatus.RUNNING && m.lastUpdatedTimestamp > 0) {
                    val offlineSeconds = maxOf(0L, (now - m.lastUpdatedTimestamp) / 1000L)
                    val newElapsed = m.elapsedSeconds + offlineSeconds
                    val targetSec = m.totalTargetSeconds

                    if (targetSec > 0 && newElapsed >= targetSec) {
                        val completed = m.copy(
                            elapsedSeconds = targetSec,
                            status = MachineStatus.COMPLETED,
                            lastUpdatedTimestamp = now
                        )
                        updatedList.add(completed)
                        machineDao.insertLog(
                            ProductionLogEntity(
                                machineId = m.id,
                                machineName = m.name,
                                sizeSpec = m.sizeSpec,
                                reelNumber = m.reelNumber,
                                targetHours = m.targetHours,
                                actualHoursWorked = targetSec.toDouble() / 3600.0,
                                notes = m.notes
                            )
                        )
                    } else {
                        updatedList.add(
                            m.copy(
                                elapsedSeconds = newElapsed,
                                lastUpdatedTimestamp = now
                            )
                        )
                    }
                }
            }
            if (updatedList.isNotEmpty()) {
                machineDao.updateMachines(updatedList)
            }
        }
    }

    suspend fun updateMachine(machine: MachineEntity) {
        machineDao.updateMachine(machine)
    }

    suspend fun updateMachines(machines: List<MachineEntity>) {
        machineDao.updateMachines(machines)
    }

    suspend fun insertLog(log: ProductionLogEntity) {
        machineDao.insertLog(log)
    }

    suspend fun clearLogs() {
        machineDao.clearAllLogs()
    }
}
