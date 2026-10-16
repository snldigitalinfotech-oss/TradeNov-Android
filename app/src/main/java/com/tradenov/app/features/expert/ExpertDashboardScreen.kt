package com.tradenov.app.features.expert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import androidx.compose.foundation.clickable
import com.tradenov.app.core.model.TipStatus

@Composable
fun ExpertDashboardScreen(
    repository: TradeNovRepository,
    onNavigateAddTip: () -> Unit,
    onNavigateManageTips: () -> Unit,
    onNavigateEarnings: () -> Unit,
    onNavigatePlans: () -> Unit = {},
    onNavigateSubscribers: () -> Unit = {},
    onNavigatePerformance: () -> Unit = {},
    onNavigateTraderHome: () -> Unit = {}
) {
    val tips by repository.tips.collectAsState()
    val plans by repository.expertPlans.collectAsState()
    val subscribers by repository.subscribers.collectAsState()
    val runningTips = remember(tips) { tips.filter { it.status == TipStatus.RUNNING } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Expert Analytics Desk",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Verified Market Analyst Desk",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = PillShape,
                            color = OrangeLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("VERIFIED", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OrangePrimary, fontSize = 9.sp))
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            onClick = onNavigateTraderHome,
                            shape = PillShape,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Trader Mode ›", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        }
                    }
                }
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
            // Net Earnings Hero Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = OrangePrimary,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Net Available Earnings",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹18,450.00",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 34.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Gross Revenue", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f)))
                                Text("₹20,500", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Platform Fee (10%)", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f)))
                                Text("-₹2,050", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = onNavigateEarnings,
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = OrangePrimary
                            )
                        ) {
                            Text("Withdraw Earnings", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Quick Stats Grid (Interactive & Clickable)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CardShape)
                            .clickable { onNavigateSubscribers() },
                        shape = CardShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Subscribers", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("2,150", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("View Roster ›", style = MaterialTheme.typography.labelSmall.copy(color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 9.sp))
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CardShape)
                            .clickable { onNavigatePerformance() },
                        shape = CardShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Win Rate", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("85.2%", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = ProfitGreen))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Audited ›", style = MaterialTheme.typography.labelSmall.copy(color = ProfitGreen, fontWeight = FontWeight.Bold, fontSize = 9.sp))
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CardShape)
                            .clickable { onNavigateManageTips() },
                        shape = CardShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Running Calls", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${runningTips.size}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = OrangePrimary))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Manage ›", style = MaterialTheme.typography.labelSmall.copy(color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 9.sp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // VIP Subscription Passes Studio Card
            item {
                Surface(
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .clickable { onNavigatePlans() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = OrangeLight,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(22.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("VIP Subscription Passes Studio", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Text("${plans.size} Active recurring pass tiers • 90% Rev Share", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp))
                            }
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Quick Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TradeNovPrimaryButton(
                        text = "+ Publish Tip",
                        onClick = onNavigateAddTip,
                        modifier = Modifier.weight(1f)
                    )

                    TradeNovSecondaryButton(
                        text = "Manage Tips",
                        onClick = onNavigateManageTips,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Real-Time Syndication Desk Status Card
            item {
                Surface(
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Multi-Channel Syndication Desk", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Surface(shape = PillShape, color = ProfitLight) {
                                Text("LIVE SYNC", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(color = ProfitGreen, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Telegram VIP Desk: @TradeNovVIP", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp))
                            Text("WhatsApp VIP: Connected", style = MaterialTheme.typography.bodySmall.copy(color = ProfitGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Running Tips Quick Follow-Up List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active Signals Needing Attention", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    TextButton(onClick = onNavigateManageTips) {
                        Text("All (${runningTips.size})", color = OrangePrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (runningTips.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.DoneAll,
                        title = "No active running signals",
                        description = "Click '+ Publish Tip' to broadcast a new trade setup to subscribers."
                    )
                }
            } else {
                items(runningTips.take(3).size) { idx ->
                    val tip = runningTips[idx]
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
                                Text(tip.symbol, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text("Entry: ₹${tip.capturedEntryPrice} • CMP: ₹${tip.currentMarketPrice}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                            Button(
                                onClick = onNavigateManageTips,
                                shape = PillShape,
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                            ) {
                                Text("Update", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
