package com.tradenov.app.features.expert

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.*
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.Date

@Composable
fun AddTipScreen(
    repository: TradeNovRepository,
    onTipPublished: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedSegment by remember { mutableStateOf(MarketSegment.OPTIONS_INDEX) }
    var action by remember { mutableStateOf(TradeAction.BUY) }
    var symbol by remember { mutableStateOf("NIFTY 25000 CE") }
    var entryMin by remember { mutableStateOf("120") }
    var entryMax by remember { mutableStateOf("128") }
    var target1 by remember { mutableStateOf("150") }
    var target2 by remember { mutableStateOf("180") }
    var target3 by remember { mutableStateOf("210") }
    var stopLoss by remember { mutableStateOf("95") }
    var basePrice by remember { mutableStateOf("99") }
    var notes by remember { mutableStateOf("Strict intraday momentum trade. Exit if 95 breaks.") }
    var broadcastTelegram by remember { mutableStateOf(true) }
    var broadcastWhatsApp by remember { mutableStateOf(true) }

    var isPublishing by remember { mutableStateOf(false) }
    var successMsg by remember { mutableStateOf<String?>(null) }

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
                    text = "Publish Trading Signal",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Broadcast deterministic setup to subscribers & VIP channels",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
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
            // One-Click Quick Templates (Section 19)
            item {
                Text("Quick Templates", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Surface(
                            modifier = Modifier.clip(PillShape).clickable {
                                selectedSegment = MarketSegment.OPTIONS_INDEX
                                action = TradeAction.BUY
                                symbol = "NIFTY 24800 CE"
                                entryMin = "140"; entryMax = "148"; target1 = "175"; target2 = "210"; stopLoss = "118"
                            },
                            shape = PillShape,
                            color = OrangeLight,
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Text("⚡ Options Buy", modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OrangePrimary))
                        }
                    }
                    item {
                        Surface(
                            modifier = Modifier.clip(PillShape).clickable {
                                selectedSegment = MarketSegment.STOCKS
                                action = TradeAction.BUY
                                symbol = "TATA MOTORS"
                                entryMin = "980"; entryMax = "990"; target1 = "1025"; target2 = "1060"; stopLoss = "955"
                            },
                            shape = PillShape,
                            color = OrangeLight,
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Text("📈 Swing Cash Buy", modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OrangePrimary))
                        }
                    }
                    item {
                        Surface(
                            modifier = Modifier.clip(PillShape).clickable {
                                selectedSegment = MarketSegment.COMMODITIES
                                action = TradeAction.BUY
                                symbol = "GOLD MINI"
                                entryMin = "75800"; entryMax = "76000"; target1 = "76800"; target2 = "77500"; stopLoss = "75100"
                            },
                            shape = PillShape,
                            color = OrangeLight,
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Text("🥇 Commodity Buy", modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OrangePrimary))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Market Category Chips
            item {
                Text("Market Segment", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(MarketSegment.values().filter { it != MarketSegment.ALL }) { seg ->
                        MarketChip(
                            text = seg.displayName,
                            isSelected = selectedSegment == seg,
                            onClick = { selectedSegment = seg }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // BUY / SELL Action Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { action = TradeAction.BUY },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (action == TradeAction.BUY) ProfitGreen else MaterialTheme.colorScheme.surface,
                            contentColor = if (action == TradeAction.BUY) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        border = if (action == TradeAction.BUY) null else CardDefaults.outlinedCardBorder()
                    ) {
                        Text("BUY / LONG", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { action = TradeAction.SELL },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (action == TradeAction.SELL) LossRed else MaterialTheme.colorScheme.surface,
                            contentColor = if (action == TradeAction.SELL) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        border = if (action == TradeAction.SELL) null else CardDefaults.outlinedCardBorder()
                    ) {
                        Text("SELL / SHORT", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Asset Symbol Field
            item {
                TradeNovTextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = "Asset Symbol",
                    placeholder = "e.g. NIFTY 24800 CE, RELIANCE, GOLD"
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Entry Brackets Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TradeNovTextField(
                        value = entryMin,
                        onValueChange = { entryMin = it },
                        label = "Entry Min (₹)",
                        placeholder = "120",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    TradeNovTextField(
                        value = entryMax,
                        onValueChange = { entryMax = it },
                        label = "Entry Max (₹)",
                        placeholder = "128",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Targets & Stop Loss
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TradeNovTextField(
                        value = target1,
                        onValueChange = { target1 = it },
                        label = "Target 1 (₹)",
                        placeholder = "150",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    TradeNovTextField(
                        value = target2,
                        onValueChange = { target2 = it },
                        label = "Target 2 (₹)",
                        placeholder = "180",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TradeNovTextField(
                        value = target3,
                        onValueChange = { target3 = it },
                        label = "Target 3 / Final (₹)",
                        placeholder = "210",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    TradeNovTextField(
                        value = stopLoss,
                        onValueChange = { stopLoss = it },
                        label = "Hard Stop Loss (₹)",
                        placeholder = "95",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

                TradeNovTextField(
                    value = basePrice,
                    onValueChange = { basePrice = it },
                    label = "Single Unlock Price (₹)",
                    placeholder = "99 (Free if 0)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Real-Time Live Institutional Risk-to-Reward (R:R) Matrix Card
                val entryMid = remember(entryMin, entryMax) {
                    val min = entryMin.toDoubleOrNull() ?: 0.0
                    val max = entryMax.toDoubleOrNull() ?: 0.0
                    if (min > 0 && max > 0) (min + max) / 2.0 else min
                }
                val slVal = remember(stopLoss) { stopLoss.toDoubleOrNull() ?: 0.0 }
                val t1Val = remember(target1) { target1.toDoubleOrNull() ?: 0.0 }
                val t2Val = remember(target2) { target2.toDoubleOrNull() ?: 0.0 }
                val t3Val = remember(target3) { target3.toDoubleOrNull() ?: 0.0 }

                val riskPoints = remember(entryMid, slVal, action) {
                    if (entryMid > 0 && slVal > 0) {
                        if (action == TradeAction.BUY) (entryMid - slVal).coerceAtLeast(0.0)
                        else (slVal - entryMid).coerceAtLeast(0.0)
                    } else 0.0
                }
                val t1Reward = remember(entryMid, t1Val, action) {
                    if (entryMid > 0 && t1Val > 0) {
                        if (action == TradeAction.BUY) (t1Val - entryMid).coerceAtLeast(0.0)
                        else (entryMid - t1Val).coerceAtLeast(0.0)
                    } else 0.0
                }
                val t2Reward = remember(entryMid, t2Val, action) {
                    if (entryMid > 0 && t2Val > 0) {
                        if (action == TradeAction.BUY) (t2Val - entryMid).coerceAtLeast(0.0)
                        else (entryMid - t2Val).coerceAtLeast(0.0)
                    } else 0.0
                }

                val rr1 = if (riskPoints > 0) t1Reward / riskPoints else 0.0
                val rr2 = if (riskPoints > 0) t2Reward / riskPoints else 0.0

                Surface(
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Calculate, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Live Risk : Reward Matrix", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            }

                            Surface(
                                shape = PillShape,
                                color = if (rr1 >= 2.0) ProfitLight else OrangeLight
                            ) {
                                Text(
                                    text = if (rr1 >= 2.0) "Optimal R:R (> 1:2)" else "Calculated",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = if (rr1 >= 2.0) ProfitGreen else OrangePrimary
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
                                Text("Max Risk / Unit", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                Text(
                                    text = if (riskPoints > 0) "₹${String.format("%.1f", riskPoints)}" else "--",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LossRed)
                                )
                            }
                            Column {
                                Text("Target 1 Ratio", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                Text(
                                    text = if (rr1 > 0) "1 : ${String.format("%.1f", rr1)}" else "--",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ProfitGreen)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Target 2 Ratio", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                Text(
                                    text = if (rr2 > 0) "1 : ${String.format("%.1f", rr2)}" else "--",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = OrangePrimary)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                TradeNovTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Analyst Broadcast Notes",
                    placeholder = "Trading commentary / trailing instructions",
                    singleLine = false
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Social Channels Broadcast Sync Toggles
            item {
                Surface(
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = OrangePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Real-Time Social Broadcast Desk", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Telegram VIP Desk (@TradeNov)", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = broadcastTelegram,
                                onCheckedChange = { broadcastTelegram = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = TelegramBlue, checkedTrackColor = TelegramBlue.copy(alpha = 0.5f))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("WhatsApp VIP Community", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = broadcastWhatsApp,
                                onCheckedChange = { broadcastWhatsApp = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppGreen, checkedTrackColor = WhatsAppGreen.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Technical Chart Image Attachment Section
            item {
                var isChartAttached by remember { mutableStateOf(true) }

                Surface(
                    shape = CardShape,
                    color = if (isChartAttached) OrangeLight else MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isChartAttached) OrangePrimary else MaterialTheme.colorScheme.outline
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .clickable { isChartAttached = !isChartAttached }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isChartAttached) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isChartAttached) "Technical Chart Attached (analysis_chart.png)" else "Attach Technical Chart Image",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isChartAttached) "Tap to change attached screenshot" else "Upload TradingView candlestick chart or breakout pattern",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Publish Button
            item {
                TradeNovPrimaryButton(
                    text = "Broadcast & Publish Signal",
                    onClick = {
                        val eMin = entryMin.toDoubleOrNull() ?: 100.0
                        val eMax = entryMax.toDoubleOrNull() ?: 105.0
                        val t1 = target1.toDoubleOrNull() ?: 120.0
                        val t2 = target2.toDoubleOrNull() ?: 140.0
                        val t3 = target3.toDoubleOrNull() ?: (t2 * 1.15)
                        val sl = stopLoss.toDoubleOrNull() ?: 85.0
                        val pr = basePrice.toDoubleOrNull() ?: 99.0

                        val newTip = Tip(
                            id = "tip_" + UUID.randomUUID().toString().take(8),
                            symbol = symbol.uppercase(),
                            signalName = "${symbol.uppercase()} ${action.name} Setup",
                            marketSegment = selectedSegment,
                            action = action,
                            tradeType = TradeType.INTRADAY,
                            entryRangeMin = eMin,
                            entryRangeMax = eMax,
                            capturedEntryPrice = (eMin + eMax) / 2,
                            currentMarketPrice = (eMin + eMax) / 2,
                            target1 = t1,
                            target2 = t2,
                            target3 = t3,
                            stopLoss = sl,
                            basePrice = pr,
                            notes = notes,
                            status = TipStatus.RUNNING,
                            isUnlocked = true,
                            expertId = "exp_001",
                            expertName = "Kunal Sharma (Technical Analyst)",
                            expertRating = 4.9,
                            createdAt = repository.formatDateTimeWithSeconds(Date()),
                            timeline = listOf(
                                TipTimelineStep("Tip Published", (eMin + eMax) / 2, repository.formatDateTimeWithSeconds(Date()), notes)
                            )
                        )

                        isPublishing = true
                        coroutineScope.launch {
                            repository.publishTip(newTip)
                            isPublishing = false
                            successMsg = "Signal successfully broadcast to all active subscribers & Telegram/WhatsApp channels!"
                        }
                    },
                    isLoading = isPublishing
                )
            }
        }
    }

    if (successMsg != null) {
        AlertDialog(
            onDismissRequest = {
                successMsg = null
                onTipPublished()
            },
            confirmButton = {
                Button(
                    onClick = {
                        successMsg = null
                        onTipPublished()
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Go to Manage Tips", fontWeight = FontWeight.Bold)
                }
            },
            title = { Text("Signal Published! 🚀", fontWeight = FontWeight.Bold) },
            text = { Text(successMsg ?: "") }
        )
    }
}
