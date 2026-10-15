package com.tradenov.app.features.trader

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.*
import kotlinx.coroutines.launch

@Composable
fun TipDetailScreen(
    tipId: String,
    repository: TradeNovRepository,
    onBack: () -> Unit,
    onNavigateWallet: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tips by repository.tips.collectAsState()
    val wallet by repository.wallet.collectAsState()
    val watchlistSymbols by repository.watchlistSymbols.collectAsState()

    val tip = tips.find { it.id == tipId } ?: tips.firstOrNull()
    if (tip == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tip not found")
        }
        return
    }

    val isWatchlisted = watchlistSymbols.contains(tip.symbol)
    var isUnlocking by remember { mutableStateOf(false) }
    var showConfirmPurchase by remember { mutableStateOf(false) }
    var unlockedSuccessTip by remember { mutableStateOf<Tip?>(null) }
    var unlockError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = tip.symbol,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Row {
                    IconButton(onClick = { repository.toggleWatchlist(tip.symbol) }) {
                        Icon(
                            imageVector = if (isWatchlisted) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (isWatchlisted) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Check out ${tip.symbol} trading setup on TradeNov: https://tradenov.com/signals/${tip.id}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Tip"))
                    }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }
                }
            }
        },
        bottomBar = {
            if (!tip.isUnlocked) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Tip Unlock Fee",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "₹${tip.basePrice.toInt()}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OrangePrimary
                                )
                            )
                        }

                        Button(
                            onClick = {
                                showConfirmPurchase = true
                            },
                            modifier = Modifier.width(180.dp).height(50.dp),
                            shape = ButtonShape,
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            enabled = !isUnlocking
                        ) {
                            if (isUnlocking) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Unlock Now", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
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
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // Error banner
            if (unlockError != null) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = ButtonShape,
                        color = LossRedLight
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = LossRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = unlockError ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(color = LossRed, fontWeight = FontWeight.Medium),
                                modifier = Modifier.weight(1f)
                            )
                            if (unlockError!!.contains("Insufficient", true)) {
                                TextButton(onClick = onNavigateWallet) {
                                    Text("Add Money", color = LossRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Hero Card: Live Market Price, Signal Info & Profit/Loss
            item {
                TradeNovCard(elevation = 2.dp) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Badges Row & Market Segment
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (tip.isUnlocked) {
                                    Surface(
                                        shape = PillShape,
                                        color = if (tip.action == TradeAction.BUY) ProfitGreen else LossRed
                                    ) {
                                        Text(
                                            text = tip.action.name,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                } else {
                                    Surface(
                                        shape = PillShape,
                                        color = OrangeLight,
                                        border = CardDefaults.outlinedCardBorder()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = OrangePrimary,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "ACTION LOCKED",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = OrangePrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(status = if (tip.isUnlocked) tip.status.name else "PREMIUM")
                            }

                            Text(
                                text = "${tip.marketSegment.displayName} • ${tip.tradeType.name}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Full-width Signal Name
                        Text(
                            text = tip.signalName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Dedicated Live CMP & Return Metric Strip
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = InputShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Current Market Price",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = LightTextMuted,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₹${tip.currentMarketPrice}",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }

                                if (tip.isUnlocked && tip.capturedEntryPrice > 0) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Return / P&L",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = LightTextMuted,
                                                fontSize = 11.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Surface(
                                            shape = PillShape,
                                            color = if (tip.profitLossRupees >= 0) ProfitGreenLight else LossRedLight
                                        ) {
                                            Text(
                                                text = tip.displayProfitLossText,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (tip.profitLossRupees >= 0) ProfitGreen else LossRed
                                                ),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Target Brackets Section (Unlocked vs Locked)
            item {
                Text("Trade Targets & Limits", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(10.dp))

                if (tip.isUnlocked) {
                    TradeNovCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Entry Range", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted))
                                Text("₹${tip.entryRangeMin} - ₹${tip.entryRangeMax}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Captured Entry", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted))
                                Text("₹${tip.capturedEntryPrice}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Target 1", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 11.sp))
                                Text("₹${tip.target1}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ProfitGreen))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Target 2", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 11.sp))
                                Text("₹${tip.target2}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ProfitGreen))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Target 3", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 11.sp))
                                Text("₹${tip.target3}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ProfitGreen))
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text("Stop Loss", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 11.sp))
                                Text("₹${tip.stopLoss}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LossRed))
                            }
                        }
                    }
                } else {
                    // Locked Card with Lock Illustration
                    TradeNovCard(borderColor = OrangePrimary.copy(alpha = 0.5f)) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(56.dp).clip(CircleShape).background(OrangeLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Trade Limits Are Locked", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Unlock to reveal entry bracket, target 1/2/3, trailing stop loss & live broadcast notes.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Expert Analysis & Technical Notes (Revealed when unlocked)
            if (tip.isUnlocked && !tip.notes.isNullOrBlank()) {
                item {
                    TradeNovCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Expert Rationale & Technical Analysis", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = tip.notes ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Social VIP Broadcast Desk Card
            item {
                Surface(
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Social VIP Broadcast Channels", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "All live follow-ups for this tip are automatically dispatched to official VIP Telegram & WhatsApp groups without fail.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/tradenov"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = PillShape,
                                colors = ButtonDefaults.buttonColors(containerColor = TelegramBlue)
                            ) {
                                Text("Telegram VIP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.whatsapp.com/TradeNovVIPAlerts"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = PillShape,
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                            ) {
                                Text("WhatsApp VIP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Timeline / Execution Roadmap (Matching the bus-stop vertical timeline in reference image)
            item {
                Text("Performance & Follow-Up Timeline", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (tip.timeline.isEmpty()) {
                item {
                    Text(
                        "Tip published. Waiting for market entry confirmation.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            } else {
                itemsIndexed(tip.timeline) { index, step ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Vertical Step Indicator (Orange dot & line)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(28.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(OrangePrimary)
                            )
                            if (index < tip.timeline.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(50.dp)
                                        .background(OrangePrimary.copy(alpha = 0.5f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Step Content
                        Column(modifier = Modifier.weight(1f).padding(bottom = 16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(step.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Text(step.timestamp, style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted))
                            }
                            if (step.notes != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    step.notes,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Purchase Confirmation Dialog
    if (showConfirmPurchase) {
        TipPurchaseConfirmationDialog(
            tip = tip,
            walletBalance = wallet.availableBalance,
            onDismiss = { showConfirmPurchase = false },
            onConfirmPay = { couponCode, discountPercent ->
                showConfirmPurchase = false
                isUnlocking = true
                unlockError = null
                coroutineScope.launch {
                    val res = repository.unlockTip(tip.id, couponCode, discountPercent)
                    isUnlocking = false
                    if (res.isSuccess) {
                        unlockedSuccessTip = tip.copy(isUnlocked = true)
                    } else {
                        unlockError = res.exceptionOrNull()?.message ?: "Transaction failed. Please check wallet balance."
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
                unlockedSuccessTip = null
            }
        )
    }
}
