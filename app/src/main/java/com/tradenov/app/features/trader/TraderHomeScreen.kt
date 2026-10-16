package com.tradenov.app.features.trader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.*
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun TraderHomeScreen(
    repository: TradeNovRepository,
    onNavigateTips: () -> Unit,
    onNavigateTipDetail: (String) -> Unit,
    onNavigateWallet: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateExpertProfile: (String) -> Unit,
    onNavigateWatchlist: () -> Unit,
    onNavigateAccount: () -> Unit,
    onNavigateJournal: () -> Unit = {},
    onNavigateCourses: () -> Unit = {},
    onNavigateReferrals: () -> Unit = {},
    onNavigateExperts: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val user by repository.sessionManager.currentUser.collectAsState()
    val wallet by repository.wallet.collectAsState()
    val tips by repository.tips.collectAsState()
    val experts by repository.experts.collectAsState()
    val notifications by repository.notifications.collectAsState()
    val referralData by repository.referralData.collectAsState()
    val unreadNotifsCount = notifications.count { !it.isRead }

    var selectedSegment by remember { mutableStateOf(MarketSegment.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var tipToUnlock by remember { mutableStateOf<Tip?>(null) }
    var unlockedSuccessTip by remember { mutableStateOf<Tip?>(null) }

    val filteredTips = remember(tips, selectedSegment, searchQuery) {
        tips.filter {
            (selectedSegment == MarketSegment.ALL || it.marketSegment == selectedSegment) &&
            (searchQuery.isBlank() || it.symbol.contains(searchQuery, true) || it.expertName.contains(searchQuery, true))
        }
    }

    val liveActiveTips = remember(filteredTips) {
        filteredTips.filter { it.status == TipStatus.RUNNING }
    }

    val unlockedTips = remember(tips) {
        tips.filter { it.isUnlocked }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Top Orange Header Hero (Matching Reference Screens 6 & 7)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                        .background(OrangePrimary)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column {
                        // User Greeting & Wallet Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clickable { onNavigateAccount() }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(2.dp, Color.White.copy(alpha = 0.85f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!user?.avatarUrl.isNullOrBlank()) {
                                            if (user!!.avatarUrl!!.startsWith("http")) {
                                                AsyncImage(
                                                    model = user!!.avatarUrl,
                                                    contentDescription = "Trader Profile",
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Text(
                                                    text = user!!.avatarUrl!!,
                                                    style = MaterialTheme.typography.titleLarge
                                                )
                                            }
                                        } else {
                                            val initials = remember(user?.name) {
                                                user?.name?.trim()?.split("\\s+".toRegex())
                                                    ?.mapNotNull { it.firstOrNull()?.toString() }
                                                    ?.take(2)
                                                    ?.joinToString("")
                                                    ?.uppercase()
                                                    ?.ifEmpty { "TR" } ?: "TR"
                                            }
                                            Text(
                                                text = initials,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = OrangePrimary
                                                )
                                            )
                                        }
                                    }
                                    // Verified Trader Badge
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .align(Alignment.BottomEnd)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified Trader",
                                            tint = EmeraldGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Welcome back,",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 12.sp
                                        )
                                    )
                                    Text(
                                        text = user?.name ?: "Trader",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Wallet Balance Pill
                                Surface(
                                    modifier = Modifier
                                        .clip(PillShape)
                                        .clickable { onNavigateWallet() },
                                    shape = PillShape,
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "₹${(user?.walletBalance ?: 2500.0).toInt()}",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }

                                // Notification Bell
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .clickable { onNavigateNotifications() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Notifications,
                                        contentDescription = "Notifications",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    if (unreadNotifsCount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(x = (-2).dp, y = 2.dp)
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(LossRed)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Search Bar Inside Top Hero
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = PillShape,
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "Search",
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (searchQuery.isEmpty()) "Search stocks, options, experts..." else searchQuery,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (searchQuery.isEmpty()) LightTextMuted else LightTextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }


            // Real-Time Stats Grid (Matching Web Dashboard Section 1.1)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DashboardStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Wallet Balance",
                            value = "₹${(user?.walletBalance ?: wallet.availableBalance).toInt()}",
                            subtext = "+ Add Money",
                            badgeColor = ProfitGreenLight,
                            textColor = ProfitGreen,
                            onClick = onNavigateWallet
                        )
                        DashboardStatCard(
                            modifier = Modifier.weight(1f),
                            title = "VIP Subscriptions",
                            value = "PRO PASS",
                            subtext = "Unlimited Access",
                            badgeColor = OrangeLight,
                            textColor = OrangePrimary,
                            onClick = onNavigateTips
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DashboardStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Tips Unlocked",
                            value = "${unlockedTips.size} Active",
                            subtext = "View Portfolio",
                            badgeColor = MaterialTheme.colorScheme.surfaceVariant,
                            textColor = MaterialTheme.colorScheme.onSurface,
                            onClick = onNavigateTips
                        )
                        DashboardStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Referral Rewards",
                            value = "₹${referralData.totalEarnedRupees.toInt()}",
                            subtext = "10% Cashback",
                            badgeColor = ProfitGreenLight,
                            textColor = ProfitGreen,
                            onClick = onNavigateReferrals
                        )
                    }
                }
            }

            // Quick Tools & Hub Grid
            item {
                Spacer(modifier = Modifier.height(22.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Quick Tools & Hub",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionItem(
                            icon = Icons.Default.TrendingUp,
                            label = "Marketplace",
                            onClick = onNavigateTips
                        )
                        QuickActionItem(
                            icon = Icons.Default.Stars,
                            label = "VIP Plans",
                            onClick = onNavigateTips
                        )
                        QuickActionItem(
                            icon = Icons.Default.VerifiedUser,
                            label = "Top Analysts",
                            onClick = onNavigateExperts
                        )
                        QuickActionItem(
                            icon = Icons.Default.MenuBook,
                            label = "Journal",
                            onClick = onNavigateJournal
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionItem(
                            icon = Icons.Default.School,
                            label = "Academy",
                            onClick = onNavigateCourses
                        )
                        QuickActionItem(
                            icon = Icons.Default.Share,
                            label = "Refer 10%",
                            onClick = onNavigateReferrals
                        )
                        QuickActionItem(
                            icon = Icons.Default.AccountBalanceWallet,
                            label = "My Wallet",
                            onClick = onNavigateWallet
                        )
                        QuickActionItem(
                            icon = Icons.Default.BookmarkBorder,
                            label = "Watchlist",
                            onClick = onNavigateWatchlist
                        )
                    }
                }
            }

            // Active Purchased Setups Strip (Matching Web Dashboard Section 1.1)
            if (unlockedTips.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(22.dp))
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "My Active Signals",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = PillShape,
                                    color = ProfitGreenLight
                                ) {
                                    Text(
                                        text = "${unlockedTips.size} UNLOCKED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ProfitGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = OrangePrimary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.clickable { onNavigateTips() }
                            )
                        }
                    }
                }

                items(unlockedTips.take(2)) { tip ->
                    TipListingCard(
                        tip = tip,
                        onClick = { onNavigateTipDetail(tip.id) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }

            // Market Filter Chips
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Explore Segments",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(MarketSegment.values()) { segment ->
                            MarketChip(
                                text = segment.displayName,
                                isSelected = selectedSegment == segment,
                                onClick = { selectedSegment = segment }
                            )
                        }
                    }
                }
            }

            // Today's Live Active Tips Header
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Today's Active Tips",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "High conviction intraday & positional setups",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        ),
                        modifier = Modifier.clickable { onNavigateTips() }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tips Cards - Only live active setups
            if (liveActiveTips.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.TrendingUp,
                        title = "No Live Tips in this segment",
                        description = "Check back soon as analysts publish new live market breakouts."
                    )
                }
            } else {
                items(liveActiveTips.take(4)) { tip ->
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

            // Featured Top Analysts Section
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Top Verified Analysts",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Experienced Technical Analysts & Chartists",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(experts) { expert ->
                            ExpertCompactCard(
                                expert = expert,
                                onClick = { onNavigateExpertProfile(expert.id) },
                                onToggleFollow = { repository.toggleFollowExpert(expert.id) }
                            )
                        }
                    }
                }
            }

            // Statutory Disclaimer
            item {
                Spacer(modifier = Modifier.height(30.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Statutory Risk Disclaimer: Trading securities and derivatives involves substantial risk of financial loss. Signals are provided for informational and educational purposes. TradeNov does not guarantee profits.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }

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
fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(OrangeLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = OrangePrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun DashboardStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtext: String,
    badgeColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted, fontSize = 11.sp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
            Spacer(modifier = Modifier.height(6.dp))
            Surface(shape = PillShape, color = badgeColor) {
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.labelSmall.copy(color = textColor, fontWeight = FontWeight.Bold, fontSize = 10.sp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

