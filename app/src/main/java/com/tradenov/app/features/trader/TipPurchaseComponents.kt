package com.tradenov.app.features.trader

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.Expert
import com.tradenov.app.core.model.SubscriptionPlan
import com.tradenov.app.core.model.Tip
import com.tradenov.app.core.model.TipStatus
import com.tradenov.app.core.model.TradeAction

@Composable
fun TipPurchaseConfirmationDialog(
    tip: Tip,
    walletBalance: Double,
    onDismiss: () -> Unit,
    onConfirmPay: (couponCode: String?, discountPercent: Double) -> Unit,
    onNavigateWallet: () -> Unit
) {
    var couponInput by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf<String?>(null) }
    var discountPercent by remember { mutableStateOf(0.0) }
    var couponMessage by remember { mutableStateOf<String?>(null) }
    var isCouponSuccess by remember { mutableStateOf(false) }

    val rawPrice = tip.basePrice
    val discountAmount = if (discountPercent > 0) rawPrice * (discountPercent / 100.0) else 0.0
    val finalPayable = Math.max(0.0, rawPrice - discountAmount)
    val hasSufficientBalance = walletBalance >= finalPayable

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Unlock Trading Setup",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = LightTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tip Summary Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.background,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = tip.symbol,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = "${tip.marketSegment.displayName} • By ${tip.expertName}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Surface(
                            shape = PillShape,
                            color = OrangeLight
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
                                    text = "PREMIUM",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = OrangePrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Coupon Code Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = couponInput,
                        onValueChange = { couponInput = it.uppercase() },
                        placeholder = { Text("Coupon (e.g. VIP50)", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Button(
                        onClick = {
                            val code = couponInput.trim()
                            if (code == "VIP50" || code == "OFFER50" || code == "SAVE50") {
                                appliedCoupon = code
                                discountPercent = 50.0
                                isCouponSuccess = true
                                couponMessage = "50% Discount Applied!"
                            } else if (code == "FIRST20" || code == "PRO20") {
                                appliedCoupon = code
                                discountPercent = 20.0
                                isCouponSuccess = true
                                couponMessage = "20% Discount Applied!"
                            } else if (code.isNotEmpty()) {
                                isCouponSuccess = false
                                couponMessage = "Invalid coupon code"
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("Apply", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                if (couponMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = couponMessage!!,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isCouponSuccess) ProfitGreen else LossRed,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price Breakdown Strip
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Base Setup Fee", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text("₹${rawPrice.toInt()}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                        }

                        if (discountPercent > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Coupon Discount ($appliedCoupon)", style = MaterialTheme.typography.bodySmall.copy(color = ProfitGreen))
                                Text("-₹${discountAmount.toInt()}", style = MaterialTheme.typography.bodySmall.copy(color = ProfitGreen, fontWeight = FontWeight.Bold))
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Payable", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = "₹${finalPayable.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Wallet Balance Indicator
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasSufficientBalance) ProfitGreenLight else LossRedLight
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = if (hasSufficientBalance) ProfitGreen else LossRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Wallet: ₹${walletBalance.toInt()}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasSufficientBalance) ProfitGreen else LossRed
                                )
                            )
                        }

                        if (!hasSufficientBalance) {
                            Text(
                                text = "Add ₹${(finalPayable - walletBalance).toInt()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OrangePrimary
                                ),
                                modifier = Modifier.clickable {
                                    onDismiss()
                                    onNavigateWallet()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                if (hasSufficientBalance) {
                    TradeNovPrimaryButton(
                        text = "Confirm & Pay ₹${finalPayable.toInt()}",
                        onClick = {
                            onConfirmPay(appliedCoupon, discountPercent)
                        }
                    )
                } else {
                    TradeNovPrimaryButton(
                        text = "Add Money to Wallet",
                        onClick = {
                            onDismiss()
                            onNavigateWallet()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun VipOnboardingSuccessDialog(
    tip: Tip,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(ProfitGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ProfitGreen,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Setup Unlocked Successfully!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "You now have 100% full unmasked access to ${tip.symbol} entry, stop loss, and targets. Join our VIP channels for instant live updates:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // VIP Community Join Buttons
                // 1. Telegram VIP Channel
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ButtonShape)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/tradenov"))
                            context.startActivity(intent)
                        },
                    shape = ButtonShape,
                    color = Color(0xFF229ED9)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Join VIP Telegram Channel",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. WhatsApp VIP Group
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ButtonShape)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.whatsapp.com/TradeNovVIPAlerts"))
                            context.startActivity(intent)
                        },
                    shape = ButtonShape,
                    color = Color(0xFF25D366)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Join WhatsApp VIP Group",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Connect @tradenovbot
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/tradenovbot"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = ButtonShape
                ) {
                    Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Connect @tradenovbot Alerts", color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // View Unlocked Setup button
                TradeNovSecondaryButton(
                    text = "View Setup & Analysis",
                    onClick = onDismiss
                )
            }
        }
    )
}

@Composable
fun PlanPurchaseConfirmationDialog(
    plan: SubscriptionPlan,
    walletBalance: Double,
    onDismiss: () -> Unit,
    onConfirmPay: (couponCode: String?, discountPercent: Double) -> Unit,
    onNavigateWallet: () -> Unit
) {
    var couponInput by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf<String?>(null) }
    var discountPercent by remember { mutableStateOf(0.0) }
    var couponMessage by remember { mutableStateOf<String?>(null) }
    var isCouponSuccess by remember { mutableStateOf(false) }

    val rawPrice = plan.offerPrice
    val discountAmount = if (discountPercent > 0) rawPrice * (discountPercent / 100.0) else 0.0
    val finalPayable = Math.max(0.0, rawPrice - discountAmount)
    val hasSufficientBalance = walletBalance >= finalPayable

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Activate VIP Subscription",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = LightTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Plan Summary Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.background,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = plan.planName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = "${plan.expertName} • ${plan.durationDays} Days VIP Access",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Surface(
                            shape = PillShape,
                            color = OrangeLight
                        ) {
                            Text(
                                text = "VIP PASS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OrangePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Coupon Code Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = couponInput,
                        onValueChange = { couponInput = it.uppercase() },
                        placeholder = { Text("Coupon (e.g. VIP50)", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Button(
                        onClick = {
                            val code = couponInput.trim()
                            if (code == "VIP50" || code == "OFFER50" || code == "SAVE50") {
                                appliedCoupon = code
                                discountPercent = 50.0
                                isCouponSuccess = true
                                couponMessage = "50% Discount Applied!"
                            } else if (code == "FIRST20" || code == "PRO20") {
                                appliedCoupon = code
                                discountPercent = 20.0
                                isCouponSuccess = true
                                couponMessage = "20% Discount Applied!"
                            } else if (code.isNotEmpty()) {
                                isCouponSuccess = false
                                couponMessage = "Invalid coupon code"
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("Apply", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                if (couponMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = couponMessage!!,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isCouponSuccess) ProfitGreen else LossRed,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price Breakdown Strip
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Standard Plan Price", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text("₹${rawPrice.toInt()}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                        }

                        if (discountPercent > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Coupon Discount ($appliedCoupon)", style = MaterialTheme.typography.bodySmall.copy(color = ProfitGreen))
                                Text("-₹${discountAmount.toInt()}", style = MaterialTheme.typography.bodySmall.copy(color = ProfitGreen, fontWeight = FontWeight.Bold))
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Payable", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = "₹${finalPayable.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Wallet Balance Indicator
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasSufficientBalance) ProfitGreenLight else LossRedLight
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = if (hasSufficientBalance) ProfitGreen else LossRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Wallet: ₹${walletBalance.toInt()}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasSufficientBalance) ProfitGreen else LossRed
                                )
                            )
                        }

                        if (!hasSufficientBalance) {
                            Text(
                                text = "Add ₹${(finalPayable - walletBalance).toInt()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OrangePrimary
                                ),
                                modifier = Modifier.clickable {
                                    onDismiss()
                                    onNavigateWallet()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                if (hasSufficientBalance) {
                    TradeNovPrimaryButton(
                        text = "Confirm & Subscribe ₹${finalPayable.toInt()}",
                        onClick = {
                            onConfirmPay(appliedCoupon, discountPercent)
                        }
                    )
                } else {
                    TradeNovPrimaryButton(
                        text = "Add Money to Wallet",
                        onClick = {
                            onDismiss()
                            onNavigateWallet()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun SubscriptionSuccessDialog(
    plan: SubscriptionPlan,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(ProfitGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ProfitGreen,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Subscription Activated! 🎉",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "You now have full VIP access to all ${plan.expertName} signals for ${plan.durationDays} days. Join your official VIP communication desks below:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 1. Telegram VIP Channel
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ButtonShape)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(plan.telegramInviteUrl))
                            context.startActivity(intent)
                        },
                    shape = ButtonShape,
                    color = Color(0xFF229ED9)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Join Telegram VIP Channel",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. WhatsApp VIP Group
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ButtonShape)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(plan.whatsappInviteUrl))
                            context.startActivity(intent)
                        },
                    shape = ButtonShape,
                    color = Color(0xFF25D366)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Join WhatsApp VIP Group",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TradeNovSecondaryButton(
                    text = "Done & Go to Setups",
                    onClick = onDismiss
                )
            }
        }
    )
}

@Composable
fun AssetMonogramBox(symbol: String, monogram: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when {
        symbol.startsWith("TATA") -> Color(0xFFEFF6FF) to Color(0xFF1D4ED8)
        symbol.startsWith("SBIN") -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
        symbol.startsWith("RELIANCE") -> Color(0xFFFEF2F2) to Color(0xFFDC2626)
        symbol.startsWith("NIFTY") -> Color(0xFFECFDF5) to Color(0xFF047857)
        symbol.startsWith("GOLD") -> Color(0xFFFFFBEB) to Color(0xFFB45309)
        symbol.startsWith("BTC") -> Color(0xFFFFF7ED) to Color(0xFFEA580C)
        else -> Color(0xFFF1F5F9) to Color(0xFF334155)
    }

    Box(
        modifier = modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = monogram,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
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
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        elevation = 2.dp
    ) {
        // 1. Top Row: Monogram, Symbol + Segment Badges, Company Name | UNLOCK FEE + Price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssetMonogramBox(symbol = tip.symbol, monogram = tip.monogram)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = tip.symbol,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0284C7)
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE0F2FE)
                        ) {
                            Text(
                                text = tip.marketSegment.displayName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1),
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tip.companyName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            // Right side: Unlock Fee / Price
            Column(horizontalAlignment = Alignment.End) {
                if (!tip.isUnlocked) {
                    Text(
                        text = "UNLOCK FEE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = LightTextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "₹${String.format("%.2f", tip.basePrice)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0284C7),
                            fontSize = 18.sp
                        )
                    )
                } else {
                    Surface(
                        shape = PillShape,
                        color = EmeraldLight
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "UNLOCKED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldDark,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Badges Row: ACTIVE (with live green signal) | ACTION HIDDEN / BUY / SELL | SWING / DELIVERY
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (tip.status == TipStatus.RUNNING) {
                Surface(
                    shape = PillShape,
                    color = Color(0xFFECFDF5),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857),
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            } else {
                Surface(
                    shape = PillShape,
                    color = if (tip.status == TipStatus.TARGET_HIT) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (tip.status == TipStatus.TARGET_HIT) Color(0xFFA7F3D0) else Color(0xFFFECACA))
                ) {
                    Text(
                        text = tip.status.name.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (tip.status == TipStatus.TARGET_HIT) Color(0xFF047857) else Color(0xFFDC2626),
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (!tip.isUnlocked) {
                Surface(
                    shape = PillShape,
                    color = Color(0xFFFFFBEB),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ACTION HIDDEN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309),
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            } else {
                Surface(
                    shape = PillShape,
                    color = if (tip.action == TradeAction.BUY) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                ) {
                    Text(
                        text = tip.action.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (tip.action == TradeAction.BUY) Color(0xFF047857) else Color(0xFFDC2626),
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Surface(
                shape = PillShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Text(
                    text = tip.tradeType.name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            if (tip.isUnlocked && tip.liveProfitLoss != 0.0) {
                Surface(
                    shape = PillShape,
                    color = if (tip.profitLossRupees >= 0) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                ) {
                    Text(
                        text = if (tip.profitLossRupees >= 0) "+${String.format("%.1f", tip.liveProfitLossPercent)}% PROFIT" else "${String.format("%.1f", tip.liveProfitLossPercent)}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (tip.profitLossRupees >= 0) Color(0xFF047857) else Color(0xFFDC2626),
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Setup Title & Risk/Reward
        Text(
            text = tip.signalName,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = "Risk/Reward: ${tip.riskRewardRatio}",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Three Parameter Boxes (ENTRY | STOP LOSS | TARGET 1) matching Image 4
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Box 1: ENTRY
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (tip.isUnlocked) "ENTRY RANGE" else "ENTRY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = LightTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (tip.isUnlocked) {
                        Text(
                            text = "₹${tip.entryRangeMin} - ₹${tip.entryRangeMax}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF047857),
                                fontSize = 12.sp
                            )
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("₹••••.••", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp))
                        }
                    }
                }

                // Divider 1
                Divider(modifier = Modifier.height(30.dp).width(1.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Box 2: STOP LOSS
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "STOP LOSS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = LightTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (tip.isUnlocked) {
                        Text(
                            text = "₹${tip.stopLoss}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFDC2626),
                                fontSize = 12.sp
                            )
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("₹••••.••", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp))
                        }
                    }
                }

                // Divider 2
                Divider(modifier = Modifier.height(30.dp).width(1.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Box 3: TARGET 1
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TARGET 1",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = LightTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (tip.isUnlocked) {
                        Text(
                            text = "₹${tip.target1}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF047857),
                                fontSize = 12.sp
                            )
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("₹••••.••", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5. Date & Time WITH SECONDS (Very important requirement)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = Color(0xFF0284C7),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = tip.createdAt,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 6. Notice Banner (Locked) or VIP Social Follow-ups (Unlocked)
        if (!tip.isUnlocked) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFFBEB),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEF3C7))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Buy/Sell & target prices hidden. Revealed upon unlocking setup after login.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF92400E),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    Text(
                        text = "● VIP Social Follow-Ups:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534),
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(6.dp), color = TelegramBlue) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("📱 Telegram VIP", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = WhatsAppGreen) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("💬 WhatsApp VIP", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE2E8F0)) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🤖 Bot DMs", color = Color(0xFF334155), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 7. Description / Notes
        if (!tip.notes.isNullOrBlank()) {
            Text(
                text = tip.notes,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 8. Author & Verification Line
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "By ",
                    style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 11.sp)
                )
                Text(
                    text = tip.expertName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified Expert",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(13.dp)
                )
            }

            Text(
                text = "Verified Desk",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857),
                    fontSize = 10.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 9. Big Bottom Action Button
        if (!tip.isUnlocked) {
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F46E5)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unlock Setup for ₹${String.format("%.2f", tip.basePrice)} →",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        } else {
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF047857)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "View Active Live Trade →",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }
            }
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
            .width(245.dp)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = expert.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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


