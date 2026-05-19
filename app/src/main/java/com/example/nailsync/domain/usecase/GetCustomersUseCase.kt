package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.repository.CustomerRepository
import javax.inject.Inject

class GetCustomersUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    operator fun invoke() = customerRepository.getCustomers()
}
