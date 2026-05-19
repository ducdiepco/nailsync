package com.example.nailsync.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nailsync.data.local.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    @Query("SELECT * FROM services ORDER BY name ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT COUNT(*) FROM services")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(services: List<ServiceEntity>)
}
