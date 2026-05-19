package com.example.nailsync.domain.usecase

import com.example.nailsync.domain.repository.CustomerRepository
import javax.inject.Inject

class CreateCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        dateOfBirth: String
    ): Int = customerRepository.createCustomer(firstName, lastName, phone, email, dateOfBirth)
}
