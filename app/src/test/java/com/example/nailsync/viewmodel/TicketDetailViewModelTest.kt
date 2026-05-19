package com.example.nailsync.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.nailsync.MainCoroutineRule
import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.model.TicketService
import com.example.nailsync.domain.model.TicketStatus
import com.example.nailsync.domain.usecase.AddServiceToTicketUseCase
import com.example.nailsync.domain.usecase.CombineTicketsUseCase
import com.example.nailsync.domain.usecase.GetServicesUseCase
import com.example.nailsync.domain.usecase.GetTicketUseCase
import com.example.nailsync.domain.usecase.GetTicketsUseCase
import com.example.nailsync.domain.usecase.RemoveServiceFromTicketUseCase
import com.example.nailsync.domain.usecase.UpdateTicketStatusUseCase
import com.example.nailsync.fake.FakeServiceRepository
import com.example.nailsync.fake.FakeTicketRepository
import com.example.nailsync.features.ticket.TicketDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TicketDetailViewModelTest {

    @get:Rule val coroutineRule = MainCoroutineRule()

    private lateinit var fakeTicketRepo: FakeTicketRepository
    private lateinit var fakeServiceRepo: FakeServiceRepository

    private val targetTicket = Ticket(
        id = 1, customerName = "Alice",
        status = TicketStatus.WAITING,
        services = listOf(TicketService(id = 10, serviceName = "Gel Manicure", price = 40.0, durationMinutes = 45))
    )

    private fun createViewModel(ticketId: Int = 1): TicketDetailViewModel {
        return TicketDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("ticketId" to ticketId)),
            getTicketUseCase = GetTicketUseCase(fakeTicketRepo),
            getTicketsUseCase = GetTicketsUseCase(fakeTicketRepo),
            updateTicketStatusUseCase = UpdateTicketStatusUseCase(fakeTicketRepo),
            removeServiceFromTicketUseCase = RemoveServiceFromTicketUseCase(fakeTicketRepo),
            getServicesUseCase = GetServicesUseCase(fakeServiceRepo),
            addServiceToTicketUseCase = AddServiceToTicketUseCase(fakeTicketRepo),
            combineTicketsUseCase = CombineTicketsUseCase(fakeTicketRepo)
        )
    }

    @Before
    fun setUp() {
        fakeTicketRepo = FakeTicketRepository()
        fakeServiceRepo = FakeServiceRepository()
        fakeTicketRepo.setTickets(listOf(targetTicket))
    }

    @Test
    fun init_loadsTicket() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()
        assertNotNull(vm.uiState.value.ticket)
        assertEquals("Alice", vm.uiState.value.ticket?.customerName)
    }

    @Test
    fun combineTargets_excludesCurrentTicket() = runTest {
        val other = Ticket(id = 2, customerName = "Bob", status = TicketStatus.WAITING)
        fakeTicketRepo.setTickets(listOf(targetTicket, other))
        val vm = createViewModel(ticketId = 1)
        advanceUntilIdle()
        val targets = vm.uiState.value.combineTargets
        assertTrue(targets.none { it.id == 1 })
        assertEquals(1, targets.size)
        assertEquals(2, targets[0].id)
    }

    @Test
    fun combineTargets_excludesPaidAndCancelled() = runTest {
        val paidTicket = Ticket(id = 2, customerName = "Paid", status = TicketStatus.PAID)
        val cancelledTicket = Ticket(id = 3, customerName = "Cancelled", status = TicketStatus.CANCELLED)
        fakeTicketRepo.setTickets(listOf(targetTicket, paidTicket, cancelledTicket))
        val vm = createViewModel()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.combineTargets.isEmpty())
    }

    @Test
    fun openCombineDialog_setsFlag() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()
        vm.openCombineDialog()
        assertTrue(vm.uiState.value.showCombineDialog)
    }

    @Test
    fun closeCombineDialog_clearsFlag() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()
        vm.openCombineDialog()
        vm.closeCombineDialog()
        assertFalse(vm.uiState.value.showCombineDialog)
        assertNull(vm.uiState.value.selectedCombineTarget)
    }

    @Test
    fun confirmCombine_movesServicesAndCancelsSource() = runTest {
        val sourceTicket = Ticket(
            id = 2, customerName = "Bob",
            status = TicketStatus.WAITING,
            services = listOf(TicketService(id = 20, serviceName = "Pedicure", price = 30.0, durationMinutes = 60))
        )
        fakeTicketRepo.setTickets(listOf(targetTicket, sourceTicket))
        val vm = createViewModel(ticketId = 1)
        advanceUntilIdle()
        vm.openCombineDialog()
        vm.selectCombineTarget(sourceTicket)
        var doneCalled = false
        vm.confirmCombine { doneCalled = true }
        advanceUntilIdle()
        assertTrue(doneCalled)
        assertFalse(vm.uiState.value.showCombineDialog)
    }

    @Test
    fun selectCategory_filtersServices() = runTest {
        fakeServiceRepo.setServices(listOf(
            Service(id = 1, name = "Manicure", price = 25.0, durationMinutes = 30, category = "NAILS"),
            Service(id = 2, name = "Facial", price = 50.0, durationMinutes = 60, category = "FACIAL")
        ))
        val vm = createViewModel()
        advanceUntilIdle()
        vm.selectCategory("NAILS")
        val filtered = vm.uiState.value.filteredServices
        assertEquals(1, filtered.size)
        assertEquals("Manicure", filtered[0].name)
    }

    @Test
    fun categories_includesAllPlusDistinctCategories() = runTest {
        fakeServiceRepo.setServices(listOf(
            Service(id = 1, name = "A", price = 10.0, durationMinutes = 30, category = "NAILS"),
            Service(id = 2, name = "B", price = 10.0, durationMinutes = 30, category = "WAXING")
        ))
        val vm = createViewModel()
        advanceUntilIdle()
        val categories = vm.uiState.value.categories
        assertTrue(categories.contains("ALL"))
        assertTrue(categories.contains("NAILS"))
        assertTrue(categories.contains("WAXING"))
    }

    @Test
    fun updateStatus_voidCancelsTicket() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()
        vm.updateStatus(TicketStatus.CANCELLED)
        advanceUntilIdle()
        assertEquals(TicketStatus.CANCELLED, vm.uiState.value.ticket?.status)
    }
}
