package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MachineStatus {
    IDLE,       // خالية
    RUNNING,    // تعمل
    PAUSED,     // متوقفة مؤقتاً
    COMPLETED   // مكتملة
}

@Entity(tableName = "machines")
data class MachineEntity(
    @PrimaryKey val id: Int, // 1 to 21
    val name: String, // "Bunching 1" to "Bunching 21"
    val sizeSpec: String = "", // المقاس (e.g. 1.5 mm²)
    val reelNumber: String = "", // رقم البكرة (e.g. R-101)
    val targetHours: Double = 0.0, // عداد الساعات المستهدفة للعمل
    val elapsedSeconds: Long = 0L, // الثواني المنقضية
    val status: MachineStatus = MachineStatus.IDLE,
    val lastStartedTimestamp: Long = 0L, // وقت التشغيل
    val lastUpdatedTimestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    val totalTargetSeconds: Long
        get() = (targetHours * 3600).toLong()

    val remainingSeconds: Long
        get() {
            val targetSec = totalTargetSeconds
            return if (targetSec <= 0) 0L else maxOf(0L, targetSec - elapsedSeconds)
        }

    val progressFraction: Float
        get() {
            val targetSec = totalTargetSeconds
            if (targetSec <= 0) return 0f
            return (elapsedSeconds.toFloat() / targetSec.toFloat()).coerceIn(0f, 1f)
        }
}
