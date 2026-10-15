package com.tradenov.app.features.trader

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
                if (tip.isUnlocked) {
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
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(OrangeLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked Setup",
                            tint = OrangePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = tip.symbol,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${tip.marketSegment.displayName} • ${if (tip.isUnlocked) tip.tradeType.name else "PREMIUM SETUP"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (tip.isUnlocked) {
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
                } else {
                    Surface(
                        shape = PillShape,
                        color = OrangeLight,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Text(
                            text = "PREMIUM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
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
            // Locked details preview (Fully masked matching requirements)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(InputShape)
                    .background(OrangeLight.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Entry, Targets & Stop Loss Masked",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Entry: ₹••••  |  Target: ₹••••  |  SL: ₹••••",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
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
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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


