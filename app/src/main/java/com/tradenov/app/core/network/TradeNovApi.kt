package com.tradenov.app.core.network

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// API Request / Response DTOs
data class LoginRequest(
    val emailOrPhone: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String // "TRADER" or "EXPERT"
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

data class ForgotPasswordRequest(
    val login: String
)

data class ResetPasswordRequest(
    val login: String,
    val otp: String,
    val newPassword: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

data class AuthResponseData(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val expertStatus: String? = "APPROVED",
    val walletBalance: Double? = 0.0,
    val avatarUrl: String? = null
)

data class UnlockTipResponseData(
    val purchaseId: String,
    val tipId: String,
    val amountPaid: Double,
    val walletBalance: Double,
    val vipChannels: VipChannelsDto? = null
)

data class VipChannelsDto(
    val telegram: TelegramChannelDto? = null,
    val whatsapp: WhatsAppChannelDto? = null
)

data class TelegramChannelDto(
    val channelName: String? = null,
    val inviteUrl: String? = null,
    val botUsername: String? = null,
    val botStartUrl: String? = null
)

data class WhatsAppChannelDto(
    val groupName: String? = null,
    val inviteUrl: String? = null,
    val supportNumber: String? = null
)

data class TopupRequest(
    val amount: Double,
    val paymentMethod: String,
    val utrNumber: String? = null,
    val screenshotUrl: String? = null
)

data class WithdrawalRequestDto(
    val amount: Double,
    val method: String,
    val upiId: String? = null,
    val bankAccountNumber: String? = null,
    val ifscCode: String? = null
)

data class CreateTipRequestDto(
    val symbol: String,
    val segment: String,
    val action: String,
    val tradeType: String,
    val entryRangeMin: Double,
    val entryRangeMax: Double,
    val target1: Double,
    val target2: Double? = null,
    val target3: Double? = null,
    val stopLoss: Double,
    val riskLevel: String,
    val notes: String? = null,
    val basePrice: Double = 99.0,
    val isFree: Boolean = false
)

data class SignalActionRequestDto(
    val actionType: String, // TARGET_1_HIT, PARTIAL_PROFIT, STOP_LOSS_HIT, CLOSE
    val notes: String? = null,
    val actionPrice: Double? = null
)

interface TradeNovApiService {
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<ApiResponse<AuthResponseData>>

    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): Response<ApiResponse<AuthResponseData>>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body req: VerifyOtpRequest): Response<ApiResponse<AuthResponseData>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body req: ForgotPasswordRequest): Response<ApiResponse<Any>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body req: ResetPasswordRequest): Response<ApiResponse<Any>>

    @GET("notifications/vip-channels")
    suspend fun getVipChannels(@Query("segment") segment: String? = null): Response<ApiResponse<VipChannelsDto>>

    @POST("purchases/signals/{id}")
    suspend fun unlockSignal(@Path("id") signalId: String): Response<ApiResponse<UnlockTipResponseData>>

    @POST("plans/{id}/subscribe")
    suspend fun subscribePlan(@Path("id") planId: String): Response<ApiResponse<Any>>

    @POST("wallet/topup")
    suspend fun requestTopup(@Body req: TopupRequest): Response<ApiResponse<Any>>

    @POST("wallet/withdraw")
    suspend fun requestWithdrawal(@Body req: WithdrawalRequestDto): Response<ApiResponse<Any>>

    @GET("signals")
    suspend fun getSignals(
        @Query("status") status: String? = null,
        @Query("segment") segment: String? = null,
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int? = 50
    ): Response<SignalsResponseData>

    @POST("signals/{id}/actions")
    suspend fun updateSignalAction(
        @Path("id") signalId: String,
        @Body req: SignalActionRequestDto
    ): Response<ApiResponse<Any>>
}

data class SignalTargetDto(
    val targetNumber: Int = 1,
    val targetPrice: Double = 0.0,
    val status: String? = null
)

data class SignalDto(
    val id: String,
    val signalName: String? = null,
    val slug: String? = null,
    val expertName: String? = "Verified Expert",
    val expertAvatar: String? = null,
    val expertSlug: String? = null,
    val segment: String? = "STOCKS",
    val tradeType: String? = "SWING",
    val duration: String? = null,
    val instrument: String? = null,
    val symbol: String,
    val exchange: String? = "NSE",
    val lotSize: Int? = 1,
    val entryPriceMin: Double? = 0.0,
    val entryPriceMax: Double? = 0.0,
    val stopLoss: Double? = null,
    val exitPrice: Double? = null,
    val realizedPnlPct: Double? = null,
    val basePrice: Double = 99.0,
    val isPromoted: Boolean = false,
    val status: String = "ACTIVE",
    val result: String? = null,
    val publishedAt: String? = null,
    val isPurchased: Boolean = false,
    val targets: List<SignalTargetDto>? = emptyList()
)

data class SignalsResponseData(
    val signals: List<SignalDto> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val limit: Int = 50
)


object ApiClient {
    // 10.0.2.2 points to host machine from Android Emulator; localhost:4000 on physical machine / preview
    private const val BASE_URL = "http://10.0.2.2:4000/api/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: TradeNovApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TradeNovApiService::class.java)
    }
}
