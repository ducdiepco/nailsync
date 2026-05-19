package com.example.nailsync.features.ticket

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.usecase.AddServiceToTicketUseCase
import com.example.nailsync.domain.usecase.GetServicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddServiceUiState(
    val services: List<Service> = emptyList(),
    val addedCount: Int = 0
)

@HiltViewModel
class AddServiceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getServicesUseCase: GetServicesUseCase,
    private val addServiceToTicketUseCase: AddServiceToTicketUseCase
) : ViewModel() {

    private val ticketId: Int = savedStateHandle.get<Int>("ticketId") ?: 0

    private val _uiState = MutableStateFlow(AddServiceUiState())
    val uiState: StateFlow<AddServiceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getServicesUseCase().collect { services ->
                _uiState.update { it.copy(services = services) }
            }
        }
    }

    fun addService(service: Service) {
        viewModelScope.launch {
            addServiceToTicketUseCase(ticketId, service)
            _uiState.update { it.copy(addedCount = it.addedCount + 1) }
        }
    }
}
