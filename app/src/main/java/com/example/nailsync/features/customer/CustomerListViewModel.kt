package com.example.nailsync.features.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nailsync.domain.model.Customer
import com.example.nailsync.domain.usecase.GetCustomersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerListUiState(
    val customers: List<Customer> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
) {
    val filtered: List<Customer>
        get() = if (searchQuery.isBlank()) customers
        else customers.filter {
            it.fullName.contains(searchQuery, ignoreCase = true) ||
            it.phone.contains(searchQuery) ||
            it.email.contains(searchQuery, ignoreCase = true)
        }
}

@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val getCustomersUseCase: GetCustomersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerListUiState(isLoading = true))
    val uiState: StateFlow<CustomerListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCustomersUseCase().collect { customers ->
                _uiState.update { it.copy(customers = customers, isLoading = false) }
            }
        }
    }

    fun search(query: String) = _uiState.update { it.copy(searchQuery = query) }
}
