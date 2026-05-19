package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.repository.TicketRepository
import javax.inject.Inject

class CreateTicketUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(customerName: String, customerId: Int? = null): Int =
        ticketRepository.createTicket(customerName, customerId)
}
