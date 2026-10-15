package com.tradenov.app.features.expert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.tradenov.app.core.model.MarketSegment

@Composable
fun ExpertSubscribersScreen(
    repository: TradeNovRepository,
    onNavigateBack: () -> Unit
) {
    val subscribers by repository.subscribers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedSegment by remember { mutableStateOf(MarketSegment.ALL) }

    val filteredSubscribers = remember(subscribers, searchQuery, selectedSegment) {
        subscribers.filter { sub ->
            val matchQuery = searchQuery.isBlank() ||
                    sub.traderName.contains(searchQuery, ignoreCase = true) ||
                    sub.planOrSignalName.contains(searchQuery, ignoreCase = true)
            val matchSegment = selectedSegment == MarketSegment.ALL || sub.marketSegment == selectedSegment
            matchQuery && matchSegment
        }
    }

    val activeCount = remember(subscribers) { subscribers.count { it.isActive } }
    val totalRevenue = remember(subscribers) { subscribers.sumOf { it.amountPaid } }

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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Trader Subscribers Roster",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Privacy-compliant directory of active pass holders",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
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
            contentPadding = PaddingValues(top = 14.dp, bottom = 40.dp)
        ) {
            // Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = CardShape,
                        color = OrangePrimary,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Active Subscribers", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f)))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$activeCount Traders", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color.White))
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = CardShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Recent Gross Share", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹${String.format("%,.0f", totalRevenue)}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = ProfitGreen))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Search Bar
            item {
                TradeNovTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search Subscribers",
                    placeholder = "Filter by trader name or pass title..."
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Market Segment Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        MarketSegment.ALL to "All Tiers",
                        MarketSegment.OPTIONS_INDEX to "Index Options",
                        MarketSegment.STOCKS to "Stocks Cash",
                        MarketSegment.COMMODITIES to "Commodities"
                    ).forEach { (segment, label) ->
                        item {
                            Surface(
                                shape = PillShape,
                                color = if (selectedSegment == segment) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clip(PillShape)
                            ) {
                                TextButton(
                                    onClick = { selectedSegment = segment },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (selectedSegment == segment) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Subscribers List
            if (filteredSubscribers.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.PeopleOutline,
                        title = "No Subscribers Found",
                        description = "Traders who purchase your signals or VIP passes will be listed here with renewal tracking."
                    )
                }
            } else {
                items(filteredSubscribers) { sub ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
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
                                        shape = CircleShape,
                                        color = OrangeLight,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = sub.traderName.take(1).uppercase(),
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = OrangePrimary
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = sub.traderName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = sub.planOrSignalName,
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                }

                                Surface(
                                    shape = PillShape,
                                    color = if (sub.isActive) ProfitLight else LossLight
                                ) {
                                    Text(
                                        text = if (sub.isActive) "ACTIVE" else "EXPIRED",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.sp,
                                            color = if (sub.isActive) ProfitGreen else LossRed
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Expires: ${sub.expiryDate}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                    )
                                    Text(
                                        text = "Joined: ${sub.purchaseDate}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${sub.amountPaid.toInt()}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = OrangePrimary)
                                    )
                                    if (sub.autoRenew) {
                                        Text(
                                            text = "Auto-Renew ON",
                                            style = MaterialTheme.typography.labelSmall.copy(color = ProfitGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
