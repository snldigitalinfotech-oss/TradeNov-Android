package com.tradenov.app.features.trader

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.MarketSegment
import com.tradenov.app.core.model.TradeAction
import com.tradenov.app.core.model.TradeJournalEntry
import java.util.UUID

@Composable
fun TraderJournalScreen(
    repository: TradeNovRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val journalEntries by repository.journalEntries.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    // Aggregate statistics
    val totalTrades = journalEntries.size
    val winningTrades = journalEntries.count { it.pnlRupees > 0 }
    val winRate = if (totalTrades > 0) (winningTrades.toDouble() / totalTrades * 100) else 0.0
    val totalPnl = journalEntries.sumOf { it.pnlRupees }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Trade Journal",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Trade", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            // Stats Highlights
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Performance Summary", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${if (totalPnl >= 0) "+₹" else "-₹"}${String.format("%,.0f", Math.abs(totalPnl))}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (totalPnl >= 0) ProfitGreen else LossRed
                                )
                            )

                            Surface(
                                shape = PillShape,
                                color = if (winRate >= 50) ProfitGreenLight else LossRedLight
                            ) {
                                Text(
                                    text = "Win Rate: ${String.format("%.1f", winRate)}%",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (winRate >= 50) ProfitGreen else LossRed
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Logged", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                                Text("$totalTrades Trades", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Column {
                                Text("Profitable", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                                Text("$winningTrades Calls", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ProfitGreen))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Losses", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                                Text("${totalTrades - winningTrades} Calls", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LossRed))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Journal Entries Header
            item {
                Text(
                    text = "Trade History (${journalEntries.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (journalEntries.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.MenuBook,
                        title = "Your journal is empty",
                        description = "Tap '+ Log Trade' to start tracking your market discipline and setups."
                    )
                }
            } else {
                items(journalEntries) { entry ->
                    JournalEntryCard(
                        entry = entry,
                        onDelete = { repository.deleteJournalEntry(entry.id) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        AddJournalEntryDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { entry ->
                repository.addJournalEntry(entry)
                showAddDialog = false
                Toast.makeText(context, "Trade logged to journal!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun JournalEntryCard(
    entry: TradeJournalEntry,
    onDelete: () -> Unit
) {
    val isProfit = entry.pnlRupees >= 0

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = PillShape,
                        color = if (entry.action == TradeAction.BUY) ProfitGreen else LossRed
                    ) {
                        Text(
                            text = entry.action.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = entry.symbol,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${entry.marketSegment.displayName}",
                        style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 11.sp)
                    )
                }

                Text(
                    text = "${if (isProfit) "+₹" else "-₹"}${String.format("%,.0f", Math.abs(entry.pnlRupees))} (${if (isProfit) "+" else ""}${String.format("%.1f", entry.pnlPercent)}%)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isProfit) ProfitGreen else LossRed
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = InputShape,
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Entry", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Text("₹${entry.entryPrice}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Column {
                        Text("Exit", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Text("₹${entry.exitPrice}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Column {
                        Text("Qty", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Text("${entry.quantity}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Emotion", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Text(entry.emotion, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = OrangePrimary))
                    }
                }
            }

            if (entry.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${entry.notes}\"",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(entry.date, style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 10.sp))
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelSmall.copy(color = LossRed, fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { onDelete() }
                )
            }
        }
    }
}

@Composable
fun AddJournalEntryDialog(
    onDismiss: () -> Unit,
    onConfirm: (TradeJournalEntry) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var selectedAction by remember { mutableStateOf(TradeAction.BUY) }
    var entryPriceStr by remember { mutableStateOf("") }
    var exitPriceStr by remember { mutableStateOf("") }
    var quantityStr by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }
    var emotion by remember { mutableStateOf("Disciplined") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Trade to Journal", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it.uppercase() },
                    label = { Text("Symbol (e.g. NIFTY, RELIANCE)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { selectedAction = TradeAction.BUY },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedAction == TradeAction.BUY) ProfitGreen else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("BUY", color = if (selectedAction == TradeAction.BUY) Color.White else MaterialTheme.colorScheme.onSurface)
                    }

                    Button(
                        onClick = { selectedAction = TradeAction.SELL },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedAction == TradeAction.SELL) LossRed else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("SELL", color = if (selectedAction == TradeAction.SELL) Color.White else MaterialTheme.colorScheme.onSurface)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = entryPriceStr,
                        onValueChange = { entryPriceStr = it },
                        label = { Text("Entry Price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = exitPriceStr,
                        onValueChange = { exitPriceStr = it },
                        label = { Text("Exit Price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("Quantity / Lots") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Setup Rationale & Reflection") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val entry = entryPriceStr.toDoubleOrNull() ?: 0.0
                    val exit = exitPriceStr.toDoubleOrNull() ?: 0.0
                    val qty = quantityStr.toIntOrNull() ?: 1
                    val pnlPerUnit = if (selectedAction == TradeAction.BUY) (exit - entry) else (entry - exit)
                    val totalPnl = pnlPerUnit * qty
                    val pnlPct = if (entry > 0) ((exit - entry) / entry * 100) else 0.0

                    if (symbol.isNotBlank() && entry > 0 && exit > 0) {
                        onConfirm(
                            TradeJournalEntry(
                                id = UUID.randomUUID().toString(),
                                symbol = symbol,
                                marketSegment = MarketSegment.STOCKS,
                                action = selectedAction,
                                entryPrice = entry,
                                exitPrice = exit,
                                quantity = qty,
                                pnlRupees = totalPnl,
                                pnlPercent = pnlPct,
                                date = "Today",
                                notes = notes,
                                emotion = emotion
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Save Entry")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
