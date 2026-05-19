package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.model.Technician
import com.example.nailsync.domain.repository.TechnicianRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTechniciansUseCase @Inject constructor(
    private val technicianRepository: TechnicianRepository
) {
    operator fun invoke(): Flow<List<Technician>> = technicianRepository.getTechnicians()
}
