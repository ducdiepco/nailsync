package com.example.nailsync.features.ticket

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.model.TicketStatus
import com.example.nailsync.domain.usecase.AddServiceToTicketUseCase
import com.example.nailsync.domain.usecase.CombineTicketsUseCase
import com.example.nailsync.domain.usecase.GetServicesUseCase
import com.example.nailsync.domain.usecase.GetTicketUseCase
import com.example.nailsync.domain.usecase.GetTicketsUseCase
import com.example.nailsync.domain.usecase.RemoveServiceFromTicketUseCase
import com.example.nailsync.domain.usecase.UpdateTicketStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val COMBINABLE_STATUSES = setOf(
    TicketStatus.WAITING, TicketStatus.ASSIGNED, TicketStatus.IN_PROGRESS, TicketStatus.READY_FOR_PAYMENT
)

data class TicketDetailUiState(
    val ticket: Ticket? = null,
    val allServices: List<Service> = emptyList(),
    val selectedCategory: String = "ALL",
    val isLoading: Boolean = false,
    // combine
    val showCombineDialog: Boolean = false,
    val combineTargets: List<Ticket> = emptyList(),
    val selectedCombineTarget: Ticket? = null,
    val isCombining: Boolean = false
) {
    val categories: List<String>
        get() = listOf("ALL") + allServices.map { it.category }.distinct().sorted()

    val filteredServices: List<Service>
        get() = if (selectedCategory == "ALL") allServices
                else allServices.filter { it.category == selectedCategory }
}

@HiltViewModel
class TicketDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTicketUseCase: GetTicketUseCase,
    private val getTicketsUseCase: GetTicketsUseCase,
    private val updateTicketStatusUseCase: UpdateTicketStatusUseCase,
    private val removeServiceFromTicketUseCase: RemoveServiceFromTicketUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val addServiceToTicketUseCase: AddServiceToTicketUseCase,
    private val combineTicketsUseCase: CombineTicketsUseCase
) : ViewModel() {

    val ticketId: Int = savedStateHandle.get<Int>("ticketId") ?: 0

    private val _uiState = MutableStateFlow(TicketDetailUiState())
    val uiState: StateFlow<TicketDetailUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            launch {
                getTicketUseCase(ticketId).collect { ticket ->
                    _uiState.update { it.copy(ticket = ticket, isLoading = false) }
                }
            }
            launch {
                getServicesUseCase().collect { services ->
                    _uiState.update { it.copy(allServices = services) }
                }
            }
            launch {
                getTicketsUseCase().collect { tickets ->
                    _uiState.update { state ->
                        state.copy(
                            combineTargets = tickets.filter {
                                it.id != ticketId && it.status in COMBINABLE_STATUSES
                            }
                        )
                    }
                }
            }
        }
    }

    fun selectCategory(category: String) = _uiState.update { it.copy(selectedCategory = category) }

    fun addService(service: Service) {
        viewModelScope.launch { addServiceToTicketUseCase(ticketId, service) }
    }

    fun removeService(serviceId: Int) {
        viewModelScope.launch { removeServiceFromTicketUseCase(serviceId) }
    }

    fun updateStatus(status: TicketStatus) {
        viewModelScope.launch { updateTicketStatusUseCase(ticketId, status) }
    }

    // ── Combine ──────────────────────────────────────────────────────────────

    fun openCombineDialog() = _uiState.update { it.copy(showCombineDialog = true, selectedCombineTarget = null) }
    fun closeCombineDialog() = _uiState.update { it.copy(showCombineDialog = false, selectedCombineTarget = null) }
    fun selectCombineTarget(ticket: Ticket) = _uiState.update { it.copy(selectedCombineTarget = ticket) }

    fun confirmCombine(onDone: () -> Unit) {
        val source = _uiState.value.selectedCombineTarget ?: return
        _uiState.update { it.copy(isCombining = true) }
        viewModelScope.launch {
            // source's services move into this (target) ticket; source gets cancelled
            combineTicketsUseCase(sourceTicketId = source.id, targetTicketId = ticketId)
            _uiState.update { it.copy(isCombining = false, showCombineDialog = false, selectedCombineTarget = null) }
            onDone()
        }
    }
}
