package com.example.nailsync.data.repository

import com.example.nailsync.data.local.dao.CustomerDao
import com.example.nailsync.data.local.entity.CustomerEntity
import com.example.nailsync.domain.model.Customer
import com.example.nailsync.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao
) : CustomerRepository {

    override fun getCustomers(): Flow<List<Customer>> =
        customerDao.getAllCustomers().map { list -> list.map { it.toDomain() } }

    override fun getCustomer(id: Int): Flow<Customer?> =
        customerDao.getCustomer(id).map { it?.toDomain() }

    override suspend fun createCustomer(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        dateOfBirth: String
    ): Int = customerDao.insertCustomer(
        CustomerEntity(
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            dateOfBirth = dateOfBirth
        )
    ).toInt()

    override suspend fun updateCustomer(customer: Customer) =
        customerDao.updateCustomer(customer.toEntity())

    private fun CustomerEntity.toDomain() = Customer(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        email = email,
        dateOfBirth = dateOfBirth,
        createdAt = createdAt
    )

    private fun Customer.toEntity() = CustomerEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        email = email,
        dateOfBirth = dateOfBirth,
        createdAt = createdAt
    )
}
