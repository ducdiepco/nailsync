package com.example.nailsync.features.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nailsync.domain.model.Customer
import com.example.nailsync.domain.usecase.CreateCustomerUseCase
import com.example.nailsync.domain.usecase.GetCustomerUseCase
import com.example.nailsync.domain.usecase.UpdateCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerDetailUiState(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val isNew: Boolean = true,
    val isSaving: Boolean = false,
    val isLoading: Boolean = false
) {
    val canSave: Boolean get() = firstName.isNotBlank() && lastName.isNotBlank()
}

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCustomerUseCase: GetCustomerUseCase,
    private val createCustomerUseCase: CreateCustomerUseCase,
    private val updateCustomerUseCase: UpdateCustomerUseCase
) : ViewModel() {

    val customerId: Int = savedStateHandle.get<Int>("customerId") ?: -1

    private val _uiState = MutableStateFlow(CustomerDetailUiState())
    val uiState: StateFlow<CustomerDetailUiState> = _uiState.asStateFlow()

    init {
        if (customerId != -1) {
            _uiState.update { it.copy(isLoading = true, isNew = false) }
            viewModelScope.launch {
                getCustomerUseCase(customerId).collect { customer ->
                    customer?.let { c ->
                        _uiState.update {
                            it.copy(
                                firstName = c.firstName,
                                lastName = c.lastName,
                                phone = c.phone,
                                email = c.email,
                                dateOfBirth = c.dateOfBirth,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateFirstName(v: String) = _uiState.update { it.copy(firstName = v) }
    fun updateLastName(v: String) = _uiState.update { it.copy(lastName = v) }
    fun updatePhone(v: String) = _uiState.update { it.copy(phone = v) }
    fun updateEmail(v: String) = _uiState.update { it.copy(email = v) }
    fun updateDateOfBirth(v: String) = _uiState.update { it.copy(dateOfBirth = v) }

    fun save(onDone: () -> Unit) {
        val s = _uiState.value
        if (!s.canSave) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            if (s.isNew) {
                createCustomerUseCase(s.firstName, s.lastName, s.phone, s.email, s.dateOfBirth)
            } else {
                updateCustomerUseCase(
                    Customer(
                        id = customerId,
                        firstName = s.firstName,
                        lastName = s.lastName,
                        phone = s.phone,
                        email = s.email,
                        dateOfBirth = s.dateOfBirth
                    )
                )
            }
            _uiState.update { it.copy(isSaving = false) }
            onDone()
        }
    }
}
