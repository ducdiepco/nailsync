package com.example.nailsync.fake

import com.example.nailsync.domain.model.Technician
import com.example.nailsync.domain.repository.TechnicianRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTechnicianRepository : TechnicianRepository {

    private val _technicians = MutableStateFlow<List<Technician>>(emptyList())

    fun setTechnicians(technicians: List<Technician>) { _technicians.value = technicians }

    override fun getTechnicians(): Flow<List<Technician>> = _technicians

    override suspend fun updateAvailability(technicianId: Int, isAvailable: Boolean) {
        _technicians.value = _technicians.value.map {
            if (it.id == technicianId) it.copy(isAvailable = isAvailable) else it
        }
    }
}
