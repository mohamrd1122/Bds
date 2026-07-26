package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "production_logs")
data class ProductionLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0L,
    val machineId: Int,
    val machineName: String,
    val sizeSpec: String,
    val reelNumber: String,
    val targetHours: Double,
    val actualHoursWorked: Double,
    val completedTimestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
