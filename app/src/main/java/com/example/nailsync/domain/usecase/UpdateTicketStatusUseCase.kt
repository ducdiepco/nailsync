package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.model.TicketStatus
import com.example.nailsync.domain.repository.TicketRepository
import javax.inject.Inject

class UpdateTicketStatusUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(ticketId: Int, status: TicketStatus) =
        ticketRepository.updateTicketStatus(ticketId, status)
}
