package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.repository.CustomerRepository
import javax.inject.Inject

class GetCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    operator fun invoke(id: Int) = customerRepository.getCustomer(id)
}
