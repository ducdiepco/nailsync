package com.example.nailsync.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.nailsync.MainCoroutineRule
import com.example.nailsync.domain.model.Customer
import com.example.nailsync.domain.usecase.CreateCustomerUseCase
import com.example.nailsync.domain.usecase.GetCustomerUseCase
import com.example.nailsync.domain.usecase.UpdateCustomerUseCase
import com.example.nailsync.fake.FakeCustomerRepository
import com.example.nailsync.features.customer.CustomerDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CustomerDetailViewModelTest {

    @get:Rule val coroutineRule = MainCoroutineRule()

    private lateinit var fakeCustomerRepo: FakeCustomerRepository

    private fun createViewModel(customerId: Int = -1): CustomerDetailViewModel {
        return CustomerDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("customerId" to customerId)),
            getCustomerUseCase = GetCustomerUseCase(fakeCustomerRepo),
            createCustomerUseCase = CreateCustomerUseCase(fakeCustomerRepo),
            updateCustomerUseCase = UpdateCustomerUseCase(fakeCustomerRepo)
        )
    }

    @Before
    fun setUp() {
        fakeCustomerRepo = FakeCustomerRepository()
    }

    @Test
    fun newCustomer_isNewTrue() {
        val vm = createViewModel(-1)
        assertTrue(vm.uiState.value.isNew)
    }

    @Test
    fun newCustomer_canSaveFalseWhenEmpty() {
        val vm = createViewModel(-1)
        assertFalse(vm.uiState.value.canSave)
    }

    @Test
    fun newCustomer_canSaveTrueAfterBothNames() {
        val vm = createViewModel(-1)
        vm.updateFirstName("Alice")
        vm.updateLastName("Smith")
        assertTrue(vm.uiState.value.canSave)
    }

    @Test
    fun newCustomer_canSaveFalseWhenOnlyFirstName() {
        val vm = createViewModel(-1)
        vm.updateFirstName("Alice")
        assertFalse(vm.uiState.value.canSave)
    }

    @Test
    fun newCustomer_canSaveFalseWhenOnlyLastName() {
        val vm = createViewModel(-1)
        vm.updateLastName("Smith")
        assertFalse(vm.uiState.value.canSave)
    }

    @Test
    fun existingCustomer_isNewFalse() = runTest {
        fakeCustomerRepo.setCustomers(listOf(
            Customer(id = 5, firstName = "Bob", lastName = "Jones", phone = "555-9999")
        ))
        val vm = createViewModel(5)
        advanceUntilIdle()
        assertFalse(vm.uiState.value.isNew)
    }

    @Test
    fun existingCustomer_loadsFieldsFromRepo() = runTest {
        fakeCustomerRepo.setCustomers(listOf(
            Customer(id = 5, firstName = "Bob", lastName = "Jones", phone = "555-9999", email = "bob@x.com")
        ))
        val vm = createViewModel(5)
        advanceUntilIdle()
        assertEquals("Bob", vm.uiState.value.firstName)
        assertEquals("Jones", vm.uiState.value.lastName)
        assertEquals("555-9999", vm.uiState.value.phone)
        assertEquals("bob@x.com", vm.uiState.value.email)
    }

    @Test
    fun saveNewCustomer_callsCreateAndInvokesCallback() = runTest {
        val vm = createViewModel(-1)
        vm.updateFirstName("Carol")
        vm.updateLastName("White")
        vm.updatePhone("555-4321")
        var doneCalled = false
        vm.save { doneCalled = true }
        advanceUntilIdle()
        assertTrue(doneCalled)
    }

    @Test
    fun saveExistingCustomer_callsUpdateAndInvokesCallback() = runTest {
        fakeCustomerRepo.setCustomers(listOf(
            Customer(id = 7, firstName = "Dan", lastName = "Brown")
        ))
        val vm = createViewModel(7)
        advanceUntilIdle()
        vm.updatePhone("555-1111")
        var doneCalled = false
        vm.save { doneCalled = true }
        advanceUntilIdle()
        assertTrue(doneCalled)
    }

    @Test
    fun updateFields_reflectsInState() {
        val vm = createViewModel(-1)
        vm.updateFirstName("Eve")
        vm.updateLastName("Green")
        vm.updatePhone("555-7777")
        vm.updateEmail("eve@test.com")
        vm.updateDateOfBirth("1990-01-15")
        val s = vm.uiState.value
        assertEquals("Eve", s.firstName)
        assertEquals("Green", s.lastName)
        assertEquals("555-7777", s.phone)
        assertEquals("eve@test.com", s.email)
        assertEquals("1990-01-15", s.dateOfBirth)
    }
}
