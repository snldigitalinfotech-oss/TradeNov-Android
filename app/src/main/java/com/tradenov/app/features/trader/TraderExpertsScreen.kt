package com.tradenov.app.features.trader

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.Expert

@Composable
fun TraderExpertsScreen(
    repository: TradeNovRepository,
    onBack: () -> Unit,
    onNavigatePlans: (String) -> Unit
) {
    val experts by repository.experts.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    val filterOptions = listOf("All", "Options", "Stocks", "Commodities")

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
                    text = "Top Market Analysts",
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
            // Hero
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(OrangeLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(26.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Verified Technical Analysts", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(
                                "TradeNov connects traders with experienced technical analysts, chartists, and market strategists.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Segment Filter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { opt ->
                        val isSelected = selectedFilter == opt
                        Surface(
                            shape = PillShape,
                            color = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surface,
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.clickable { selectedFilter = opt }
                        ) {
                            Text(
                                text = opt,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Analysts List
            items(experts) { expert ->
                ExpertDetailedCard(
                    expert = expert,
                    onToggleFollow = { repository.toggleFollowExpert(expert.id) },
                    onViewPlans = { onNavigatePlans(expert.id) }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
fun ExpertDetailedCard(
    expert: Expert,
    onToggleFollow: () -> Unit,
    onViewPlans: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Avatar, Name, Reg, Follow CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(OrangeLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = expert.name.take(1),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = expert.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified Analyst",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Technical Analyst • ${expert.experienceYears}+ Yrs Exp",
                            style = MaterialTheme.typography.bodySmall.copy(color = LightTextMuted, fontSize = 11.sp)
                        )
                    }
                }

                Button(
                    onClick = onToggleFollow,
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (expert.isFollowed) MaterialTheme.colorScheme.surfaceVariant else OrangePrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (expert.isFollowed) "Following" else "Follow",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (expert.isFollowed) MaterialTheme.colorScheme.onSurface else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = expert.bio,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Bar: Win Rate, Total Tips, Rating
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = InputShape,
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Win Rate", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Text("${expert.winRate}%", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, color = ProfitGreen))
                    }
                    Column {
                        Text("Signals Published", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Text("${expert.totalTips} Calls", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Column {
                        Text("Subscribers", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Text("${expert.subscribersCount}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Rating", style = MaterialTheme.typography.labelSmall.copy(color = LightTextMuted))
                        Text("★ ${expert.rating}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = OrangePrimary))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onViewPlans,
                modifier = Modifier.fillMaxWidth().height(42.dp),
                shape = ButtonShape,
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(OrangePrimary)
                )
            ) {
                Text("View VIP Access Plans", color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
