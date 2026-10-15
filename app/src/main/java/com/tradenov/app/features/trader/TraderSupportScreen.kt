package com.tradenov.app.features.trader

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.FaqItem

@Composable
fun TraderSupportScreen(
    repository: TradeNovRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val faqs = remember { repository.getFaqs() }

    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }
    var showTicketDialog by remember { mutableStateOf(false) }
    var ticketCategory by remember { mutableStateOf("Billing / Wallet") }
    var ticketSubject by remember { mutableStateOf("") }
    var ticketMessage by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Help & 24/7 Support",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            // Direct Contact Channels Banner
            item {
                Text(
                    text = "Instant Contact Channels",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SupportChannelCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Chat,
                        title = "WhatsApp",
                        subtitle = "Instant Chat",
                        color = WhatsAppGreen,
                        onClick = {
                            val uri = Uri.parse("https://wa.me/919876543210?text=Hello%20TradeNov%20Support,%20I%20need%20assistance.")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    )

                    SupportChannelCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Send,
                        title = "Telegram",
                        subtitle = "@tradenovbot",
                        color = TelegramBlue,
                        onClick = {
                            val uri = Uri.parse("https://t.me/tradenov")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    )

                    SupportChannelCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.MailOutline,
                        title = "Email Desk",
                        subtitle = "support@",
                        color = OrangePrimary,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@tradenov.com")
                                putExtra(Intent.EXTRA_SUBJECT, "TradeNov Support Request")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Raise Support Ticket CTA
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Raise a Support Ticket", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Submit a formal query or transaction dispute.", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        Button(
                            onClick = { showTicketDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = ButtonShape
                        ) {
                            Text("New Ticket", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // FAQs Section
            item {
                Text(
                    text = "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(faqs.indices.toList()) { index ->
                val faq = faqs[index]
                val isExpanded = expandedFaqIndex == index

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable {
                            expandedFaqIndex = if (isExpanded) null else index
                        },
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = faq.question,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = OrangePrimary
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = faq.answer,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTicketDialog) {
        AlertDialog(
            onDismissRequest = { showTicketDialog = false },
            title = { Text("Submit Support Ticket", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Category", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Billing", "Signal", "KYC", "General").forEach { cat ->
                            Surface(
                                shape = PillShape,
                                color = if (ticketCategory.contains(cat)) OrangeLight else MaterialTheme.colorScheme.surface,
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.clickable { ticketCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (ticketCategory.contains(cat)) OrangePrimary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = ticketSubject,
                        onValueChange = { ticketSubject = it },
                        label = { Text("Subject") },
                        placeholder = { Text("e.g. Wallet deposit pending") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ticketMessage,
                        onValueChange = { ticketMessage = it },
                        label = { Text("Description") },
                        placeholder = { Text("Describe the issue in detail...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (ticketSubject.isBlank() || ticketMessage.isBlank()) {
                            Toast.makeText(context, "Please fill in all ticket details", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        showTicketDialog = false
                        Toast.makeText(context, "Ticket #TN-${(1000..9999).random()} submitted successfully!", Toast.LENGTH_LONG).show()
                        ticketSubject = ""
                        ticketMessage = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Submit Ticket")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showTicketDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SupportChannelCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 10.sp))
        }
    }
}
