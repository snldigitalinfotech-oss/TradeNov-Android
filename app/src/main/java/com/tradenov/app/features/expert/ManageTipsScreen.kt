package com.tradenov.app.features.expert

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
import com.tradenov.app.core.model.Tip
import com.tradenov.app.core.model.TipStatus
import kotlinx.coroutines.launch

@Composable
fun ManageTipsScreen(
    repository: TradeNovRepository,
    onNavigateAddTip: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val tips by repository.tips.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Running, 1: Closed
    val runningTips = remember(tips) { tips.filter { it.status == TipStatus.RUNNING } }
    val closedTips = remember(tips) { tips.filter { it.status != TipStatus.RUNNING } }

    var activeTipForAction by remember { mutableStateOf<Tip?>(null) }
    var selectedActionType by remember { mutableStateOf<String?>(null) }
    var actionNote by remember { mutableStateOf("") }
    var isSubmittingAction by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Manage Signals",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onNavigateAddTip) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Tip", tint = OrangePrimary, modifier = Modifier.size(28.dp))
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = OrangePrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Running (${runningTips.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Closed / Targets (${closedTips.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }
    ) { padding ->
        val currentList = if (selectedTab == 0) runningTips else closedTips
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 40.dp)
        ) {
            if (currentList.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.FilterListOff,
                        title = if (selectedTab == 0) "No Active Running Tips" else "No Closed Tips",
                        description = "Published signals will appear here with live follow-up buttons."
                    )
                }
            } else {
                items(currentList) { tip ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = CardShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tip.symbol, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Text("${tip.marketSegment.displayName} • Entry: ₹${tip.capturedEntryPrice}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                                StatusBadge(status = tip.status.name)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("T1: ₹${tip.target1} | T2: ₹${tip.target2}", style = MaterialTheme.typography.bodySmall.copy(color = ProfitGreen, fontWeight = FontWeight.SemiBold))
                                Text("SL: ₹${tip.stopLoss}", style = MaterialTheme.typography.bodySmall.copy(color = LossRed, fontWeight = FontWeight.SemiBold))
                            }

                            if (tip.status == TipStatus.RUNNING) {
                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Fast Action Buttons Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            activeTipForAction = tip
                                            selectedActionType = "TARGET_1_HIT"
                                            actionNote = "Target 1 achieved! Book 50% profits & trail SL to cost."
                                        },
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        shape = PillShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen)
                                    ) {
                                        Text("Target 1", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            activeTipForAction = tip
                                            selectedActionType = "PARTIAL_PROFIT"
                                            actionNote = "Book partial profits. Trail SL."
                                        },
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        shape = PillShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                                    ) {
                                        Text("Trail SL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            activeTipForAction = tip
                                            selectedActionType = "STOP_LOSS_HIT"
                                            actionNote = "Strict SL triggered. Exit trade."
                                        },
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        shape = PillShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = LossRed)
                                    ) {
                                        Text("SL Hit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            activeTipForAction = tip
                                            selectedActionType = "CLOSE"
                                            actionNote = "Trade closed at market."
                                        },
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        shape = PillShape
                                    ) {
                                        Text("Close", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Follow-up Confirmation Dialog with Telegram & WhatsApp Sync Notice
    if (activeTipForAction != null && selectedActionType != null) {
        val t = activeTipForAction!!
        val actionLabel = selectedActionType!!.replace("_", " ")

        AlertDialog(
            onDismissRequest = {
                activeTipForAction = null
                selectedActionType = null
            },
            confirmButton = {
                Button(
                    onClick = {
                        isSubmittingAction = true
                        coroutineScope.launch {
                            repository.updateTipAction(t.id, selectedActionType!!, actionNote)
                            isSubmittingAction = false
                            activeTipForAction = null
                            selectedActionType = null
                        }
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    enabled = !isSubmittingAction
                ) {
                    if (isSubmittingAction) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                    } else {
                        Text("Confirm & Broadcast", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    activeTipForAction = null
                    selectedActionType = null
                }) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Confirm $actionLabel for ${t.symbol}", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "This will immediately dispatch a high-priority push notification and broadcast to all VIP Telegram and WhatsApp channels.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TradeNovTextField(
                        value = actionNote,
                        onValueChange = { actionNote = it },
                        label = "Broadcast Note to Traders",
                        placeholder = "e.g. Target reached at 162. Trail SL."
                    )
                }
            }
        )
    }
}
