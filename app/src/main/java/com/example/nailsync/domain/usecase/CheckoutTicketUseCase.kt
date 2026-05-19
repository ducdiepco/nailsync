package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.model.TicketStatus
import com.example.nailsync.domain.repository.TicketRepository
import javax.inject.Inject

class CheckoutTicketUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(ticketId: Int, tip: Double) {
        ticketRepository.updateTip(ticketId, tip)
        ticketRepository.updateTicketStatus(ticketId, TicketStatus.PAID)
    }
}
