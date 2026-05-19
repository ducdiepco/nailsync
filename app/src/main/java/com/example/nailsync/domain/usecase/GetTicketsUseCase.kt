package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTicketsUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    operator fun invoke(): Flow<List<Ticket>> = ticketRepository.getTickets()
}
