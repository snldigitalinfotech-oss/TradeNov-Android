package com.tradenov.app.features.trader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.SessionManager
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*

@Composable
fun TraderAccountScreen(
    repository: TradeNovRepository,
    sessionManager: SessionManager,
    onNavigateWallet: () -> Unit,
    onNavigateSubscriptions: () -> Unit,
    onNavigateWatchlist: () -> Unit,
    onNavigateReferrals: () -> Unit,
    onNavigateJournal: () -> Unit = {},
    onNavigateCourses: () -> Unit = {},
    onNavigateExperts: () -> Unit = {},
    onNavigateSupport: () -> Unit = {},
    onNavigateStatutory: () -> Unit = {},
    onNavigateExpertDesk: () -> Unit = {},
    onLogout: () -> Unit
) {
    val user by sessionManager.currentUser.collectAsState()
    val themeMode by sessionManager.themeMode.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }

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
                    text = "My Account",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp)
        ) {
            // Profile Card (Matching Reference Screen 18)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(OrangeLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.name?.take(1) ?: "T").uppercase(),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user?.name ?: "Trader",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = user?.email ?: "trader@tradenov.com",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = user?.phone ?: "+91 98765 43210",
                                style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 11.sp)
                            )
                        }

                        Surface(
                            shape = PillShape,
                            color = OrangeLight
                        ) {
                            Text(
                                text = user?.role?.name ?: "TRADER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Financial & Portfolio Group
            item {
                Text(
                    text = "Financial & Purchases",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LightTextMuted)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                AccountMenuItem(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    title = "TradeNov Wallet",
                    subtitle = "Balance: ₹${(user?.walletBalance ?: 2500.0).toInt()}",
                    onClick = onNavigateWallet
                )
                AccountMenuItem(
                    icon = Icons.Outlined.Stars,
                    title = "VIP Subscriptions",
                    subtitle = "Manage active expert passes",
                    onClick = onNavigateSubscriptions
                )
                AccountMenuItem(
                    icon = Icons.Outlined.MenuBook,
                    title = "Trade Journal",
                    subtitle = "Track trades, discipline & personal P&L",
                    onClick = onNavigateJournal
                )
                AccountMenuItem(
                    icon = Icons.Outlined.BookmarkBorder,
                    title = "Watchlist",
                    subtitle = "Saved market breakout instruments",
                    onClick = onNavigateWatchlist
                )
                AccountMenuItem(
                    icon = Icons.Outlined.Share,
                    title = "Refer & Earn (10% Cashback)",
                    subtitle = "Invite peers and earn instant credits",
                    onClick = onNavigateReferrals
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Learning & Analysts Desk
            item {
                Text(
                    text = "Academy & Research Analysts",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LightTextMuted)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                AccountMenuItem(
                    icon = Icons.Outlined.VerifiedUser,
                    title = "Top Market Analysts",
                    subtitle = "Explore certified technical analysts & win rates",
                    onClick = onNavigateExperts
                )
                AccountMenuItem(
                    icon = Icons.Outlined.School,
                    title = "Trader Academy & Courses",
                    subtitle = "Masterclasses on Price Action & Option Greeks",
                    onClick = onNavigateCourses
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Professional Analyst & Creator Studio
            item {
                Text(
                    text = "Institutional & Analyst Studio",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LightTextMuted)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                AccountMenuItem(
                    icon = Icons.Outlined.Analytics,
                    title = "Expert Studio & Trading Desk",
                    subtitle = "Publish market signals, manage VIP passes & track win rates",
                    onClick = onNavigateExpertDesk
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // App Preferences & Support
            item {
                Text(
                    text = "Preferences & Support",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LightTextMuted)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                AccountMenuItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Theme Mode",
                    subtitle = themeMode.name,
                    onClick = { showThemeDialog = true }
                )
                AccountMenuItem(
                    icon = Icons.Outlined.Translate,
                    title = "Language",
                    subtitle = selectedLanguage,
                    onClick = { showLanguageDialog = true }
                )
                AccountMenuItem(
                    icon = Icons.Outlined.HelpOutline,
                    title = "Help & 24/7 Support Desk",
                    subtitle = "WhatsApp, Telegram, and Ticket Support",
                    onClick = onNavigateSupport
                )
                AccountMenuItem(
                    icon = Icons.Outlined.Shield,
                    title = "Market Risk & Legal Policies",
                    subtitle = "Risk Disclosures, Terms, Privacy & Refunds",
                    onClick = onNavigateStatutory
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Logout Button
            item {
                TradeNovSecondaryButton(
                    text = "Logout",
                    onClick = onLogout,
                    borderColor = LossRed,
                    contentColor = LossRed
                )
            }
        }
    }

    // Theme Switcher Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text("Close") }
            },
            title = { Text("Choose Theme", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    AppThemeMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    sessionManager.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = {
                                    sessionManager.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(mode.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            confirmButton = { TextButton(onClick = { showLanguageDialog = false }) { Text("Close") } },
            title = { Text("Choose Language", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf("English", "Hindi (हिन्दी)").forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == lang,
                                onClick = {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun AccountMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onClick() },
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(OrangeLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = LightTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
