package com.example.nailsync.features.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nailsync.ui.theme.GradientEnd
import com.example.nailsync.ui.theme.GradientStart
import com.example.nailsync.ui.theme.NailPurple
import com.example.nailsync.ui.theme.NailTeal
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onPaymentComplete: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val ticket = state.ticket ?: return

    var customTipText by remember { mutableStateOf("") }
    val tipPresets = listOf(0.0, 0.10, 0.15, 0.20)
    val tipLabels = listOf("No Tip", "10%", "15%", "20%")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Checkout — ${ticket.customerName}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientStart, GradientEnd),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(padding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    SectionLabel("SERVICES")
                    Spacer(Modifier.height(6.dp))
                }

                items(ticket.services) { service ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    service.serviceName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1030)
                                )
                                service.technicianName?.let {
                                    Text(it, style = MaterialTheme.typography.labelSmall, color = NailPurple)
                                }
                            }
                            Text(
                                "$${String.format(Locale.US, "%.2f", service.price)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1030)
                            )
                        }
                    }
                }

                // Tip selector
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            SectionLabel("TIP")
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                tipPresets.forEachIndexed { idx, pct ->
                                    val tipValue = state.subtotal * pct
                                    val isSelected = state.tipAmount == tipValue
                                    Surface(
                                        onClick = {
                                            viewModel.setTip(tipValue)
                                            customTipText = ""
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = tipLabels[idx],
                                                modifier = Modifier.padding(vertical = 8.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) NailPurple else Color.White
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = customTipText,
                                onValueChange = { text ->
                                    customTipText = text
                                    text.toDoubleOrNull()?.let { viewModel.setTip(it) }
                                },
                                label = { Text("Custom amount", color = Color.White.copy(alpha = 0.7f)) },
                                prefix = { Text("$", color = Color.White) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                    cursorColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Total summary + PAY
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            TotalLine("Subtotal", state.subtotal)
                            TotalLine("Tax (8.5%)", state.tax)
                            if (state.tipAmount > 0) TotalLine("Tip", state.tipAmount)
                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "TOTAL",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "$${String.format(Locale.US, "%.2f", state.total)}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(14.dp))
                            Button(
                                onClick = { viewModel.confirmPayment(onPaymentComplete) },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NailTeal),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    "CONFIRM PAYMENT  ·  $${String.format(Locale.US, "%.2f", state.total)}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.7f),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        letterSpacing = androidx.compose.ui.unit.TextUnit(1.5f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
}

@Composable
private fun TotalLine(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
        Text(
            "$${String.format(Locale.US, "%.2f", amount)}",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
