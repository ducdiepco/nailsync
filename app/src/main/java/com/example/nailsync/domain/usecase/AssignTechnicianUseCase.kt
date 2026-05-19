package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.model.Technician
import com.example.nailsync.domain.repository.TicketRepository
import javax.inject.Inject

class AssignTechnicianUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(serviceId: Int, technician: Technician?) =
        ticketRepository.assignTechnician(serviceId, technician)
}
