package com.example.nailsync.features.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.usecase.CheckoutTicketUseCase
import com.example.nailsync.domain.usecase.GetTicketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val ticket: Ticket? = null,
    val tipAmount: Double = 0.0,
    val isCheckedOut: Boolean = false
) {
    val subtotal: Double get() = ticket?.subtotal ?: 0.0
    val tax: Double get() = ticket?.tax ?: 0.0
    val total: Double get() = subtotal + tax + tipAmount
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTicketUseCase: GetTicketUseCase,
    private val checkoutTicketUseCase: CheckoutTicketUseCase
) : ViewModel() {

    private val ticketId: Int = savedStateHandle.get<Int>("ticketId") ?: 0

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTicketUseCase(ticketId).collect { ticket ->
                _uiState.update { it.copy(ticket = ticket) }
            }
        }
    }

    fun setTip(tip: Double) = _uiState.update { it.copy(tipAmount = tip) }

    fun confirmPayment(onComplete: () -> Unit) {
        viewModelScope.launch {
            checkoutTicketUseCase(ticketId, _uiState.value.tipAmount)
            _uiState.update { it.copy(isCheckedOut = true) }
            onComplete()
        }
    }
}
