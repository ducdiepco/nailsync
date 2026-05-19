package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.model.Customer
import com.example.nailsync.domain.repository.CustomerRepository
import javax.inject.Inject

class UpdateCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customer: Customer) = customerRepository.updateCustomer(customer)
}
