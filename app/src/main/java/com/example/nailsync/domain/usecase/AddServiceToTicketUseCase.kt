package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.repository.TicketRepository
import javax.inject.Inject

class AddServiceToTicketUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(ticketId: Int, service: Service): Int =
        ticketRepository.addServiceToTicket(ticketId, service)
}
