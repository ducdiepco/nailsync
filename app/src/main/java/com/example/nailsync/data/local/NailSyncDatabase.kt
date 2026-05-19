package com.example.nailsync.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nailsync.data.local.dao.CustomerDao
import com.example.nailsync.data.local.dao.ServiceDao
import com.example.nailsync.data.local.dao.TechnicianDao
import com.example.nailsync.data.local.dao.TicketDao
import com.example.nailsync.data.local.entity.CustomerEntity
import com.example.nailsync.data.local.entity.ServiceEntity
import com.example.nailsync.data.local.entity.TechnicianEntity
import com.example.nailsync.data.local.entity.TicketEntity
import com.example.nailsync.data.local.entity.TicketServiceEntity

@Database(
    entities = [
        TicketEntity::class,
        TicketServiceEntity::class,
        TechnicianEntity::class,
        ServiceEntity::class,
        CustomerEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class NailSyncDatabase : RoomDatabase() {
    abstract fun ticketDao(): TicketDao
    abstract fun technicianDao(): TechnicianDao
    abstract fun serviceDao(): ServiceDao
    abstract fun customerDao(): CustomerDao
}
