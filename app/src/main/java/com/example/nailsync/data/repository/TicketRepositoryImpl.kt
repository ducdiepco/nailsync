package com.example.nailsync.data.repository

import androidx.room.withTransaction
import com.example.nailsync.data.local.NailSyncDatabase
import com.example.nailsync.data.local.dao.TicketDao
import com.example.nailsync.data.local.entity.TicketEntity
import com.example.nailsync.data.local.entity.TicketServiceEntity
import com.example.nailsync.data.local.mapper.toDomain
import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.model.Technician
import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.model.TicketStatus
import com.example.nailsync.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TicketRepositoryImpl @Inject constructor(
    private val ticketDao: TicketDao,
    private val database: NailSyncDatabase
) : TicketRepository {

    override fun getTickets(): Flow<List<Ticket>> =
        ticketDao.getAllTicketsWithServices().map { list -> list.map { it.toDomain() } }

    override fun getTicket(ticketId: Int): Flow<Ticket?> =
        ticketDao.getTicketWithServices(ticketId).map { it?.toDomain() }

    override suspend fun createTicket(customerName: String, customerId: Int?): Int {
        val entity = TicketEntity(customerName = customerName, status = TicketStatus.WAITING.name, customerId = customerId)
        return ticketDao.insertTicket(entity).toInt()
    }

    override suspend fun updateTicketStatus(ticketId: Int, status: TicketStatus) {
        ticketDao.updateTicketStatus(ticketId, status.name)
    }

    override suspend fun addServiceToTicket(ticketId: Int, service: Service): Int {
        val entity = TicketServiceEntity(
            ticketId = ticketId,
            catalogServiceId = service.id,
            serviceName = service.name,
            price = service.price,
            durationMinutes = service.durationMinutes
        )
        return ticketDao.insertTicketService(entity).toInt()
    }

    override suspend fun removeServiceFromTicket(serviceId: Int) {
        ticketDao.deleteTicketService(serviceId)
    }

    override suspend fun assignTechnician(serviceId: Int, technician: Technician?) {
        ticketDao.assignTechnician(serviceId, technician?.id, technician?.name)
    }

    override suspend fun updateTip(ticketId: Int, tip: Double) {
        ticketDao.updateTicketTip(ticketId, tip)
    }

    override suspend fun combineTickets(sourceTicketId: Int, targetTicketId: Int) {
        database.withTransaction {
            ticketDao.moveServices(sourceTicketId, targetTicketId)
            ticketDao.updateTicketStatus(sourceTicketId, TicketStatus.CANCELLED.name)
        }
    }
}
