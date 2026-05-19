package com.example.nailsync.features.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nailsync.domain.model.Customer
import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.model.TicketStatus
import com.example.nailsync.domain.model.Technician
import com.example.nailsync.domain.usecase.CreateTicketUseCase
import com.example.nailsync.domain.usecase.GetCustomersUseCase
import com.example.nailsync.domain.usecase.GetTechniciansUseCase
import com.example.nailsync.domain.usecase.GetTicketsUseCase
import com.example.nailsync.domain.usecase.UpdateTicketStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val ACTIVE_STATUSES = setOf(
    TicketStatus.WAITING, TicketStatus.ASSIGNED, TicketStatus.IN_PROGRESS, TicketStatus.READY_FOR_PAYMENT
)

data class QueueUiState(
    val activeTickets: List<Ticket> = emptyList(),
    val closedTickets: List<Ticket> = emptyList(),
    val technicians: List<Technician> = emptyList(),
    val isLoading: Boolean = false,
    val showCreateDialog: Boolean = false,
    val allCustomers: List<Customer> = emptyList(),
    val dialogSearchQuery: String = "",
    val dialogSelectedCustomer: Customer? = null
) {
    val dialogSearchResults: List<Customer>
        get() = if (dialogSearchQuery.isBlank()) emptyList()
        else allCustomers.filter {
            it.fullName.contains(dialogSearchQuery, ignoreCase = true) ||
            it.phone.contains(dialogSearchQuery)
        }.take(5)
}

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val getTicketsUseCase: GetTicketsUseCase,
    private val getTechniciansUseCase: GetTechniciansUseCase,
    private val createTicketUseCase: CreateTicketUseCase,
    private val updateTicketStatusUseCase: UpdateTicketStatusUseCase,
    private val getCustomersUseCase: GetCustomersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QueueUiState())
    val uiState: StateFlow<QueueUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            launch {
                getTicketsUseCase().collect { tickets ->
                    _uiState.update {
                        it.copy(
                            activeTickets = tickets.filter { t -> t.status in ACTIVE_STATUSES },
                            closedTickets = tickets.filter { t -> t.status !in ACTIVE_STATUSES },
                            isLoading = false
                        )
                    }
                }
            }
            launch {
                getTechniciansUseCase().collect { technicians ->
                    _uiState.update { it.copy(technicians = technicians) }
                }
            }
            launch {
                getCustomersUseCase().collect { customers ->
                    _uiState.update { it.copy(allCustomers = customers) }
                }
            }
        }
    }

    fun showCreateDialog() = _uiState.update {
        it.copy(showCreateDialog = true, dialogSearchQuery = "", dialogSelectedCustomer = null)
    }
    fun hideCreateDialog() = _uiState.update {
        it.copy(showCreateDialog = false, dialogSearchQuery = "", dialogSelectedCustomer = null)
    }

    fun updateDialogSearch(query: String) = _uiState.update {
        it.copy(dialogSearchQuery = query, dialogSelectedCustomer = null)
    }
    fun selectDialogCustomer(customer: Customer) = _uiState.update {
        it.copy(dialogSelectedCustomer = customer, dialogSearchQuery = "")
    }
    fun clearDialogCustomer() = _uiState.update {
        it.copy(dialogSelectedCustomer = null, dialogSearchQuery = "")
    }

    fun createTicket(customerName: String, customerId: Int?, onCreated: (Int) -> Unit) {
        if (customerName.isBlank()) return
        viewModelScope.launch {
            val id = createTicketUseCase(customerName, customerId)
            hideCreateDialog()
            onCreated(id)
        }
    }

    fun advanceStatus(ticket: Ticket) {
        val next = ticket.status.nextStatus ?: return
        viewModelScope.launch { updateTicketStatusUseCase(ticket.id, next) }
    }
}
