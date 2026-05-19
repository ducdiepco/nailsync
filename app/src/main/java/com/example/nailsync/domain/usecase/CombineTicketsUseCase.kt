package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.repository.TicketRepository
import javax.inject.Inject

class CombineTicketsUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(sourceTicketId: Int, targetTicketId: Int) =
        ticketRepository.combineTickets(sourceTicketId, targetTicketId)
}
