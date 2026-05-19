package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.repository.TicketRepository
import javax.inject.Inject

class RemoveServiceFromTicketUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(serviceId: Int) =
        ticketRepository.removeServiceFromTicket(serviceId)
}
