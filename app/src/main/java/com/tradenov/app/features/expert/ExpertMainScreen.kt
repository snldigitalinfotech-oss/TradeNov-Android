package com.tradenov.app.features.expert

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
import com.tradenov.app.features.trader.TraderAccountScreen

enum class ExpertBottomTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Desk", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    ADD_TIP("Add Tip", Icons.Filled.AddCircle, Icons.Outlined.AddCircle),
    MANAGE_TIPS("Signals", Icons.Filled.FormatListBulleted, Icons.Outlined.FormatListBulleted),
    EARNINGS("Payouts", Icons.Filled.Payments, Icons.Outlined.Payments),
    ACCOUNT("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

enum class ExpertSubScreen {
    PLANS,
    SUBSCRIBERS,
    PERFORMANCE
}

@Composable
fun ExpertMainScreen(
    repository: TradeNovRepository,
    sessionManager: SessionManager,
    onNavigateTraderHome: () -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(ExpertBottomTab.DASHBOARD) }
    var currentSubScreen by remember { mutableStateOf<ExpertSubScreen?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }

    // Hardware back press / edge back swipe prevention
    BackHandler(enabled = true) {
        if (currentSubScreen != null) {
            currentSubScreen = null
        } else if (selectedTab != ExpertBottomTab.DASHBOARD) {
            selectedTab = ExpertBottomTab.DASHBOARD
        } else {
            showExitDialog = true
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

    if (currentSubScreen != null) {
        when (currentSubScreen) {
            ExpertSubScreen.PLANS -> {
                ExpertPlansScreen(
                    repository = repository,
                    onNavigateBack = { currentSubScreen = null }
                )
            }
            ExpertSubScreen.SUBSCRIBERS -> {
                ExpertSubscribersScreen(
                    repository = repository,
                    onNavigateBack = { currentSubScreen = null }
                )
            }
            ExpertSubScreen.PERFORMANCE -> {
                ExpertPerformanceScreen(
                    repository = repository,
                    onNavigateBack = { currentSubScreen = null }
                )
            }
            null -> Unit
        }
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    ExpertBottomTab.values().forEach { tab ->
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
                ExpertBottomTab.DASHBOARD -> {
                    ExpertDashboardScreen(
                        repository = repository,
                        onNavigateAddTip = { selectedTab = ExpertBottomTab.ADD_TIP },
                        onNavigateManageTips = { selectedTab = ExpertBottomTab.MANAGE_TIPS },
                        onNavigateEarnings = { selectedTab = ExpertBottomTab.EARNINGS },
                        onNavigatePlans = { currentSubScreen = ExpertSubScreen.PLANS },
                        onNavigateSubscribers = { currentSubScreen = ExpertSubScreen.SUBSCRIBERS },
                        onNavigatePerformance = { currentSubScreen = ExpertSubScreen.PERFORMANCE },
                        onNavigateTraderHome = onNavigateTraderHome
                    )
                }
                ExpertBottomTab.ADD_TIP -> {
                    AddTipScreen(
                        repository = repository,
                        onTipPublished = { selectedTab = ExpertBottomTab.MANAGE_TIPS }
                    )
                }
                ExpertBottomTab.MANAGE_TIPS -> {
                    ManageTipsScreen(
                        repository = repository,
                        onNavigateAddTip = { selectedTab = ExpertBottomTab.ADD_TIP }
                    )
                }
                ExpertBottomTab.EARNINGS -> {
                    ExpertEarningsScreen(
                        repository = repository
                    )
                }
                ExpertBottomTab.ACCOUNT -> {
                    TraderAccountScreen(
                        repository = repository,
                        sessionManager = sessionManager,
                        onNavigateWallet = { selectedTab = ExpertBottomTab.EARNINGS },
                        onNavigateSubscriptions = { currentSubScreen = ExpertSubScreen.PLANS },
                        onNavigateWatchlist = {},
                        onNavigateReferrals = {},
                        onNavigateExpertDesk = onNavigateTraderHome,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}
