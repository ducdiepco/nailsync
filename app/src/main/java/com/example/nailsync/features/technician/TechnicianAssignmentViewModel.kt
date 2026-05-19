package com.example.nailsync.features.technician

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nailsync.domain.model.Technician
import com.example.nailsync.domain.usecase.AssignTechnicianUseCase
import com.example.nailsync.domain.usecase.GetTechniciansUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TechnicianAssignmentUiState(
    val technicians: List<Technician> = emptyList(),
    val assignedId: Int? = null
)

@HiltViewModel
class TechnicianAssignmentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTechniciansUseCase: GetTechniciansUseCase,
    private val assignTechnicianUseCase: AssignTechnicianUseCase
) : ViewModel() {

    private val serviceId: Int = savedStateHandle.get<Int>("serviceId") ?: 0

    private val _uiState = MutableStateFlow(TechnicianAssignmentUiState())
    val uiState: StateFlow<TechnicianAssignmentUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTechniciansUseCase().collect { technicians ->
                _uiState.update { it.copy(technicians = technicians) }
            }
        }
    }

    fun assign(technician: Technician, onDone: () -> Unit) {
        viewModelScope.launch {
            assignTechnicianUseCase(serviceId, technician)
            _uiState.update { it.copy(assignedId = technician.id) }
            onDone()
        }
    }

    fun unassign(onDone: () -> Unit) {
        viewModelScope.launch {
            assignTechnicianUseCase(serviceId, null)
            _uiState.update { it.copy(assignedId = null) }
            onDone()
        }
    }
}
