package com.example.nailsync.fake

import com.example.nailsync.domain.model.Customer
import com.example.nailsync.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCustomerRepository : CustomerRepository {

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())

    fun setCustomers(customers: List<Customer>) { _customers.value = customers }

    override fun getCustomers(): Flow<List<Customer>> = _customers

    override fun getCustomer(id: Int): Flow<Customer?> =
        _customers.map { it.find { c -> c.id == id } }

    override suspend fun createCustomer(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        dateOfBirth: String
    ): Int {
        val newId = (_customers.value.maxOfOrNull { it.id } ?: 0) + 1
        _customers.value = _customers.value + Customer(
            id = newId, firstName = firstName, lastName = lastName,
            phone = phone, email = email, dateOfBirth = dateOfBirth
        )
        return newId
    }

    override suspend fun updateCustomer(customer: Customer) {
        _customers.value = _customers.value.map {
            if (it.id == customer.id) customer else it
        }
    }
}
