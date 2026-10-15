package com.tradenov.app.features.trader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.MarketSegment
import com.tradenov.app.core.model.Tip
import com.tradenov.app.core.model.TipStatus
import com.tradenov.app.core.model.TradeAction
import kotlinx.coroutines.launch

@Composable
fun TipsMarketplaceScreen(
    repository: TradeNovRepository,
    onNavigateTipDetail: (String) -> Unit,
    onNavigateWallet: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val tips by repository.tips.collectAsState()
    val wallet by repository.wallet.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Live Setups, 1: Subscribed, 2: Purchased, 3: Performance
    var selectedSegment by remember { mutableStateOf(MarketSegment.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var tipToUnlock by remember { mutableStateOf<Tip?>(null) }
    var unlockedSuccessTip by remember { mutableStateOf<Tip?>(null) }

    val filteredTips = remember(tips, selectedTab, selectedSegment, searchQuery) {
        tips.filter { tip ->
            val matchesTab = when (selectedTab) {
                0 -> tip.status == TipStatus.RUNNING
                1 -> tip.isUnlocked && tip.expertName.contains("Kunal", true)
                2 -> tip.isUnlocked
                3 -> tip.status != TipStatus.RUNNING
                else -> true
            }
            val matchesSegment = selectedSegment == MarketSegment.ALL || tip.marketSegment == selectedSegment
            val matchesSearch = searchQuery.isBlank() ||
                    tip.symbol.contains(searchQuery, true) ||
                    tip.expertName.contains(searchQuery, true)
            matchesTab && matchesSegment && matchesSearch
        }
    }

    val runningCount = tips.count { it.status == TipStatus.RUNNING }
    val subscribedCount = tips.count { it.isUnlocked && it.expertName.contains("Kunal", true) }
    val purchasedCount = tips.count { it.isUnlocked }
    val performanceCount = tips.count { it.status != TipStatus.RUNNING }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 4.dp)
            ) {
                Text(
                    text = "Tips Marketplace",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                TradeNovTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search by symbol (e.g. RELIANCE, NIFTY)...",
                    leadingIcon = Icons.Outlined.Search
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Tabs: Live Setups | Subscribed | Purchased | Performance
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = OrangePrimary,
                    edgePadding = 0.dp
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Live Setups ($runningCount)",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Subscribed ($subscribedCount)",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "Purchased ($purchasedCount)",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Text(
                                "Performance ($performanceCount)",
                                fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
        ) {
            // Market Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MarketSegment.values()) { segment ->
                        MarketChip(
                            text = segment.displayName,
                            isSelected = selectedSegment == segment,
                            onClick = { selectedSegment = segment }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            if (filteredTips.isEmpty()) {
                item {
                    val emptyTitle = when (selectedTab) {
                        0 -> "No Live Active Setups"
                        1 -> "No Subscribed Setups Yet"
                        2 -> "No Purchased Setups"
                        3 -> "No Closed Trades"
                        else -> "No tips matching criteria"
                    }
                    val emptyDesc = when (selectedTab) {
                        0 -> "SEBI analysts publish fresh setups during active market hours."
                        1 -> "Subscribe to expert monthly plans to automatically unlock VIP tips."
                        2 -> "Unlocked trades and verified calls will appear here permanently."
                        3 -> "Past closed and target-achieved setups will be cataloged here."
                        else -> "Try selecting another market segment or clearing search filters."
                    }
                    EmptyStateView(
                        icon = if (selectedTab == 3) Icons.Default.Assessment else Icons.Default.FilterListOff,
                        title = emptyTitle,
                        description = emptyDesc
                    )
                }
            } else {
                if (selectedTab == 3) {
                    // Performance Tab: Closed Trades with Entry, Exit, Outcome, and Rupee P&L + Pct
                    items(filteredTips) { tip ->
                        PerformanceTradeCard(
                            tip = tip,
                            onClick = { onNavigateTipDetail(tip.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    // Live, Subscribed, or Purchased Tabs
                    items(filteredTips) { tip ->
                        TipListingCard(
                            tip = tip,
                            onClick = {
                                if (tip.isUnlocked) {
                                    onNavigateTipDetail(tip.id)
                                } else {
                                    tipToUnlock = tip
                                }
                            },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }

    // Purchase Confirmation Modal (with Coupon Code input and Wallet check)
    if (tipToUnlock != null) {
        TipPurchaseConfirmationDialog(
            tip = tipToUnlock!!,
            walletBalance = wallet.availableBalance,
            onDismiss = { tipToUnlock = null },
            onConfirmPay = { couponCode, discountPercent ->
                val targetTip = tipToUnlock!!
                tipToUnlock = null
                coroutineScope.launch {
                    val res = repository.unlockTip(targetTip.id, couponCode, discountPercent)
                    if (res.isSuccess) {
                        unlockedSuccessTip = targetTip.copy(isUnlocked = true)
                    }
                }
            },
            onNavigateWallet = onNavigateWallet
        )
    }

    // Purchase Success & VIP Onboarding Dialog
    if (unlockedSuccessTip != null) {
        VipOnboardingSuccessDialog(
            tip = unlockedSuccessTip!!,
            onDismiss = {
                val tipId = unlockedSuccessTip!!.id
                unlockedSuccessTip = null
                onNavigateTipDetail(tipId)
            }
        )
    }
}

@Composable
fun PerformanceTradeCard(
    tip: Tip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isProfit = tip.profitLossRupees >= 0
    val outcomeColor = when (tip.status) {
        TipStatus.TARGET_HIT -> ProfitGreen
        TipStatus.STOP_LOSS_HIT -> LossRed
        else -> OrangePrimary
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Symbol & Segment, Outcome Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isProfit) ProfitGreenLight else LossRedLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isProfit) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (isProfit) ProfitGreen else LossRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = tip.symbol,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${tip.marketSegment.displayName} • ${tip.action.name} • By ${tip.expertName}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = PillShape,
                    color = outcomeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = tip.status.name.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = outcomeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics Grid: Entry, Exit, Net Rupee P&L & Pct
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = InputShape,
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Entry Price", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Text(
                            "₹${tip.capturedEntryPrice}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Column {
                        Text("Exit / CMP", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Text(
                            "₹${tip.currentMarketPrice}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Realized P&L", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Text(
                            text = tip.displayProfitLossText,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isProfit) ProfitGreen else LossRed
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Timestamp and View Audit Trail link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Closed: ${tip.createdAt}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LightTextMuted,
                        fontSize = 11.sp
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View Audit Trail",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OrangePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
