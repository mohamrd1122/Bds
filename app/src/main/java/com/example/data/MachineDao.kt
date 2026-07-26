package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MachineDao {
    @Query("SELECT * FROM machines ORDER BY id ASC")
    fun getAllMachines(): Flow<List<MachineEntity>>

    @Query("SELECT * FROM machines ORDER BY id ASC")
    suspend fun getAllMachinesOnce(): List<MachineEntity>

    @Query("SELECT * FROM machines WHERE id = :id")
    suspend fun getMachineById(id: Int): MachineEntity?

    @Query("SELECT COUNT(*) FROM machines")
    suspend fun getMachineCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(machines: List<MachineEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMachine(machine: MachineEntity)

    @Update
    suspend fun updateMachine(machine: MachineEntity)

    @Update
    suspend fun updateMachines(machines: List<MachineEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ProductionLogEntity)

    @Query("SELECT * FROM production_logs ORDER BY completedTimestamp DESC")
    fun getAllLogs(): Flow<List<ProductionLogEntity>>

    @Query("DELETE FROM production_logs")
    suspend fun clearAllLogs()
}
