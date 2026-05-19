package com.example.nailsync.domain.model

data class Ticket(
    val id: Int = 0,
    val customerName: String,
    val status: TicketStatus = TicketStatus.WAITING,
    val services: List<TicketService> = emptyList(),
    val tip: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val customerId: Int? = null
) {
    val subtotal: Double get() = services.sumOf { it.price }
    val tax: Double get() = subtotal * TAX_RATE
    val total: Double get() = subtotal + tax + tip

    companion object {
        const val TAX_RATE = 0.085
    }
}
