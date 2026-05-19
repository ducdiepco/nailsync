package com.example.nailsync.fake

import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.model.Technician
import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.model.TicketService
import com.example.nailsync.domain.model.TicketStatus
import com.example.nailsync.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTicketRepository : TicketRepository {

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())

    fun setTickets(tickets: List<Ticket>) { _tickets.value = tickets }

    override fun getTickets(): Flow<List<Ticket>> = _tickets

    override fun getTicket(ticketId: Int): Flow<Ticket?> =
        _tickets.map { it.find { t -> t.id == ticketId } }

    override suspend fun createTicket(customerName: String, customerId: Int?): Int {
        val newId = (_tickets.value.maxOfOrNull { it.id } ?: 0) + 1
        _tickets.value = _tickets.value + Ticket(id = newId, customerName = customerName, customerId = customerId)
        return newId
    }

    override suspend fun updateTicketStatus(ticketId: Int, status: TicketStatus) {
        _tickets.value = _tickets.value.map {
            if (it.id == ticketId) it.copy(status = status) else it
        }
    }

    override suspend fun addServiceToTicket(ticketId: Int, service: Service): Int {
        val newId = 1000 + _tickets.value.flatMap { it.services }.size
        val ts = TicketService(id = newId, serviceName = service.name, price = service.price, durationMinutes = service.durationMinutes)
        _tickets.value = _tickets.value.map {
            if (it.id == ticketId) it.copy(services = it.services + ts) else it
        }
        return newId
    }

    override suspend fun removeServiceFromTicket(serviceId: Int) {
        _tickets.value = _tickets.value.map { t ->
            t.copy(services = t.services.filter { it.id != serviceId })
        }
    }

    override suspend fun assignTechnician(serviceId: Int, technician: Technician?) {
        _tickets.value = _tickets.value.map { t ->
            t.copy(services = t.services.map { s ->
                if (s.id == serviceId) s.copy(technicianId = technician?.id, technicianName = technician?.name)
                else s
            })
        }
    }

    override suspend fun updateTip(ticketId: Int, tip: Double) {
        _tickets.value = _tickets.value.map {
            if (it.id == ticketId) it.copy(tip = tip) else it
        }
    }

    override suspend fun combineTickets(sourceTicketId: Int, targetTicketId: Int) {
        val source = _tickets.value.find { it.id == sourceTicketId } ?: return
        _tickets.value = _tickets.value.map {
            when (it.id) {
                targetTicketId -> it.copy(services = it.services + source.services)
                sourceTicketId -> it.copy(status = TicketStatus.CANCELLED)
                else -> it
            }
        }
    }
}
