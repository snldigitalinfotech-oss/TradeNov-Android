package com.tradenov.app.features.trader

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.SubscriptionPlan
import kotlinx.coroutines.launch

@Composable
fun SubscriptionsScreen(
    repository: TradeNovRepository,
    onNavigateWallet: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val experts by repository.experts.collectAsState()
    val wallet by repository.wallet.collectAsState()

    val allPlans = remember(experts) { experts.flatMap { it.plans } }
    val activeSubscribedPlans = remember(allPlans) { allPlans.filter { it.isSubscribed } }

    var isSubscribing by remember { mutableStateOf(false) }
    var subscribeError by remember { mutableStateOf<String?>(null) }
    var subscribedSuccessPlan by remember { mutableStateOf<SubscriptionPlan?>(null) }

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
                    text = "Expert Subscriptions",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Unlock all tips, daily strategies & VIP communication desks",
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
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
        ) {
            // Error banner
            if (subscribeError != null) {
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
                                text = subscribeError ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(color = LossRed, fontWeight = FontWeight.Medium),
                                modifier = Modifier.weight(1f)
                            )
                            if (subscribeError!!.contains("Insufficient", true)) {
                                TextButton(onClick = onNavigateWallet) {
                                    Text("Add Money", color = LossRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Active Subscriptions Section
            if (activeSubscribedPlans.isNotEmpty()) {
                item {
                    Text(
                        text = "Active VIP Subscriptions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                items(activeSubscribedPlans) { plan ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                        shape = CardShape,
                        color = OrangeLight,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(OrangePrimary)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(plan.planName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(plan.expertName, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                                Surface(shape = PillShape, color = OrangePrimary) {
                                    Text(
                                        text = "Expires in ${plan.expiresInDays ?: plan.durationDays} days",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 1-Click VIP Desk Access
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(plan.telegramInviteUrl))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    shape = PillShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = TelegramBlue)
                                ) {
                                    Text("Open Telegram VIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(plan.whatsappInviteUrl))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    shape = PillShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                                ) {
                                    Text("Open WhatsApp VIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Available Plans to Explore
            item {
                Text(
                    text = "Explore Expert Plans",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(allPlans.filter { !it.isSubscribed }) { plan ->
                TradeNovCard(modifier = Modifier.padding(bottom = 14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(plan.planName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(plan.expertName, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹${plan.offerPrice.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = OrangePrimary))
                            Text("/${plan.durationDays} Days", style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(plan.description, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))

                    Spacer(modifier = Modifier.height(14.dp))

                    TradeNovPrimaryButton(
                        text = "Subscribe for ₹${plan.offerPrice.toInt()}",
                        onClick = {
                            if (wallet.availableBalance < plan.offerPrice) {
                                subscribeError = "Insufficient wallet balance (₹${wallet.availableBalance.toInt()}). Please add money."
                            } else {
                                isSubscribing = true
                                subscribeError = null
                                coroutineScope.launch {
                                    val res = repository.subscribePlan(plan.id)
                                    isSubscribing = false
                                    if (res.isSuccess) {
                                        subscribedSuccessPlan = plan
                                    } else {
                                        subscribeError = res.exceptionOrNull()?.message
                                    }
                                }
                            }
                        },
                        isLoading = isSubscribing
                    )
                }
            }
        }
    }

    // Success Modal
    if (subscribedSuccessPlan != null) {
        val p = subscribedSuccessPlan!!
        AlertDialog(
            onDismissRequest = { subscribedSuccessPlan = null },
            confirmButton = {
                Button(
                    onClick = { subscribedSuccessPlan = null },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text("Subscription Activated! 🎉", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("All tips from ${p.expertName} are now unlocked. Join your VIP broadcast channels below:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(p.telegramInviteUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = TelegramBlue)
                    ) {
                        Text("Join Telegram VIP Channel", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(p.whatsappInviteUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                    ) {
                        Text("Join WhatsApp VIP Group", fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}
