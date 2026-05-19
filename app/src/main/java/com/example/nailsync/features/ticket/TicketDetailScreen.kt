package com.example.nailsync.features.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nailsync.domain.model.Service
import com.example.nailsync.domain.model.Ticket
import com.example.nailsync.domain.model.TicketService
import com.example.nailsync.domain.model.TicketStatus
import com.example.nailsync.features.queue.StatusBadge
import com.example.nailsync.ui.theme.GradientEnd
import com.example.nailsync.ui.theme.GradientStart
import com.example.nailsync.ui.theme.NailPurple
import com.example.nailsync.ui.theme.NailTeal
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    onBack: () -> Unit,
    onAddService: (Int) -> Unit,
    onAssignTechnician: (Int) -> Unit,
    onCheckout: (Int) -> Unit,
    viewModel: TicketDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val gradientBrush = Brush.linearGradient(
        colors = listOf(GradientStart, GradientEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = state.ticket?.customerName ?: "Ticket",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.width(10.dp))
                        state.ticket?.let { StatusBadge(it.status) }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    val ticket = state.ticket
                    val combinableStatuses = setOf(
                        TicketStatus.WAITING, TicketStatus.ASSIGNED,
                        TicketStatus.IN_PROGRESS, TicketStatus.READY_FOR_PAYMENT
                    )
                    if (ticket != null && ticket.status in combinableStatuses && state.combineTargets.isNotEmpty()) {
                        TextButton(onClick = viewModel::openCombineDialog) {
                            Text("COMBINE", color = Color.White, fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(padding)
        ) {
            if (state.isLoading || state.ticket == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
                return@Box
            }
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val isWide = maxWidth >= 600.dp
                if (isWide) {
                    TwoPaneTicketLayout(state, viewModel, onAssignTechnician, onCheckout)
                } else {
                    NarrowTicketLayout(state, viewModel, onAddService, onAssignTechnician, onCheckout)
                }
            }

            if (state.showCombineDialog) {
                CombineDialog(
                    targets = state.combineTargets,
                    selected = state.selectedCombineTarget,
                    isCombining = state.isCombining,
                    onSelect = viewModel::selectCombineTarget,
                    onConfirm = { viewModel.confirmCombine(onBack) },
                    onDismiss = viewModel::closeCombineDialog
                )
            }
        }
    }
}

// ─── Two-pane ────────────────────────────────────────────────────────────────

@Composable
private fun TwoPaneTicketLayout(
    state: TicketDetailUiState,
    viewModel: TicketDetailViewModel,
    onAssignTechnician: (Int) -> Unit,
    onCheckout: (Int) -> Unit
) {
    val ticket = state.ticket!!
    Row(modifier = Modifier.fillMaxSize()) {
        // LEFT: ticket panel
        Column(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { PanelSectionLabel("SERVICES") }

                if (ticket.services.isEmpty()) {
                    item {
                        Text(
                            "Tap a service on the right to add",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    items(ticket.services, key = { it.id }) { service ->
                        TicketServiceItem(
                            service = service,
                            onAssign = { onAssignTechnician(service.id) },
                            onRemove = { viewModel.removeService(service.id) }
                        )
                    }
                }
            }

            // Bottom: totals + actions
            TicketTotalsPanel(
                ticket = ticket,
                onStatusAdvance = { next -> viewModel.updateStatus(next) },
                onCheckout = { onCheckout(ticket.id) },
                onCancel = { viewModel.updateStatus(TicketStatus.CANCELLED) }
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.White.copy(alpha = 0.2f))
        )

        // RIGHT: service catalog panel
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            PanelSectionLabel("SERVICES", modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp))
            CategoryTabRow(
                categories = state.categories,
                selected = state.selectedCategory,
                onSelect = viewModel::selectCategory
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.filteredServices, key = { it.id }) { service ->
                    ServiceCatalogCard(service = service, onAdd = { viewModel.addService(service) })
                }
            }
        }
    }
}

// ─── Narrow (phone) ──────────────────────────────────────────────────────────

