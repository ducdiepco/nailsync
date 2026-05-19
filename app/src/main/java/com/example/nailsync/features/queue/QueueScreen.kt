package com.example.nailsync.features.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nailsync.domain.model.Customer
import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.model.TicketStatus
import com.example.nailsync.domain.model.Technician
import com.example.nailsync.ui.theme.GradientEnd
import com.example.nailsync.ui.theme.GradientStart
import com.example.nailsync.ui.theme.NailPurple
import com.example.nailsync.ui.theme.NailTeal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QueueScreen(
    onTicketClick: (Int) -> Unit,
    onCustomersClick: () -> Unit,
    viewModel: QueueViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(GradientStart, GradientEnd),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        // Top bar: app name + CUSTOMERS button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "NAILSYNC",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Surface(
                onClick = onCustomersClick,
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.18f),
                contentColor = Color.White
            ) {
                Text(
                    "CUSTOMERS",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 52.dp)
        ) {
            val isWide = maxWidth >= 600.dp
            if (isWide) {
                TwoPaneQueueLayout(state, viewModel, onTicketClick)
            } else {
                NarrowQueueLayout(state, viewModel, onTicketClick)
            }
        }

        // FAB bottom-right
        FloatingActionButton(
            onClick = viewModel::showCreateDialog,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = NailTeal,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Ticket")
        }
    }

    if (state.showCreateDialog) {
        CreateTicketDialog(
            searchQuery = state.dialogSearchQuery,
            searchResults = state.dialogSearchResults,
            selectedCustomer = state.dialogSelectedCustomer,
            onSearchChange = viewModel::updateDialogSearch,
            onSelectCustomer = viewModel::selectDialogCustomer,
            onClearCustomer = viewModel::clearDialogCustomer,
            onDismiss = viewModel::hideCreateDialog,
            onCreate = { name, customerId -> viewModel.createTicket(name, customerId, onTicketClick) }
        )
    }
}

// ─── Two-pane (tablet / landscape) ─────────────────────────────────────────

@Composable
private fun TwoPaneQueueLayout(
    state: QueueUiState,
    viewModel: QueueViewModel,
    onTicketClick: (Int) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left: staff panel
        Column(
            modifier = Modifier
                .width(260.dp)
                .fillMaxHeight()
                .padding(start = 16.dp, top = 16.dp, bottom = 80.dp, end = 8.dp)
        ) {
            PanelHeader("STAFF  ${state.technicians.size}")
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.technicians, key = { it.id }) { tech ->
                    TechnicianCard(tech, state.activeTickets)
                }
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.White.copy(alpha = 0.2f))
        )

        // Right: ticket queue
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 12.dp, top = 16.dp, end = 16.dp, bottom = 80.dp)
        ) {
            PanelHeader("QUEUE  ${state.activeTickets.size}")
            Spacer(Modifier.height(8.dp))
            TicketQueueList(state.activeTickets, viewModel, onTicketClick)
        }
    }
}

// ─── Narrow (phone) ──────────────────────────────────────────────────────────

@Composable
private fun NarrowQueueLayout(
    state: QueueUiState,
    viewModel: QueueViewModel,
    onTicketClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 80.dp)
    ) {
        PanelHeader("QUEUE  ${state.activeTickets.size}")
        Spacer(Modifier.height(8.dp))
        TicketQueueList(state.activeTickets, viewModel, onTicketClick)
    }
}

// ─── Shared components ────────────────────────────────────────────────────────

@Composable
private fun PanelHeader(title: String) {
    Text(
        text = title,
        color = Color.White.copy(alpha = 0.7f),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp
    )
}

