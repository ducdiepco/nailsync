package com.example.nailsync.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nailsync.data.local.entity.TechnicianEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TechnicianDao {

    @Query("SELECT * FROM technicians ORDER BY name ASC")
    fun getAllTechnicians(): Flow<List<TechnicianEntity>>

    @Query("SELECT COUNT(*) FROM technicians")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(technicians: List<TechnicianEntity>)

    @Query("UPDATE technicians SET isAvailable = :isAvailable WHERE id = :technicianId")
    suspend fun updateAvailability(technicianId: Int, isAvailable: Boolean)
}
