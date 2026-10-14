package com.tradenov.app.features.trader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.MarketSegment
import com.tradenov.app.core.model.Tip

@Composable
fun TipsMarketplaceScreen(
    repository: TradeNovRepository,
    onNavigateTipDetail: (String) -> Unit
) {
    val tips by repository.tips.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: From Subscriptions, 2: Purchased
    var selectedSegment by remember { mutableStateOf(MarketSegment.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredTips = remember(tips, selectedTab, selectedSegment, searchQuery) {
        tips.filter { tip ->
            val matchesTab = when (selectedTab) {
                1 -> tip.isUnlocked && tip.expertName.contains("Kunal", true)
                2 -> tip.isUnlocked
                else -> true
            }
            val matchesSegment = selectedSegment == MarketSegment.ALL || tip.marketSegment == selectedSegment
            val matchesSearch = searchQuery.isBlank() ||
                    tip.symbol.contains(searchQuery, true) ||
                    tip.expertName.contains(searchQuery, true)
            matchesTab && matchesSegment && matchesSearch
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 8.dp)
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

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs: All Tips | Subscriptions | Purchased
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = OrangePrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("All Tips (${tips.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Subscribed", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Purchased (${tips.count { it.isUnlocked }})", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
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
                    EmptyStateView(
                        icon = Icons.Default.FilterListOff,
                        title = "No tips matching criteria",
                        description = "Try selecting another market segment or clearing search filters."
                    )
                }
            } else {
                items(filteredTips) { tip ->
                    TipListingCard(
                        tip = tip,
                        onClick = { onNavigateTipDetail(tip.id) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
