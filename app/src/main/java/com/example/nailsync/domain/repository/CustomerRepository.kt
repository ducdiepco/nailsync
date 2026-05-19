package com.example.nailsync.domain.repository

import com.example.nailsync.domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getCustomers(): Flow<List<Customer>>
    fun getCustomer(id: Int): Flow<Customer?>
    suspend fun createCustomer(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        dateOfBirth: String
    ): Int
    suspend fun updateCustomer(customer: Customer)
}
