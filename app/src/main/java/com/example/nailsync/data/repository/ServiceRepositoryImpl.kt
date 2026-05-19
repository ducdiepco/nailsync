package com.example.nailsync.data.repository

import com.example.nailsync.data.local.dao.ServiceDao
import com.example.nailsync.data.local.mapper.toDomain
import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val serviceDao: ServiceDao
) : ServiceRepository {

    override fun getServices(): Flow<List<Service>> =
        serviceDao.getAllServices().map { list -> list.map { it.toDomain() } }
}
