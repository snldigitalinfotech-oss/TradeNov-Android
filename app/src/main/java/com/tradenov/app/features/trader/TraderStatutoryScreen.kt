package com.tradenov.app.features.trader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.designsystem.*

@Composable
fun TraderStatutoryScreen(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("SEBI Risk", "Terms", "Privacy", "Refunds")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Statutory & Legal Policies",
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
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            // Tab Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEachIndexed { idx, title ->
                        val isSelected = selectedTab == idx
                        Surface(
                            shape = PillShape,
                            color = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surface,
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.clickable { selectedTab = idx }
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            item {
                TradeNovCard {
                    when (selectedTab) {
                        0 -> {
                            Text("SEBI Compliance & Risk Disclosure", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "1. Securities and derivatives markets are subject to substantial market risks. TradeNov acts strictly as a technology aggregation and analytical platform connecting certified SEBI Research Analysts (RA) and traders.\n\n" +
                                        "2. Past performance is not indicative of future returns. The signals and analysis provided do not guarantee capital protection or positive returns.\n\n" +
                                        "3. 9 out of 10 individual traders in equity futures and options segment incur net losses (based on SEBI study). Traders are advised to assess their risk appetite and adhere strictly to stop-loss guidelines.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                            )
                        }
                        1 -> {
                            Text("Terms & Conditions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "By creating an account and subscribing to services on TradeNov, you agree to:\n\n" +
                                        "• Use research insights strictly for your personal informational and educational use.\n" +
                                        "• Do not redistribute, scrape, or resell broadcast signals.\n" +
                                        "• Maintain sufficient margin limits as prescribed by your individual broker.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                            )
                        }
                        2 -> {
                            Text("Privacy Policy", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "TradeNov is committed to safeguarding your personal data.\n\n" +
                                        "• Mobile numbers and email addresses are encrypted and used solely for account authentication, transactional OTPs, and push notifications.\n" +
                                        "• Payment credentials (cards, UPI tokens) are processed via PCI-DSS certified payment gateways and never stored on TradeNov servers.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                            )
                        }
                        3 -> {
                            Text("Cancellation & Refund Policy", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "1. Failed / Untriggered Tips: If a published signal expires without triggering the entry price, the unlock fee is automatically credited back to your TradeNov wallet.\n\n" +
                                        "2. Wallet Balances: Deposited wallet balances can be refunded to the original payment source within 7 business days upon raising a support ticket.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}
