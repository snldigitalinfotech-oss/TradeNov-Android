package com.tradenov.app.features.trader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.TransactionType
import kotlinx.coroutines.launch

@Composable
fun TraderWalletScreen(
    repository: TradeNovRepository,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val wallet by repository.wallet.collectAsState()

    var showAddMoneyDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var addAmountText by remember { mutableStateOf("1000") }
    var utrNumberText by remember { mutableStateOf("") }
    var withdrawAmountText by remember { mutableStateOf("") }
    var withdrawUpiText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TradeNov Wallet",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
        ) {
            // Balance Hero Card (Orange Gradient / Brand style)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = OrangePrimary,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Available Balance",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹${String.format("%,.2f", wallet.availableBalance)}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 34.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showAddMoneyDialog = true },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = PillShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = OrangePrimary
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Money", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showWithdrawDialog = true },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = PillShape,
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Color.White)
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Withdraw", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Wallet Summary Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = CardShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Added", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹${wallet.totalAdded.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ProfitGreen))
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = CardShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Spent", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹${wallet.totalSpent.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            // Transactions Ledger Header
            item {
                Text(
                    text = "Transaction History",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            if (wallet.transactions.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.ReceiptLong,
                        title = "No transactions yet",
                        description = "Your tip unlocks and wallet topups will appear here."
                    )
                }
            } else {
                items(wallet.transactions) { tx ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        shape = CardShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (tx.type == TransactionType.CREDIT) ProfitGreenLight else OrangeLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (tx.type == TransactionType.CREDIT) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = if (tx.type == TransactionType.CREDIT) ProfitGreen else OrangePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(tx.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    Text("${tx.date} • ${tx.id}", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 11.sp))
                                }
                            }

                            Text(
                                text = "${if (tx.type == TransactionType.CREDIT) "+" else "-"}₹${tx.amount.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.type == TransactionType.CREDIT) ProfitGreen else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Money Dialog
    if (showAddMoneyDialog) {
        AlertDialog(
            onDismissRequest = { showAddMoneyDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = addAmountText.toDoubleOrNull() ?: 1000.0
                        isProcessing = true
                        coroutineScope.launch {
                            repository.addMoney(amount, "UPI / Instant Gateway", utrNumberText.ifBlank { null })
                            isProcessing = false
                            showAddMoneyDialog = false
                        }
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                    } else {
                        Text("Add ₹$addAmountText", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMoneyDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Add Money to Wallet", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select or enter amount (INR):", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted))
                    Spacer(modifier = Modifier.height(10.dp))
                    TradeNovTextField(
                        value = addAmountText,
                        onValueChange = { addAmountText = it },
                        placeholder = "Amount",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("500", "1000", "2000", "5000").forEach { preset ->
                            Surface(
                                modifier = Modifier.weight(1f).clickable { addAmountText = preset },
                                shape = PillShape,
                                color = if (addAmountText == preset) OrangeLight else MaterialTheme.colorScheme.surface,
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Text(
                                    text = "₹$preset",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (addAmountText == preset) OrangePrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Payment Method: Instant UPI QR / Cards / NetBanking", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted))
                }
            }
        )
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = withdrawAmountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0 && amount <= wallet.availableBalance) {
                            coroutineScope.launch {
                                repository.addMoney(-amount, "Withdrawal to ${withdrawUpiText.ifBlank { "UPI" }}")
                                showWithdrawDialog = false
                            }
                        }
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Submit Request", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) { Text("Cancel") }
            },
            title = { Text("Request Withdrawal", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Available to withdraw: ₹${wallet.availableBalance.toInt()}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    TradeNovTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        placeholder = "Enter amount",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TradeNovTextField(
                        value = withdrawUpiText,
                        onValueChange = { withdrawUpiText = it },
                        placeholder = "Enter UPI ID (e.g. name@okaxis)"
                    )
                }
            }
        )
    }
}
