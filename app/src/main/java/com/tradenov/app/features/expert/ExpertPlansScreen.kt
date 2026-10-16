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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.SubscriptionPlan
import java.util.UUID

@Composable
fun ExpertPlansScreen(
    repository: TradeNovRepository,
    onNavigateBack: () -> Unit
) {
    val plans by repository.expertPlans.collectAsState()

    var showCreateModal by remember { mutableStateOf(false) }
    var editingPlan by remember { mutableStateOf<SubscriptionPlan?>(null) }

    // Modal Form State
    var planName by remember { mutableStateOf("") }
    var durationDays by remember { mutableStateOf(30) }
    var originalPrice by remember { mutableStateOf("2999") }
    var offerPrice by remember { mutableStateOf("1499") }
    var description by remember { mutableStateOf("") }
    var hasTelegram by remember { mutableStateOf(true) }
    var hasWhatsApp by remember { mutableStateOf(true) }

    val totalSubscribers = remember(plans) { plans.sumOf { it.subscribersCount } }
    val totalRevenue = remember(plans) { plans.sumOf { it.totalRevenue } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "VIP Subscription Studio",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Create & manage recurring pass tiers",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            editingPlan = null
                            planName = ""
                            durationDays = 30
                            originalPrice = "2999"
                            offerPrice = "1499"
                            description = "High conviction intraday & swing calls with disciplined risk management."
                            hasTelegram = true
                            hasWhatsApp = true
                            showCreateModal = true
                        },
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create Pass", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            contentPadding = PaddingValues(top = 14.dp, bottom = 40.dp)
        ) {
            // Overview Stats Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = OrangePrimary,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Pass Revenue", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f)))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "₹${String.format("%,.0f", totalRevenue)}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color.White)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Active Subscribers", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f)))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "$totalSubscribers Traders",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Subscription Passes (${plans.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "90% Net Payout",
                        style = MaterialTheme.typography.labelSmall.copy(color = ProfitGreen, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Plans List
            items(plans) { plan ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = plan.planName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${plan.durationDays} Days Pass • ${plan.includedMarkets.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            Switch(
                                checked = plan.isActive,
                                onCheckedChange = { repository.toggleExpertPlanStatus(plan.id) },
                                colors = SwitchDefaults.colors(checkedThumbColor = OrangePrimary, checkedTrackColor = OrangePrimary.copy(alpha = 0.5f))
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = plan.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "₹${plan.offerPrice.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = OrangePrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "₹${plan.originalPrice.toInt()}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        textDecoration = TextDecoration.LineThrough,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = PillShape,
                                    color = OrangeLight
                                ) {
                                    Text(
                                        text = "${plan.subscribersCount} Subscribed",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OrangePrimary)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        editingPlan = plan
                                        planName = plan.planName
                                        durationDays = plan.durationDays
                                        originalPrice = plan.originalPrice.toInt().toString()
                                        offerPrice = plan.offerPrice.toInt().toString()
                                        description = plan.description
                                        hasTelegram = plan.hasTelegramAccess
                                        hasWhatsApp = plan.hasWhatsAppAccess
                                        showCreateModal = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = { repository.deleteExpertPlan(plan.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = LossRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create / Edit Modal Dialog
    if (showCreateModal) {
        AlertDialog(
            onDismissRequest = { showCreateModal = false },
            title = {
                Text(
                    text = if (editingPlan != null) "Edit VIP Pass" else "Create New VIP Pass",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TradeNovTextField(
                        value = planName,
                        onValueChange = { planName = it },
                        label = "Pass Title",
                        placeholder = "e.g. BankNifty Expiry Alpha VIP"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Duration Tier", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(1 to "1 Day", 7 to "7 Days", 15 to "15 Days", 30 to "1 Month", 90 to "3 Months").forEach { (days, label) ->
                            item {
                                Surface(
                                    modifier = Modifier
                                        .clip(PillShape)
                                        .clickable { durationDays = days },
                                    shape = PillShape,
                                    color = if (durationDays == days) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (durationDays == days) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TradeNovTextField(
                            value = originalPrice,
                            onValueChange = { originalPrice = it },
                            label = "Regular (₹)",
                            placeholder = "2999",
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        TradeNovTextField(
                            value = offerPrice,
                            onValueChange = { offerPrice = it },
                            label = "Offer (₹)",
                            placeholder = "1499",
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    TradeNovTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Plan Rationale & Features",
                        placeholder = "Details on call frequency, risk mitigation, and target ratio.",
                        singleLine = false
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Include VIP Telegram Alerts", style = MaterialTheme.typography.bodySmall)
                        Checkbox(checked = hasTelegram, onCheckedChange = { hasTelegram = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Include WhatsApp VIP Community", style = MaterialTheme.typography.bodySmall)
                        Checkbox(checked = hasWhatsApp, onCheckedChange = { hasWhatsApp = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val oPrice = originalPrice.toDoubleOrNull() ?: 2999.0
                        val fPrice = offerPrice.toDoubleOrNull() ?: 1499.0
                        if (editingPlan != null) {
                            val updated = editingPlan!!.copy(
                                planName = planName.ifBlank { "VIP Pass" },
                                durationDays = durationDays,
                                originalPrice = oPrice,
                                offerPrice = fPrice,
                                description = description,
                                hasTelegramAccess = hasTelegram,
                                hasWhatsAppAccess = hasWhatsApp
                            )
                            repository.updateExpertPlan(updated)
                        } else {
                            val newPlan = SubscriptionPlan(
                                id = "plan_${UUID.randomUUID()}",
                                expertId = "exp_001",
                                expertName = "Kunal Sharma (Technical Analyst)",
                                planName = planName.ifBlank { "VIP Trading Pass" },
                                durationDays = durationDays,
                                originalPrice = oPrice,
                                offerPrice = fPrice,
                                description = description.ifBlank { "High-conviction calls curated for disciplined traders." },
                                includedMarkets = listOf("Options", "Stocks"),
                                hasTelegramAccess = hasTelegram,
                                hasWhatsAppAccess = hasWhatsApp,
                                isActive = true,
                                subscribersCount = 0,
                                totalRevenue = 0.0
                            )
                            repository.createExpertPlan(newPlan)
                        }
                        showCreateModal = false
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text(if (editingPlan != null) "Save Changes" else "Publish Pass", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
