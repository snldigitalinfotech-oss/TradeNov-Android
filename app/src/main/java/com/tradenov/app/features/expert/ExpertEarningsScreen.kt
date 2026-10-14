package com.tradenov.app.features.expert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import kotlinx.coroutines.launch

@Composable
fun ExpertEarningsScreen(
    repository: TradeNovRepository
) {
    val coroutineScope = rememberCoroutineScope()
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawAmount by remember { mutableStateOf("") }
    var withdrawAccount by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Earnings & Payouts",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Platform fee: 10% • Automated weekly NEFT / IMPS payouts",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // Net Balance Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = OrangePrimary,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Available for Withdrawal", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f)))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "₹18,450.00",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 34.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Gross Earnings", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f)))
                                Text("₹20,500.00", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Platform Fee (10%)", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f)))
                                Text("-₹2,050.00", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showWithdrawDialog = true },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = OrangePrimary)
                        ) {
                            Text("Request Payout", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Ledger breakdown
            item {
                Text("Revenue Breakdown", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                EarningsTransactionItem("Single Tip: NIFTY 24500 CE", "Trader: Aman V.", "+₹89.10", "₹99 - 10% fee", "Today, 09:28 AM")
                EarningsTransactionItem("VIP Weekly Pass", "Trader: Vikram R.", "+₹899.10", "₹999 - 10% fee", "Yesterday, 02:15 PM")
                EarningsTransactionItem("VIP Monthly Pass", "Trader: Priya S.", "+₹2,699.10", "₹2,999 - 10% fee", "12 Oct, 11:30 AM")
                EarningsTransactionItem("Single Tip: RELIANCE Swing", "Trader: Sanjay K.", "+₹134.10", "₹149 - 10% fee", "10 Oct, 04:45 PM")
            }
        }
    }

    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        isSubmitting = true
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(600)
                            isSubmitting = false
                            showWithdrawDialog = false
                        }
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                    } else {
                        Text("Confirm Payout", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) { Text("Cancel") }
            },
            title = { Text("Request Earnings Payout", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter amount to transfer to registered bank account:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    TradeNovTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it },
                        placeholder = "Amount (Max ₹18,450)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TradeNovTextField(
                        value = withdrawAccount,
                        onValueChange = { withdrawAccount = it },
                        placeholder = "Bank Account No or UPI ID"
                    )
                }
            }
        )
    }
}

@Composable
fun EarningsTransactionItem(
    title: String,
    buyer: String,
    amount: String,
    feeNote: String,
    date: String
) {
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
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text("$buyer • $date", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 11.sp))
                Text(feeNote, style = MaterialTheme.typography.labelSmall.copy(color = OrangePrimary, fontWeight = FontWeight.Medium))
            }
            Text(amount, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ProfitGreen))
        }
    }
}
