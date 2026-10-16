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
                MarketSegment.OPTIONS_INDEX, MarketSegment.OPTIONS_STOCKS -> 50.0
                MarketSegment.COMMODITIES -> 10.0
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

    val companyName: String
        get() = when (symbol.uppercase()) {
            "TATASTEEL" -> "Tata Steel Ltd."
            "SBIN" -> "State Bank Of India"
            "RELIANCE" -> "Reliance Industries Ltd."
            "INFY" -> "Infosys Limited"
            "TCS" -> "Tata Consultancy Services"
            "HDFCBANK" -> "HDFC Bank Limited"
            "ICICIBANK" -> "ICICI Bank Limited"
            "GOLD", "GOLD 05DEC" -> "MCX Gold Futures"
            "SILVER" -> "MCX Silver Futures"
            "CRUDEOIL" -> "MCX Crude Oil"
            "BTCUSDT" -> "Bitcoin / USDT Crypto"
            "NIFTY 24500 CE", "NIFTY 50" -> "NSE Nifty 50 Index"
            "BANKNIFTY 51200 PE" -> "NSE Bank Nifty Index"
            else -> if (symbol.contains("NIFTY")) "Nifty Index Options" else "$symbol Ltd."
        }

    val monogram: String
        get() = when {
            symbol.startsWith("TATA") -> "TATA"
            symbol.startsWith("SBIN") -> "SBIN"
            symbol.startsWith("RELIANCE") -> "RIL"
            symbol.startsWith("INFY") -> "INFY"
            symbol.startsWith("HDFC") -> "HDFC"
            symbol.startsWith("ICICI") -> "ICICI"
            symbol.startsWith("NIFTY") -> "NSE"
            symbol.startsWith("BANK") -> "BNF"
            symbol.startsWith("GOLD") -> "GOLD"
            symbol.startsWith("BTC") -> "BTC"
            else -> symbol.take(4).uppercase()
        }

    val riskRewardRatio: String
        get() {
            val risk = Math.abs(capturedEntryPrice - stopLoss)
            val reward = Math.abs(target1 - capturedEntryPrice)
            if (risk > 0 && reward > 0) {
                val ratio = reward / risk
                return "1:${String.format("%.1f", ratio)}"
            }
            return "1:1.7"
        }

    val exchangeBadge: String
        get() = when (marketSegment) {
            MarketSegment.COMMODITIES -> "MCX"
            MarketSegment.CRYPTO -> "CRYPTO"
            MarketSegment.FOREX -> "FOREX"
            else -> "NSE"
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
    val expiresInDays: Int? = null,
    val isActive: Boolean = true,
    val subscribersCount: Int = 42,
    val totalRevenue: Double = 62500.0
) : Serializable

data class ExpertSubscriber(
    val id: String,
    val traderName: String,
    val planOrSignalName: String,
    val marketSegment: MarketSegment,
    val amountPaid: Double,
    val purchaseDate: String,
    val expiryDate: String,
    val isActive: Boolean = true,
    val autoRenew: Boolean = true
) : Serializable

data class SegmentPerformance(
    val segment: MarketSegment,
    val totalSignals: Int,
    val winCount: Int,
    val lossCount: Int,
    val winRate: Double,
    val netPoints: Double
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

data class TradeJournalEntry(
    val id: String,
    val symbol: String,
    val marketSegment: MarketSegment,
    val action: TradeAction,
    val entryPrice: Double,
    val exitPrice: Double,
    val quantity: Int = 1,
    val pnlRupees: Double,
    val pnlPercent: Double,
    val date: String,
    val notes: String = "",
    val emotion: String = "Disciplined"
) : Serializable

data class ReferredUser(
    val id: String,
    val name: String,
    val dateJoined: String,
    val status: String = "ACTIVE",
    val commissionEarned: Double = 250.0
) : Serializable

data class ReferralData(
    val referralCode: String = "NOVTRADER10",
    val referralLink: String = "https://tradenov.com/join?ref=NOVTRADER10",
    val totalReferred: Int = 12,
    val totalEarnedRupees: Double = 3450.0,
    val pendingPayoutRupees: Double = 650.0,
    val commissionPercent: Int = 10,
    val referrals: List<ReferredUser> = emptyList()
) : Serializable

data class FaqItem(
    val question: String,
    val answer: String,
    val category: String = "General"
) : Serializable

