package com.example.nailsync.fake

import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeServiceRepository : ServiceRepository {

    private val _services = MutableStateFlow<List<Service>>(emptyList())

    fun setServices(services: List<Service>) { _services.value = services }

    override fun getServices(): Flow<List<Service>> = _services
}
