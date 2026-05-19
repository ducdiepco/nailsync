package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServicesUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    operator fun invoke(): Flow<List<Service>> = serviceRepository.getServices()
}
