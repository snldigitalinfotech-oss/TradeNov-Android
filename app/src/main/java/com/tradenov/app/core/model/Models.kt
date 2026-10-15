package com.tradenov.app.core.model

import java.io.Serializable

enum class UserRole {
    TRADER,
    EXPERT,
    ADMIN
}

enum class ExpertApprovalStatus {
    APPROVED,
    PENDING_APPROVAL,
    SUSPENDED
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val avatarUrl: String? = null,
    val isVerified: Boolean = false,
    val expertStatus: ExpertApprovalStatus = ExpertApprovalStatus.APPROVED,
    val walletBalance: Double = 0.0,
    val token: String? = null
) : Serializable

enum class MarketSegment(val displayName: String) {
    ALL("All"),
    STOCKS("Stocks"),
    OPTIONS_INDEX("Index Options"),
    OPTIONS_STOCKS("Stock Options"),
    COMMODITIES("Commodity"),
    FOREX("Forex"),
    CRYPTO("Crypto")
}

enum class TradeAction {
    BUY,
    SELL
}

enum class TradeType {
    INTRADAY,
    SWING,
    DELIVERY,
    POSITIONAL
}

enum class TipStatus {
    RUNNING,
    TARGET_HIT,
    STOP_LOSS_HIT,
    CLOSED
}

data class TipTimelineStep(
    val title: String,
    val price: Double?,
    val timestamp: String,
    val notes: String? = null,
    val isCompleted: Boolean = true
) : Serializable

data class Tip(
    val id: String,
    val symbol: String,
    val signalName: String,
    val marketSegment: MarketSegment,
    val action: TradeAction,
    val tradeType: TradeType,
    val entryRangeMin: Double,
    val entryRangeMax: Double,
    val capturedEntryPrice: Double,
    val currentMarketPrice: Double,
    val target1: Double,
    val target2: Double,
    val target3: Double,
    val stopLoss: Double,
    val riskLevel: String = "MODERATE", // LOW, MODERATE, HIGH
    val timeHorizon: String = "INTRADAY",
    val notes: String? = null,
    val basePrice: Double = 99.0,
    val status: TipStatus = TipStatus.RUNNING,
    val isUnlocked: Boolean = false,
    val expertId: String,
    val expertName: String,
    val expertAvatarUrl: String? = null,
    val expertRating: Double = 4.8,
    val createdAt: String,
    val timeline: List<TipTimelineStep> = emptyList()
) : Serializable {
    val liveProfitLoss: Double
        get() = if (action == TradeAction.BUY) {
            currentMarketPrice - capturedEntryPrice
        } else {
            capturedEntryPrice - currentMarketPrice
        }

    val liveProfitLossPercent: Double
        get() = if (capturedEntryPrice > 0) {
            (liveProfitLoss / capturedEntryPrice) * 100
        } else 0.0

    val profitLossRupees: Double
        get() {
            val pts = liveProfitLoss
            val multiplier = when (marketSegment) {
                MarketSegment.OPTIONS -> 50.0
                MarketSegment.FUTURES -> 25.0
                MarketSegment.COMMODITY -> 10.0
                else -> if (capturedEntryPrice < 500) 50.0 else 10.0
            }
            return pts * multiplier
        }

    val displayProfitLossText: String
        get() {
            val prefix = if (profitLossRupees >= 0) "+₹" else "-₹"
            val absAmt = Math.abs(profitLossRupees)
            val pct = liveProfitLossPercent
            val pctPrefix = if (pct >= 0) "+" else ""
            return "${prefix}${String.format("%,.0f", absAmt)} (${pctPrefix}${String.format("%.1f", pct)}%)"
        }
}

data class SubscriptionPlan(
    val id: String,
    val expertId: String,
    val expertName: String,
    val planName: String,
    val durationDays: Int,
    val originalPrice: Double,
    val offerPrice: Double,
    val description: String,
    val includedMarkets: List<String>,
    val hasTelegramAccess: Boolean = true,
    val hasWhatsAppAccess: Boolean = true,
    val telegramInviteUrl: String = "https://t.me/tradenov",
    val whatsappInviteUrl: String = "https://chat.whatsapp.com/TradeNovVIPAlerts",
    val isSubscribed: Boolean = false,
    val expiresInDays: Int? = null
) : Serializable

data class Expert(
    val id: String,
    val name: String,
    val tagline: String,
    val bio: String,
    val avatarUrl: String? = null,
    val isVerified: Boolean = true,
    val experienceYears: Int = 8,
    val markets: List<String> = listOf("Options", "Stocks", "Commodities"),
    val followersCount: Int = 12400,
    val subscribersCount: Int = 1420,
    val totalTips: Int = 380,
    val winRate: Double = 84.5,
    val rating: Double = 4.9,
    val plans: List<SubscriptionPlan> = emptyList(),
    val isFollowed: Boolean = false
) : Serializable

data class MarketQuote(
    val symbol: String,
    val name: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val isPositive: Boolean = change >= 0
) : Serializable

data class Wallet(
    val availableBalance: Double = 2500.0,
    val totalAdded: Double = 12000.0,
    val totalSpent: Double = 9500.0,
    val pendingEarnings: Double = 0.0,
    val transactions: List<TransactionItem> = emptyList()
) : Serializable

enum class TransactionType {
    CREDIT,
    DEBIT,
    PURCHASE,
    SUBSCRIPTION,
    WITHDRAWAL,
    COMMISSION
}

data class TransactionItem(
    val id: String,
    val title: String,
    val type: TransactionType,
    val amount: Double,
    val status: String = "COMPLETED",
    val date: String,
    val referenceId: String
) : Serializable

enum class NotificationCategory {
    ALL,
    TIPS,
    PAYMENTS,
    SUBSCRIPTIONS,
    SYSTEM
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val category: NotificationCategory,
    val timestamp: String,
    val isRead: Boolean = false,
    val deepLink: String? = null
) : Serializable

data class Blog(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val publishedAt: String,
    val readTimeMinutes: Int = 4,
    val tags: List<String> = listOf("Technical Analysis", "Nifty 50")
) : Serializable

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val expertName: String,
    val rating: Double = 4.8,
    val price: Double = 1499.0,
    val isFree: Boolean = false,
    val durationHours: Int = 12,
    val totalLessons: Int = 24,
    val thumbnailUrl: String? = null,
    val isPurchased: Boolean = false
) : Serializable
