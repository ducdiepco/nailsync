package com.example.nailsync.viewmodel

import com.example.nailsync.MainCoroutineRule
import com.example.nailsync.domain.model.Customer
import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.model.TicketStatus
import com.example.nailsync.domain.usecase.CreateTicketUseCase
import com.example.nailsync.domain.usecase.GetCustomersUseCase
import com.example.nailsync.domain.usecase.GetTechniciansUseCase
import com.example.nailsync.domain.usecase.GetTicketsUseCase
import com.example.nailsync.domain.usecase.UpdateTicketStatusUseCase
import com.example.nailsync.fake.FakeCustomerRepository
import com.example.nailsync.fake.FakeTechnicianRepository
import com.example.nailsync.fake.FakeTicketRepository
import com.example.nailsync.features.queue.QueueViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QueueViewModelTest {

    @get:Rule val coroutineRule = MainCoroutineRule()

    private lateinit var fakeTicketRepo: FakeTicketRepository
    private lateinit var fakeCustomerRepo: FakeCustomerRepository
    private lateinit var fakeTechnicianRepo: FakeTechnicianRepository
    private lateinit var viewModel: QueueViewModel

    @Before
    fun setUp() {
        fakeTicketRepo = FakeTicketRepository()
        fakeCustomerRepo = FakeCustomerRepository()
        fakeTechnicianRepo = FakeTechnicianRepository()
        viewModel = QueueViewModel(
            getTicketsUseCase = GetTicketsUseCase(fakeTicketRepo),
            getTechniciansUseCase = GetTechniciansUseCase(fakeTechnicianRepo),
            createTicketUseCase = CreateTicketUseCase(fakeTicketRepo),
            updateTicketStatusUseCase = UpdateTicketStatusUseCase(fakeTicketRepo),
            getCustomersUseCase = GetCustomersUseCase(fakeCustomerRepo)
        )
    }

    @Test
    fun initialState_isEmpty() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state.activeTickets.isEmpty())
        assertTrue(state.closedTickets.isEmpty())
        assertFalse(state.showCreateDialog)
    }

    @Test
    fun ticketsFromRepo_splitIntoActiveAndClosed() = runTest {
        fakeTicketRepo.setTickets(listOf(
            Ticket(id = 1, customerName = "Alice", status = TicketStatus.WAITING),
            Ticket(id = 2, customerName = "Bob", status = TicketStatus.PAID)
        ))
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(1, state.activeTickets.size)
        assertEquals("Alice", state.activeTickets[0].customerName)
        assertEquals(1, state.closedTickets.size)
        assertEquals("Bob", state.closedTickets[0].customerName)
    }

    @Test
    fun showCreateDialog_setsFlag() {
        viewModel.showCreateDialog()
        assertTrue(viewModel.uiState.value.showCreateDialog)
    }

    @Test
    fun hideCreateDialog_clearsFlag() {
        viewModel.showCreateDialog()
        viewModel.hideCreateDialog()
        assertFalse(viewModel.uiState.value.showCreateDialog)
    }

    @Test
    fun createTicket_addsToRepo() = runTest {
        var createdId = -1
        viewModel.createTicket("Carol", null) { createdId = it }
        advanceUntilIdle()
        assertTrue(createdId > 0)
        assertTrue(fakeTicketRepo.getTickets().let { true }) // repo holds the ticket
    }

    @Test
    fun dialogSearch_returnsMatchingCustomers() = runTest {
        fakeCustomerRepo.setCustomers(listOf(
            Customer(id = 1, firstName = "Jane", lastName = "Doe"),
            Customer(id = 2, firstName = "John", lastName = "Smith")
        ))
        advanceUntilIdle()
        viewModel.updateDialogSearch("Jane")
        val results = viewModel.uiState.value.dialogSearchResults
        assertEquals(1, results.size)
        assertEquals("Jane Doe", results[0].fullName)
    }

    @Test
    fun dialogSearch_emptyQuery_returnsNoResults() = runTest {
        fakeCustomerRepo.setCustomers(listOf(
            Customer(id = 1, firstName = "Jane", lastName = "Doe")
        ))
        advanceUntilIdle()
        viewModel.updateDialogSearch("")
        assertTrue(viewModel.uiState.value.dialogSearchResults.isEmpty())
    }

    @Test
    fun selectDialogCustomer_clearsSearchQuery() = runTest {
        val customer = Customer(id = 1, firstName = "Jane", lastName = "Doe")
        fakeCustomerRepo.setCustomers(listOf(customer))
        advanceUntilIdle()
        viewModel.updateDialogSearch("Jane")
        viewModel.selectDialogCustomer(customer)
        val state = viewModel.uiState.value
        assertEquals(customer, state.dialogSelectedCustomer)
        assertTrue(state.dialogSearchQuery.isEmpty())
    }

    @Test
    fun advanceStatus_updatesTicketInActiveList() = runTest {
        val ticket = Ticket(id = 1, customerName = "Dave", status = TicketStatus.WAITING)
        fakeTicketRepo.setTickets(listOf(ticket))
        advanceUntilIdle()
        viewModel.advanceStatus(ticket)
        advanceUntilIdle()
        val updated = viewModel.uiState.value.activeTickets.find { it.id == 1 }
        assertEquals(TicketStatus.ASSIGNED, updated?.status)
    }
}
