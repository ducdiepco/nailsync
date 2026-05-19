package com.example.nailsync.core.navigation

object Routes {
    const val QUEUE = "queue"
    const val TICKET_DETAIL = "ticket/{ticketId}"
    const val ADD_SERVICE = "ticket/{ticketId}/add-service"
    const val TECHNICIAN_ASSIGNMENT = "service/{serviceId}/assign-technician"
    const val CHECKOUT = "ticket/{ticketId}/checkout"
    const val CUSTOMER_LIST = "customers"
    const val CUSTOMER_DETAIL = "customer/{customerId}"

    fun ticketDetail(ticketId: Int) = "ticket/$ticketId"
    fun addService(ticketId: Int) = "ticket/$ticketId/add-service"
    fun technicianAssignment(serviceId: Int) = "service/$serviceId/assign-technician"
    fun checkout(ticketId: Int) = "ticket/$ticketId/checkout"
    fun customerDetail(customerId: Int) = "customer/$customerId"
    fun newCustomer() = "customer/-1"
}
