package com.tradenov.app.features.trader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.tradenov.app.core.model.NotificationCategory
import com.tradenov.app.core.model.NotificationItem

@Composable
fun TraderNotificationsScreen(
    repository: TradeNovRepository,
    onNavigateTipDetail: (String) -> Unit
) {
    val notifications by repository.notifications.collectAsState()
    var selectedCategory by remember { mutableStateOf(NotificationCategory.ALL) }

    val filteredNotifications = remember(notifications, selectedCategory) {
        if (selectedCategory == NotificationCategory.ALL) notifications
        else notifications.filter { it.category == selectedCategory }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = { repository.markAllNotificationsRead() }) {
                        Text("Mark all read", color = OrangePrimary, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Category Filter Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedCategory.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = OrangePrimary,
                    edgePadding = 0.dp
                ) {
                    NotificationCategory.values().forEach { category ->
                        Tab(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            text = {
                                Text(
                                    category.name,
                                    fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                                )
                            }
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
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
        ) {
            if (filteredNotifications.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.NotificationsNone,
                        title = "You're all caught up!",
                        description = "New tip alerts, target hits & account updates will show up here."
                    )
                }
            } else {
                items(filteredNotifications) { item ->
                    NotificationCard(
                        item = item,
                        onClick = {
                            if (item.deepLink != null && item.deepLink.contains("tip/")) {
                                val tipId = item.deepLink.substringAfter("tip/")
                                onNavigateTipDetail(tipId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    item: NotificationItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() },
        shape = CardShape,
        color = if (item.isRead) MaterialTheme.colorScheme.surface else OrangeLight.copy(alpha = 0.35f),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(OrangeLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.category) {
                        NotificationCategory.TIPS -> Icons.Default.TrendingUp
                        NotificationCategory.PAYMENTS -> Icons.Default.AccountBalanceWallet
                        NotificationCategory.SUBSCRIPTIONS -> Icons.Default.Stars
                        else -> Icons.Default.Campaign
                    },
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = item.timestamp,
                        style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 11.sp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                )
            }
        }
    }
}
