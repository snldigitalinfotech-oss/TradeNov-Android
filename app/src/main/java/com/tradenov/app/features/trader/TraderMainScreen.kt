package com.tradenov.app.features.trader

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.SessionManager
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.LossRed
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
    onNavigateExpertDesk: () -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(TraderBottomTab.HOME) }
    var selectedTipIdForDetail by remember { mutableStateOf<String?>(null) }
    var showWalletScreen by remember { mutableStateOf(false) }
    var showReferralScreen by remember { mutableStateOf(false) }
    var showSupportScreen by remember { mutableStateOf(false) }
    var showJournalScreen by remember { mutableStateOf(false) }
    var showCoursesScreen by remember { mutableStateOf(false) }
    var showExpertsScreen by remember { mutableStateOf(false) }
    var showStatutoryScreen by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    // Hardware back press / edge back swipe prevention
    BackHandler(enabled = true) {
        when {
            selectedTipIdForDetail != null -> selectedTipIdForDetail = null
            showWalletScreen -> showWalletScreen = false
            showReferralScreen -> showReferralScreen = false
            showSupportScreen -> showSupportScreen = false
            showJournalScreen -> showJournalScreen = false
            showCoursesScreen -> showCoursesScreen = false
            showExpertsScreen -> showExpertsScreen = false
            showStatutoryScreen -> showStatutoryScreen = false
            selectedTab != TraderBottomTab.HOME -> selectedTab = TraderBottomTab.HOME
            else -> showExitDialog = true
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text("Exit TradeNov?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Are you sure you want to exit the application?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        (context as? Activity)?.finish()
                    }
                ) {
                    Text("Exit", color = LossRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            }
        )
    }

    // Detail Screen
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

    // Sub-modules
    if (showWalletScreen) {
        TraderWalletScreen(
            repository = repository,
            onBack = { showWalletScreen = false }
        )
        return
    }

    if (showReferralScreen) {
        TraderReferralScreen(
            repository = repository,
            onBack = { showReferralScreen = false }
        )
        return
    }

    if (showSupportScreen) {
        TraderSupportScreen(
            repository = repository,
            onBack = { showSupportScreen = false }
        )
        return
    }

    if (showJournalScreen) {
        TraderJournalScreen(
            repository = repository,
            onBack = { showJournalScreen = false }
        )
        return
    }

    if (showCoursesScreen) {
        TraderCoursesScreen(
            repository = repository,
            onBack = { showCoursesScreen = false }
        )
        return
    }

    if (showExpertsScreen) {
        TraderExpertsScreen(
            repository = repository,
            onBack = { showExpertsScreen = false },
            onNavigatePlans = {
                showExpertsScreen = false
                selectedTab = TraderBottomTab.SUBSCRIPTIONS
            }
        )
        return
    }

    if (showStatutoryScreen) {
        TraderStatutoryScreen(
            onBack = { showStatutoryScreen = false }
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
                    onNavigateAccount = { selectedTab = TraderBottomTab.ACCOUNT },
                    onNavigateJournal = { showJournalScreen = true },
                    onNavigateCourses = { showCoursesScreen = true },
                    onNavigateReferrals = { showReferralScreen = true },
                    onNavigateExperts = { showExpertsScreen = true }
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
                    onNavigateReferrals = { showReferralScreen = true },
                    onNavigateJournal = { showJournalScreen = true },
                    onNavigateCourses = { showCoursesScreen = true },
                    onNavigateExperts = { showExpertsScreen = true },
                    onNavigateSupport = { showSupportScreen = true },
                    onNavigateStatutory = { showStatutoryScreen = true },
                    onNavigateExpertDesk = onNavigateExpertDesk,
                    onLogout = onLogout
                )
            }
        }
    }
}

