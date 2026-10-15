package com.tradenov.app.features.trader

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.SessionManager
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.OrangePrimary

enum class TraderBottomTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    TIPS("Tips", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
    SUBSCRIPTIONS("Plans", Icons.Filled.Stars, Icons.Outlined.Stars),
    NOTIFICATIONS("Alerts", Icons.Filled.Notifications, Icons.Outlined.Notifications),
    ACCOUNT("Account", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun TraderMainScreen(
    repository: TradeNovRepository,
    sessionManager: SessionManager,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(TraderBottomTab.HOME) }
    var selectedTipIdForDetail by remember { mutableStateOf<String?>(null) }
    var showWalletScreen by remember { mutableStateOf(false) }

    if (selectedTipIdForDetail != null) {
        TipDetailScreen(
            tipId = selectedTipIdForDetail!!,
            repository = repository,
            onBack = { selectedTipIdForDetail = null },
            onNavigateWallet = {
                selectedTipIdForDetail = null
                showWalletScreen = true
            }
        )
        return
    }

    if (showWalletScreen) {
        TraderWalletScreen(
            repository = repository,
            onBack = { showWalletScreen = false }
        )
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                TraderBottomTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OrangePrimary,
                            selectedTextColor = OrangePrimary,
                            indicatorColor = OrangePrimary.copy(alpha = 0.12f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            TraderBottomTab.HOME -> {
                TraderHomeScreen(
                    repository = repository,
                    onNavigateTips = { selectedTab = TraderBottomTab.TIPS },
                    onNavigateTipDetail = { id -> selectedTipIdForDetail = id },
                    onNavigateWallet = { showWalletScreen = true },
                    onNavigateNotifications = { selectedTab = TraderBottomTab.NOTIFICATIONS },
                    onNavigateExpertProfile = { selectedTab = TraderBottomTab.SUBSCRIPTIONS },
                    onNavigateWatchlist = { selectedTab = TraderBottomTab.TIPS },
                    onNavigateAccount = { selectedTab = TraderBottomTab.ACCOUNT }
                )
            }
            TraderBottomTab.TIPS -> {
                TipsMarketplaceScreen(
                    repository = repository,
                    onNavigateTipDetail = { id -> selectedTipIdForDetail = id },
                    onNavigateWallet = { showWalletScreen = true }
                )
            }
            TraderBottomTab.SUBSCRIPTIONS -> {
                SubscriptionsScreen(
                    repository = repository,
                    onNavigateWallet = { showWalletScreen = true }
                )
            }
            TraderBottomTab.NOTIFICATIONS -> {
                TraderNotificationsScreen(
                    repository = repository,
                    onNavigateTipDetail = { id -> selectedTipIdForDetail = id }
                )
            }
            TraderBottomTab.ACCOUNT -> {
                TraderAccountScreen(
                    repository = repository,
                    sessionManager = sessionManager,
                    onNavigateWallet = { showWalletScreen = true },
                    onNavigateSubscriptions = { selectedTab = TraderBottomTab.SUBSCRIPTIONS },
                    onNavigateWatchlist = { selectedTab = TraderBottomTab.TIPS },
                    onNavigateReferrals = {},
                    onLogout = onLogout
                )
            }
        }
    }
}
