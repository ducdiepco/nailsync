package com.example.nailsync.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.nailsync.data.local.entity.TicketEntity
import com.example.nailsync.data.local.entity.TicketServiceEntity
import com.example.nailsync.data.local.entity.TicketWithServices
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {

    @Transaction
    @Query("SELECT * FROM tickets ORDER BY createdAt DESC")
    fun getAllTicketsWithServices(): Flow<List<TicketWithServices>>

    @Transaction
    @Query("SELECT * FROM tickets WHERE id = :ticketId")
    fun getTicketWithServices(ticketId: Int): Flow<TicketWithServices?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity): Long

    @Query("UPDATE tickets SET status = :status WHERE id = :ticketId")
    suspend fun updateTicketStatus(ticketId: Int, status: String)

    @Query("UPDATE tickets SET tip = :tip WHERE id = :ticketId")
    suspend fun updateTicketTip(ticketId: Int, tip: Double)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicketService(service: TicketServiceEntity): Long

    @Query(
        "UPDATE ticket_services SET technicianId = :technicianId, technicianName = :technicianName WHERE id = :serviceId"
    )
    suspend fun assignTechnician(serviceId: Int, technicianId: Int?, technicianName: String?)

    @Query("DELETE FROM ticket_services WHERE id = :serviceId")
    suspend fun deleteTicketService(serviceId: Int)

    @Query("UPDATE ticket_services SET ticketId = :targetId WHERE ticketId = :sourceId")
    suspend fun moveServices(sourceId: Int, targetId: Int)
}
