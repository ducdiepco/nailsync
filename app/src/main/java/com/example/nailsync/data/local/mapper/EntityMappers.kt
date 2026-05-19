package com.example.nailsync.data.local.mapper

import com.example.nailsync.data.local.entity.ServiceEntity
import com.example.nailsync.data.local.entity.TechnicianEntity
import com.example.nailsync.data.local.entity.TicketServiceEntity
import com.example.nailsync.data.local.entity.TicketWithServices
import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.model.Technician
import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.model.TicketService
import com.example.nailsync.domain.model.TicketStatus

fun TicketWithServices.toDomain(): Ticket = Ticket(
    id = ticket.id,
    customerName = ticket.customerName,
    status = TicketStatus.valueOf(ticket.status),
    services = services.map { it.toDomain() },
    tip = ticket.tip,
    createdAt = ticket.createdAt,
    customerId = ticket.customerId
)

fun TicketServiceEntity.toDomain(): TicketService = TicketService(
    id = id,
    serviceName = serviceName,
    technicianId = technicianId,
    technicianName = technicianName,
    price = price,
    durationMinutes = durationMinutes
)

fun TechnicianEntity.toDomain(): Technician = Technician(
    id = id,
    name = name,
    isAvailable = isAvailable
)

fun ServiceEntity.toDomain(): Service = Service(
    id = id,
    name = name,
    price = price,
    durationMinutes = durationMinutes,
    category = category
)
