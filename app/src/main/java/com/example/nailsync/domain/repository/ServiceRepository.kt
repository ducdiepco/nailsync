package com.example.nailsync.domain.repository

import com.example.nailsync.domain.model.Service
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getServices(): Flow<List<Service>>
}