@Composable
private fun NarrowTicketLayout(
    state: TicketDetailUiState,
    viewModel: TicketDetailViewModel,
    onAddService: (Int) -> Unit,
    onAssignTechnician: (Int) -> Unit,
    onCheckout: (Int) -> Unit
) {
    val ticket = state.ticket!!
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PanelSectionLabel("SERVICES")
                    Surface(
                        onClick = { onAddService(ticket.id) },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            if (ticket.services.isEmpty()) {
                item {
                    Text(
                        "No services yet",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                items(ticket.services, key = { it.id }) { service ->
                    TicketServiceItem(
                        service = service,
                        onAssign = { onAssignTechnician(service.id) },
                        onRemove = { viewModel.removeService(service.id) }
                    )
                }
            }
        }

        TicketTotalsPanel(
            ticket = ticket,
            onStatusAdvance = { next -> viewModel.updateStatus(next) },
            onCheckout = { onCheckout(ticket.id) },
            onCancel = { viewModel.updateStatus(TicketStatus.CANCELLED) }
        )
    }
}

// ─── Shared components ────────────────────────────────────────────────────────

@Composable
private fun PanelSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color.White.copy(alpha = 0.7f),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp
    )
}

@Composable
private fun TicketServiceItem(
    service: TicketService,
    onAssign: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    service.serviceName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1030)
                )
                Text(
                    text = service.technicianName ?: "Tap  to assign",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (service.technicianName != null) NailPurple else Color(0xFF9E9E9E)
                )
            }
            Text(
                text = "$${String.format(Locale.US, "%.2f", service.price)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1030)
            )
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onAssign, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Person, null, tint = NailPurple, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFFF44336), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun TicketTotalsPanel(
    ticket: Ticket,
    onStatusAdvance: (TicketStatus) -> Unit,
    onCheckout: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.25f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TotalRow("Subtotal", ticket.subtotal)
        TotalRow("Tax (8.5%)", ticket.tax)
        if (ticket.tip > 0) TotalRow("Tip", ticket.tip)
        HorizontalDivider(color = Color.White.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("TOTAL", color = Color.White, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium)
            Text(
                "$${String.format(Locale.US, "%.2f", ticket.total)}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cancel / void
            if (ticket.status !in listOf(TicketStatus.PAID, TicketStatus.CANCELLED)) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(46.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Text("VOID", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }
            }
            // Advance / Pay
            when (ticket.status) {
                TicketStatus.READY_FOR_PAYMENT -> {
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier.weight(2f).height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NailTeal)
                    ) {
                        Text("PAY", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }
                }
                TicketStatus.PAID, TicketStatus.CANCELLED -> { /* no action */ }
                else -> {
                    val next = ticket.status.nextStatus
                    if (next != null) {
                        Button(
                            onClick = { onStatusAdvance(next) },
                            modifier = Modifier.weight(2f).height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NailPurple)
                        ) {
                            Text(
                                "→ ${next.displayName}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
        Text(
            "$${String.format(Locale.US, "%.2f", amount)}",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CategoryTabRow(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        categories.forEach { cat ->
            val isSelected = cat == selected
            Surface(
                onClick = { onSelect(cat) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) Color.White else Color.Transparent,
                contentColor = if (isSelected) NailPurple else Color.White
            ) {
                Text(
                    text = cat,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun ServiceCatalogCard(service: Service, onAdd: () -> Unit) {
    Card(
        onClick = onAdd,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = service.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1030),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "$${String.format(Locale.US, "%.2f", service.price)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = NailPurple
            )
        }
    }
}

// ─── Combine dialog ───────────────────────────────────────────────────────────

@Composable
private fun CombineDialog(
    targets: List<Ticket>,
    selected: Ticket?,
    isCombining: Boolean,
    onSelect: (Ticket) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isCombining) onDismiss() },
        title = {
            Text("Combine Tickets", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Select a ticket to merge into this one. Its services will move here and it will be cancelled.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                targets.forEach { ticket ->
                    val isSelected = ticket.id == selected?.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isCombining) { onSelect(ticket) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelect(ticket) },
                            enabled = !isCombining,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = NailPurple
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                ticket.customerName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "${ticket.services.size} service(s) · ${ticket.status.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "$${String.format(Locale.US, "%.2f", ticket.subtotal)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = NailPurple
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = selected != null && !isCombining,
                colors = ButtonDefaults.buttonColors(containerColor = NailPurple)
            ) {
                if (isCombining) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("COMBINE", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isCombining) {
                Text("Cancel")
            }
        }
    )
}
