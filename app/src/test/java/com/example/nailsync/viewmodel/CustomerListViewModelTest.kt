package com.example.nailsync.viewmodel

import com.example.nailsync.MainCoroutineRule
import com.example.nailsync.domain.model.Customer
import com.example.nailsync.domain.usecase.GetCustomersUseCase
import com.example.nailsync.fake.FakeCustomerRepository
import com.example.nailsync.features.customer.CustomerListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CustomerListViewModelTest {

    @get:Rule val coroutineRule = MainCoroutineRule()

    private lateinit var fakeCustomerRepo: FakeCustomerRepository
    private lateinit var viewModel: CustomerListViewModel

    private val customers = listOf(
        Customer(id = 1, firstName = "Alice", lastName = "Smith", phone = "555-0001", email = "alice@example.com"),
        Customer(id = 2, firstName = "Bob", lastName = "Jones", phone = "555-0002", email = "bob@example.com")
    )

    @Before
    fun setUp() {
        fakeCustomerRepo = FakeCustomerRepository()
        viewModel = CustomerListViewModel(GetCustomersUseCase(fakeCustomerRepo))
    }

    @Test
    fun initialState_isLoading() {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun customersFromRepo_populateList() = runTest {
        fakeCustomerRepo.setCustomers(customers)
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.customers.size)
    }

    @Test
    fun emptySearch_returnsAllCustomers() = runTest {
        fakeCustomerRepo.setCustomers(customers)
        advanceUntilIdle()
        viewModel.search("")
        assertEquals(2, viewModel.uiState.value.filtered.size)
    }

    @Test
    fun searchByFirstName_returnsMatchingCustomers() = runTest {
        fakeCustomerRepo.setCustomers(customers)
        advanceUntilIdle()
        viewModel.search("alice")
        val filtered = viewModel.uiState.value.filtered
        assertEquals(1, filtered.size)
        assertEquals("Alice Smith", filtered[0].fullName)
    }

    @Test
    fun searchByLastName_returnsMatchingCustomers() = runTest {
        fakeCustomerRepo.setCustomers(customers)
        advanceUntilIdle()
        viewModel.search("jones")
        val filtered = viewModel.uiState.value.filtered
        assertEquals(1, filtered.size)
        assertEquals("Bob Jones", filtered[0].fullName)
    }

    @Test
    fun searchByPhone_returnsMatchingCustomers() = runTest {
        fakeCustomerRepo.setCustomers(customers)
        advanceUntilIdle()
        viewModel.search("555-0001")
        val filtered = viewModel.uiState.value.filtered
        assertEquals(1, filtered.size)
        assertEquals("Alice Smith", filtered[0].fullName)
    }

    @Test
    fun searchByEmail_returnsMatchingCustomers() = runTest {
        fakeCustomerRepo.setCustomers(customers)
        advanceUntilIdle()
        viewModel.search("bob@example.com")
        val filtered = viewModel.uiState.value.filtered
        assertEquals(1, filtered.size)
        assertEquals("Bob Jones", filtered[0].fullName)
    }

    @Test
    fun searchNoMatch_returnsEmptyList() = runTest {
        fakeCustomerRepo.setCustomers(customers)
        advanceUntilIdle()
        viewModel.search("zzz")
        assertTrue(viewModel.uiState.value.filtered.isEmpty())
    }

    @Test
    fun searchIsCaseInsensitive() = runTest {
        fakeCustomerRepo.setCustomers(customers)
        advanceUntilIdle()
        viewModel.search("ALICE")
        assertEquals(1, viewModel.uiState.value.filtered.size)
    }
}
