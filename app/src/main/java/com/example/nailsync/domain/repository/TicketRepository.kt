package com.example.nailsync.domain.repository

import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.model.Technician
import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.model.TicketStatus
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    fun getTickets(): Flow<List<Ticket>>
    fun getTicket(ticketId: Int): Flow<Ticket?>
    suspend fun createTicket(customerName: String, customerId: Int? = null): Int
    suspend fun updateTicketStatus(ticketId: Int, status: TicketStatus)
    suspend fun addServiceToTicket(ticketId: Int, service: Service): Int
    suspend fun removeServiceFromTicket(serviceId: Int)
    suspend fun assignTechnician(serviceId: Int, technician: Technician?)
    suspend fun updateTip(ticketId: Int, tip: Double)
    suspend fun combineTickets(sourceTicketId: Int, targetTicketId: Int)
}
