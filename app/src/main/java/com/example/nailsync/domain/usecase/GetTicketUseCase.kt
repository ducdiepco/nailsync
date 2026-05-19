package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTicketUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    operator fun invoke(ticketId: Int): Flow<Ticket?> = ticketRepository.getTicket(ticketId)
}