@Composable
private fun TechnicianCard(technician: Technician, activeTickets: List<Ticket>) {
    val servicesCount = activeTickets.count { ticket ->
        ticket.services.any { it.technicianId == technician.id }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = NailPurple
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = technician.name.first().toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = technician.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1030)
            )
            Text(
                text = if (technician.isAvailable) "Available" else "Busy",
                style = MaterialTheme.typography.labelSmall,
                color = if (technician.isAvailable) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
            if (servicesCount > 0) {
                Spacer(Modifier.height(2.dp))
                Surface(
                    shape = CircleShape,
                    color = NailPurple.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "$servicesCount",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = NailPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TicketQueueList(
    tickets: List<Ticket>,
    viewModel: QueueViewModel,
    onTicketClick: (Int) -> Unit
) {
    if (tickets.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active tickets", color = Color.White.copy(alpha = 0.6f))
        }
        return
    }
    val statusOrder = listOf(
        TicketStatus.READY_FOR_PAYMENT,
        TicketStatus.IN_PROGRESS,
        TicketStatus.ASSIGNED,
        TicketStatus.WAITING
    )
    val grouped = tickets.groupBy { it.status }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        statusOrder.forEach { status ->
            val group = grouped[status] ?: return@forEach
            if (group.isEmpty()) return@forEach
            item(key = "header_$status") {
                StatusGroupLabel(status)
                Spacer(Modifier.height(4.dp))
            }
            items(group, key = { it.id }) { ticket ->
                TicketQueueCard(
                    ticket = ticket,
                    onCardClick = { onTicketClick(ticket.id) },
                    onActionClick = {
                        if (ticket.status == TicketStatus.READY_FOR_PAYMENT) onTicketClick(ticket.id)
                        else viewModel.advanceStatus(ticket)
                    }
                )
            }
            item(key = "spacer_$status") { Spacer(Modifier.height(4.dp)) }
        }
    }
}

@Composable
private fun StatusGroupLabel(status: TicketStatus) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(status.color)
        )
        Text(
            text = status.displayName.uppercase(),
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun TicketQueueCard(
    ticket: Ticket,
    onCardClick: () -> Unit,
    onActionClick: () -> Unit
) {
    val timeStr = remember(ticket.createdAt) {
        SimpleDateFormat("h:mm a", Locale.US).format(Date(ticket.createdAt))
    }
    Card(
        onClick = onCardClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status color stripe
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ticket.status.color)
            )
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ticket.customerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1030)
                )
                if (ticket.services.isNotEmpty()) {
                    Text(
                        text = ticket.services.joinToString(" · ") { it.serviceName },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B5B8E),
                        maxLines = 1
                    )
                    val techNames = ticket.services.mapNotNull { it.technicianName }.distinct()
                    if (techNames.isNotEmpty()) {
                        Text(
                            text = techNames.joinToString(", "),
                            style = MaterialTheme.typography.labelSmall,
                            color = NailPurple,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "No services yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9E9E9E)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$${String.format(Locale.US, "%.2f", ticket.total)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1030)
                )
                Spacer(Modifier.height(6.dp))
                ActionChip(
                    label = if (ticket.status == TicketStatus.READY_FOR_PAYMENT) "PAY"
                            else ticket.status.actionLabel,
                    containerColor = if (ticket.status == TicketStatus.READY_FOR_PAYMENT)
                        NailTeal else NailPurple,
                    onClick = onActionClick
                )
            }
        }
    }
}

@Composable
private fun ActionChip(label: String, containerColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = containerColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// ─── Create ticket dialog ─────────────────────────────────────────────────────

@Composable
private fun CreateTicketDialog(
    searchQuery: String,
    searchResults: List<Customer>,
    selectedCustomer: Customer?,
    onSearchChange: (String) -> Unit,
    onSelectCustomer: (Customer) -> Unit,
    onClearCustomer: () -> Unit,
    onDismiss: () -> Unit,
    onCreate: (String, Int?) -> Unit
) {
    var manualName by remember { mutableStateOf(selectedCustomer?.fullName ?: "") }

    // Keep manualName in sync when a customer is selected/cleared
    if (selectedCustomer != null && manualName != selectedCustomer.fullName) {
        manualName = selectedCustomer.fullName
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Ticket", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Customer search / selected chip
                if (selectedCustomer != null) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = NailPurple.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    selectedCustomer.fullName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NailPurple
                                )
                                if (selectedCustomer.phone.isNotBlank()) {
                                    Text(
                                        selectedCustomer.phone,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            TextButton(onClick = onClearCustomer) { Text("Change") }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        label = { Text("Search customer") },
                        placeholder = { Text("Name or phone…") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (searchResults.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            searchResults.forEach { customer ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelectCustomer(customer) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(customer.fullName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        if (customer.phone.isNotBlank()) {
                                            Text(customer.phone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Name field (walk-in or pre-filled from customer)
                OutlinedTextField(
                    value = if (selectedCustomer != null) selectedCustomer.fullName else manualName,
                    onValueChange = { if (selectedCustomer == null) manualName = it },
                    label = { Text(if (selectedCustomer != null) "Customer Name" else "Walk-in Name") },
                    singleLine = true,
                    enabled = selectedCustomer == null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            val nameToUse = if (selectedCustomer != null) selectedCustomer.fullName else manualName
            Button(
                onClick = { onCreate(nameToUse, selectedCustomer?.id) },
                enabled = nameToUse.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NailTeal)
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Re-export StatusBadge for other screens
@Composable
fun StatusBadge(status: TicketStatus) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = status.color.copy(alpha = 0.15f)
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = status.color
        )
    }
}
