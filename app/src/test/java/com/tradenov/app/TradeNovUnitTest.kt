package com.tradenov.app

import com.tradenov.app.core.data.SessionManager
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.model.*
import com.tradenov.app.core.network.*
import com.tradenov.app.features.auth.AuthNavigationEvent
import com.tradenov.app.features.auth.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class FakeTradeNovApiService : TradeNovApiService {
    var loginResponse: Response<ApiResponse<AuthResponseData>>? = null
    var registerResponse: Response<ApiResponse<AuthResponseData>>? = null

    override suspend fun login(req: LoginRequest): Response<ApiResponse<AuthResponseData>> {
        return loginResponse ?: Response.error(404, okhttp3.ResponseBody.create(null, ""))
    }

    override suspend fun register(req: RegisterRequest): Response<ApiResponse<AuthResponseData>> {
        return registerResponse ?: Response.error(404, okhttp3.ResponseBody.create(null, ""))
    }

    override suspend fun verifyOtp(req: VerifyOtpRequest): Response<ApiResponse<AuthResponseData>> {
        return Response.success(ApiResponse(true, "OK"))
    }

    override suspend fun getVipChannels(segment: String?): Response<ApiResponse<VipChannelsDto>> {
        return Response.success(ApiResponse(true, "OK", VipChannelsDto()))
    }

    override suspend fun unlockSignal(signalId: String): Response<ApiResponse<UnlockTipResponseData>> {
        return Response.success(ApiResponse(true, "OK"))
    }

    override suspend fun subscribePlan(planId: String): Response<ApiResponse<Any>> {
        return Response.success(ApiResponse(true, "OK"))
    }

    override suspend fun requestTopup(req: TopupRequest): Response<ApiResponse<Any>> {
        return Response.success(ApiResponse(true, "OK"))
    }

    override suspend fun requestWithdrawal(req: WithdrawalRequestDto): Response<ApiResponse<Any>> {
        return Response.success(ApiResponse(true, "OK"))
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TradeNovUnitTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeApiService: FakeTradeNovApiService
    private lateinit var repository: TradeNovRepository
    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeApiService = FakeTradeNovApiService()
        val mockSession = org.mockito.Mockito.mock(SessionManager::class.java)
        repository = TradeNovRepository(mockSession, fakeApiService)
        authViewModel = AuthViewModel(repository, mockSession)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testTraderLoginRoleRedirect() = runTest(testDispatcher) {
        val traderDto = UserDto(
            id = "usr_001",
            name = "Aman Verma",
            email = "aman.trader@tradenov.com",
            phone = "+91 98765 43210",
            role = "TRADER",
            walletBalance = 2500.0
        )
        fakeApiService.loginResponse = Response.success(ApiResponse(true, "OK", AuthResponseData("jwt_token", traderDto)))

        var event: AuthNavigationEvent? = null
        authViewModel.login("aman.trader@tradenov.com", "Tradenov@123") { ev ->
            event = ev
        }
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue("Expected trader home navigation", event is AuthNavigationEvent.NavigateToTraderHome)
    }

    @Test
    fun testExpertLoginRoleRedirect() = runTest(testDispatcher) {
        val expertDto = UserDto(
            id = "exp_001",
            name = "Kunal Sharma (SEBI RA)",
            email = "kunal.expert@tradenov.com",
            phone = "+91 98765 43210",
            role = "EXPERT",
            expertStatus = "APPROVED",
            walletBalance = 18450.0
        )
        fakeApiService.loginResponse = Response.success(ApiResponse(true, "OK", AuthResponseData("jwt_token", expertDto)))

        var event: AuthNavigationEvent? = null
        authViewModel.login("kunal.expert@tradenov.com", "Tradenov@123") { ev ->
            event = ev
        }
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue("Expected expert dashboard navigation", event is AuthNavigationEvent.NavigateToExpertDashboard)
    }

    @Test
    fun testExpertRegistrationPendingApproval() = runTest(testDispatcher) {
        val pendingExpertDto = UserDto(
            id = "exp_new",
            name = "Suresh Analyst",
            email = "suresh@expert.com",
            phone = "+91 98765 43210",
            role = "EXPERT",
            expertStatus = "PENDING_APPROVAL",
            walletBalance = 500.0
        )
        fakeApiService.registerResponse = Response.success(ApiResponse(true, "OK", AuthResponseData("jwt_token", pendingExpertDto)))

        var event: AuthNavigationEvent? = null
        authViewModel.register("Suresh Analyst", "suresh@expert.com", "+919876543210", "Password123", UserRole.EXPERT) { ev ->
            event = ev
        }
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue("Expected pending approval for newly registered expert", event is AuthNavigationEvent.NavigateToPendingApproval)
    }

    @Test
    fun testTipUnlockFlow() = runTest(testDispatcher) {
        val initialWallet = repository.wallet.value.availableBalance
        val tipToUnlock = repository.tips.value.first { !it.isUnlocked }

        val res = repository.unlockTip(tipToUnlock.id)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue("Tip unlock should succeed", res.isSuccess)
        val updatedTip = repository.tips.value.first { it.id == tipToUnlock.id }
        assertTrue("Tip should be unlocked", updatedTip.isUnlocked)

        val newWallet = repository.wallet.value.availableBalance
        assertEquals("Wallet should deduct tip price", initialWallet - tipToUnlock.basePrice, newWallet, 0.01)
    }

    @Test
    fun testWalletAddMoney() = runTest(testDispatcher) {
        val initialBalance = repository.wallet.value.availableBalance
        val addAmount = 1500.0

        val res = repository.addMoney(addAmount, "UPI QR")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue("Add money should succeed", res.isSuccess)
        val newBalance = repository.wallet.value.availableBalance
        assertEquals("Balance should increase by 1500", initialBalance + addAmount, newBalance, 0.01)
    }

    @Test
    fun testExpertPublishTip() = runTest(testDispatcher) {
        val countBefore = repository.tips.value.size
        val newTip = Tip(
            id = "test_tip_1",
            symbol = "INFY",
            signalName = "Infosys Swing Breakout",
            marketSegment = MarketSegment.STOCKS,
            action = TradeAction.BUY,
            tradeType = TradeType.SWING,
            entryRangeMin = 1850.0,
            entryRangeMax = 1865.0,
            capturedEntryPrice = 1855.0,
            currentMarketPrice = 1855.0,
            target1 = 1920.0,
            target2 = 1980.0,
            target3 = 2050.0,
            stopLoss = 1800.0,
            basePrice = 99.0,
            status = TipStatus.RUNNING,
            isUnlocked = true,
            expertId = "exp_001",
            expertName = "Kunal Sharma (SEBI RA)",
            expertRating = 4.9,
            createdAt = "Just now"
        )

        val res = repository.publishTip(newTip)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue("Publish tip should succeed", res.isSuccess)
        assertEquals("Tip count should increment", countBefore + 1, repository.tips.value.size)
    }

    @Test
    fun testExpertFollowUpAction() = runTest(testDispatcher) {
        val runningTip = repository.tips.value.first { it.status == TipStatus.RUNNING }
        val res = repository.updateTipAction(runningTip.id, "TARGET_1_HIT", "Target 1 hit at 162. Trail SL.")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue("Follow-up action should succeed", res.isSuccess)
        val updatedTip = repository.tips.value.first { it.id == runningTip.id }
        assertEquals("Tip status should become TARGET_HIT", TipStatus.TARGET_HIT, updatedTip.status)
        assertTrue("Timeline should contain follow-up step", updatedTip.timeline.any { it.title.contains("TARGET 1 HIT", true) })
    }

    @Test
    fun testPlatformCommissionCalculation() {
        val grossTipPrice = 1000.0
        val commissionRate = 0.10 // 10% Platform fee
        val platformFee = grossTipPrice * commissionRate
        val netExpertEarnings = grossTipPrice - platformFee

        assertEquals("Platform fee should be 10%", 100.0, platformFee, 0.01)
        assertEquals("Expert net should be 90%", 900.0, netExpertEarnings, 0.01)
    }
}
