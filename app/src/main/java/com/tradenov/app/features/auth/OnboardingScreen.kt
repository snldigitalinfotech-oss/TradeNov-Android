package com.tradenov.app.features.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.designsystem.OrangeLight
import com.tradenov.app.core.designsystem.OrangePrimary

data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPageData(
            title = "Expert Trading Insights",
            subtitle = "Get high-probability intraday & swing calls from top technical analysts and price action chartists.",
            icon = Icons.Default.Analytics
        ),
        OnboardingPageData(
            title = "Follow Professional Experts",
            subtitle = "Discover top rated traders across Stocks, Index Options, Commodity, Forex, and Crypto with transparent win-rate audits.",
            icon = Icons.Default.VerifiedUser
        ),
        OnboardingPageData(
            title = "Real-Time Trade Updates",
            subtitle = "Instant notifications on entry trigger, target 1/2/3 hits, trailing stop loss, and synchronized Telegram & WhatsApp alerts.",
            icon = Icons.Default.NotificationsActive
        ),
        OnboardingPageData(
            title = "Track Performance & Results",
            subtitle = "Complete transparency with verified performance timelines, profit/loss indicators, and secure one-click tip unlocks.",
            icon = Icons.Default.ShowChart
        )
    )

    var currentPage by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Skip Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (currentPage < pages.size - 1) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onFinish() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            // Animated Visual & Text Content
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "OnboardingContent"
            ) { pageIdx ->
                val page = pages[pageIdx]
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Modern Illustration Card with Orange Accents
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(CircleShape)
                            .background(OrangeLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(170.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = page.icon,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(90.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = page.subtitle,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            // Bottom Navigation Row: Indicator Dots + Circular Next Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pages.indices.forEach { index ->
                        val isSelected = index == currentPage
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (isSelected) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) OrangePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )
                    }
                }

                // Circular Orange Next Button (Matching reference UI)
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(6.dp, CircleShape, spotColor = OrangePrimary.copy(alpha = 0.4f))
                        .clip(CircleShape)
                        .background(OrangePrimary)
                        .clickable {
                            if (currentPage < pages.size - 1) {
                                currentPage++
                            } else {
                                onFinish()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (currentPage == pages.size - 1) Icons.Default.Check else Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
