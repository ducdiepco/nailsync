package com.example.nailsync.data.repository

import com.example.nailsync.data.local.dao.TechnicianDao
import com.example.nailsync.data.local.mapper.toDomain
import com.example.nailsync.domain.model.Technician
import com.example.nailsync.domain.repository.TechnicianRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TechnicianRepositoryImpl @Inject constructor(
    private val technicianDao: TechnicianDao
) : TechnicianRepository {

    override fun getTechnicians(): Flow<List<Technician>> =
        technicianDao.getAllTechnicians().map { list -> list.map { it.toDomain() } }

    override suspend fun updateAvailability(technicianId: Int, isAvailable: Boolean) {
        technicianDao.updateAvailability(technicianId, isAvailable)
    }
}
