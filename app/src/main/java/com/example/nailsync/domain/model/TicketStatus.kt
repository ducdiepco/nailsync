package com.example.nailsync.domain.model

import androidx.compose.ui.graphics.Color

enum class TicketStatus {
    WAITING, ASSIGNED, IN_PROGRESS, READY_FOR_PAYMENT, PAID, CANCELLED;

    val displayName: String get() = when (this) {
        WAITING -> "Waiting"
        ASSIGNED -> "Assigned"
        IN_PROGRESS -> "In Progress"
        READY_FOR_PAYMENT -> "Ready for Payment"
        PAID -> "Paid"
        CANCELLED -> "Cancelled"
    }

    val color: Color get() = when (this) {
        WAITING -> Color(0xFF6B9BD2)
        ASSIGNED -> Color(0xFFFF9800)
        IN_PROGRESS -> Color(0xFFFFC107)
        READY_FOR_PAYMENT -> Color(0xFF4CAF50)
        PAID -> Color(0xFF9E9E9E)
        CANCELLED -> Color(0xFFF44336)
    }

    val nextStatus: TicketStatus? get() = when (this) {
        WAITING -> ASSIGNED
        ASSIGNED -> IN_PROGRESS
        IN_PROGRESS -> READY_FOR_PAYMENT
        else -> null
    }

    val actionLabel: String get() = when (this) {
        WAITING -> "Assign"
        ASSIGNED -> "Start"
        IN_PROGRESS -> "Complete"
        READY_FOR_PAYMENT -> "Checkout"
        else -> ""
    }
}
