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

@Composable
fun TraderHomeScreen(
    repository: TradeNovRepository,
    onNavigateTips: () -> Unit,
    onNavigateTipDetail: (String) -> Unit,
    onNavigateWallet: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateExpertProfile: (String) -> Unit,
    onNavigateWatchlist: () -> Unit
) {
    val user by repository.sessionManager.currentUser.collectAsState()
    val marketQuotes by repository.marketQuotes.collectAsState()
    val tips by repository.tips.collectAsState()
    val experts by repository.experts.collectAsState()
    val notifications by repository.notifications.collectAsState()
    val unreadNotifsCount = notifications.count { !it.isRead }

    var selectedSegment by remember { mutableStateOf(MarketSegment.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredTips = remember(tips, selectedSegment, searchQuery) {
        tips.filter {
            (selectedSegment == MarketSegment.ALL || it.marketSegment == selectedSegment) &&
            (searchQuery.isBlank() || it.symbol.contains(searchQuery, true) || it.expertName.contains(searchQuery, true))
        }
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
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (user?.name?.take(1) ?: "T").uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OrangePrimary
                                        )
                                    )
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

            // Live Market Ticker Strip
            item {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(marketQuotes) { quote ->
                        Surface(
                            shape = CardShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.width(138.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = quote.name,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${quote.price.toInt()}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${if (quote.isPositive) "+" else ""}${quote.changePercent}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (quote.isPositive) ProfitGreen else LossRed
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Quick Actions Strip (4 cards matching reference UI)
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionItem(
                        icon = Icons.Default.AddCard,
                        label = "Add Money",
                        onClick = onNavigateWallet
                    )
                    QuickActionItem(
                        icon = Icons.Default.TrendingUp,
                        label = "Browse Tips",
                        onClick = onNavigateTips
                    )
                    QuickActionItem(
                        icon = Icons.Default.Stars,
                        label = "VIP Plans",
                        onClick = onNavigateTips
                    )
                    QuickActionItem(
                        icon = Icons.Default.BookmarkBorder,
                        label = "Watchlist",
                        onClick = onNavigateWatchlist
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

            // Tips Cards
            if (filteredTips.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.TrendingUp,
                        title = "No Tips in this segment",
                        description = "Check back soon as SEBI analysts publish new market breakouts."
                    )
                }
            } else {
                items(filteredTips.take(4)) { tip ->
                    TipListingCard(
                        tip = tip,
                        onClick = { onNavigateTipDetail(tip.id) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }

            // Featured SEBI Experts Section
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
                                text = "SEBI Registered Research Analysts & CMTs",
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
fun TipListingCard(
    tip: Tip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TradeNovCard(
        modifier = modifier.clickable { onClick() },
        elevation = 2.dp
    ) {
        // Header Row: Symbol, Market Tag, Status Badge
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
                        .background(if (tip.action == TradeAction.BUY) ProfitGreenLight else LossRedLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (tip.action == TradeAction.BUY) "B" else "S",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (tip.action == TradeAction.BUY) ProfitGreen else LossRed
                        )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = tip.symbol,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${tip.marketSegment.displayName} • ${tip.tradeType.name}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(status = tip.status.name)
                if (tip.status == TipStatus.TARGET_HIT || tip.liveProfitLoss != 0.0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tip.displayProfitLossText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (tip.profitLossRupees >= 0) ProfitGreen else LossRed,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Price & Targets Info (Locked or Unlocked)
        if (tip.isUnlocked) {
            // Unlocked details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(InputShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Entry Price", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                    Text("₹${tip.capturedEntryPrice}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                }
                Column {
                    Text("Target 1", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                    Text("₹${tip.target1}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ProfitGreen))
                }
                Column {
                    Text("Stop Loss", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                    Text("₹${tip.stopLoss}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LossRed))
                }
            }
        } else {
            // Locked details preview (Blurred feel matching specification)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(InputShape)
                    .background(OrangeLight.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Entry & Targets Locked",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    )
                }

                Surface(
                    shape = PillShape,
                    color = OrangePrimary
                ) {
                    Text(
                        text = "Unlock • ₹${tip.basePrice.toInt()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Footer: Expert name, rating, published time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = tip.expertName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = tip.createdAt,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = LightTextMuted,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun ExpertCompactCard(
    expert: Expert,
    onClick: () -> Unit,
    onToggleFollow: () -> Unit
) {
    Surface(
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(OrangeLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = expert.name.take(1),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = expert.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${expert.experienceYears}+ Yrs Exp",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Win Rate", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                    Text("${expert.winRate}%", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ProfitGreen))
                }
                Column {
                    Text("Total Tips", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                    Text("${expert.totalTips}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                }
                Column {
                    Text("Rating", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                    Text("★ ${expert.rating}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = OrangePrimary))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TradeNovPrimaryButton(
                text = if (expert.isFollowed) "Following" else "Follow",
                onClick = onToggleFollow,
                modifier = Modifier.height(38.dp),
                backgroundColor = if (expert.isFollowed) MaterialTheme.colorScheme.surfaceVariant else OrangePrimary,
                contentColor = if (expert.isFollowed) MaterialTheme.colorScheme.onSurface else Color.White
            )
        }
    }
}
