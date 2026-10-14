package com.tradenov.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradenov.app.core.data.SessionManager
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.designsystem.AppThemeMode
import com.tradenov.app.core.designsystem.TradeNovTheme
import com.tradenov.app.core.model.ExpertApprovalStatus
import com.tradenov.app.core.model.UserRole
import com.tradenov.app.features.auth.*
import com.tradenov.app.features.expert.ExpertMainScreen
import com.tradenov.app.features.trader.TraderMainScreen

enum class AppDestination {
    SPLASH,
    ONBOARDING,
    LOGIN,
    REGISTER,
    PENDING_APPROVAL,
    TRADER_MAIN,
    EXPERT_MAIN
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager.getInstance(applicationContext)
        val repository = TradeNovRepository(sessionManager)

        setContent {
            val themeMode by sessionManager.themeMode.collectAsState()

            TradeNovTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var currentDestination by remember { mutableStateOf(AppDestination.SPLASH) }
                    val authViewModel = remember { AuthViewModel(repository, sessionManager) }

                    when (currentDestination) {
                        AppDestination.SPLASH -> {
                            SplashScreen(
                                viewModel = authViewModel,
                                onNavigateNext = { event ->
                                    currentDestination = when (event) {
                                        is AuthNavigationEvent.NavigateToTraderHome -> AppDestination.TRADER_MAIN
                                        is AuthNavigationEvent.NavigateToExpertDashboard -> AppDestination.EXPERT_MAIN
                                        is AuthNavigationEvent.NavigateToPendingApproval -> AppDestination.PENDING_APPROVAL
                                        else -> AppDestination.ONBOARDING
                                    }
                                }
                            )
                        }

                        AppDestination.ONBOARDING -> {
                            OnboardingScreen(
                                onFinish = { currentDestination = AppDestination.LOGIN }
                            )
                        }

                        AppDestination.LOGIN -> {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateRegister = { currentDestination = AppDestination.REGISTER },
                                onNavigateForgotPassword = {},
                                onLoginSuccess = { event ->
                                    currentDestination = when (event) {
                                        is AuthNavigationEvent.NavigateToTraderHome -> AppDestination.TRADER_MAIN
                                        is AuthNavigationEvent.NavigateToExpertDashboard -> AppDestination.EXPERT_MAIN
                                        is AuthNavigationEvent.NavigateToPendingApproval -> AppDestination.PENDING_APPROVAL
                                        else -> AppDestination.TRADER_MAIN
                                    }
                                }
                            )
                        }

                        AppDestination.REGISTER -> {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onNavigateLogin = { currentDestination = AppDestination.LOGIN },
                                onRegisterSuccess = { event ->
                                    currentDestination = when (event) {
                                        is AuthNavigationEvent.NavigateToPendingApproval -> AppDestination.PENDING_APPROVAL
                                        is AuthNavigationEvent.NavigateToExpertDashboard -> AppDestination.EXPERT_MAIN
                                        else -> AppDestination.TRADER_MAIN
                                    }
                                }
                            )
                        }

                        AppDestination.PENDING_APPROVAL -> {
                            PendingApprovalScreen(
                                onLogout = {
                                    authViewModel.logout {
                                        currentDestination = AppDestination.LOGIN
                                    }
                                },
                                onContactSupport = {}
                            )
                        }

                        AppDestination.TRADER_MAIN -> {
                            TraderMainScreen(
                                repository = repository,
                                sessionManager = sessionManager,
                                onLogout = {
                                    authViewModel.logout {
                                        currentDestination = AppDestination.LOGIN
                                    }
                                }
                            )
                        }

                        AppDestination.EXPERT_MAIN -> {
                            ExpertMainScreen(
                                repository = repository,
                                sessionManager = sessionManager,
                                onLogout = {
                                    authViewModel.logout {
                                        currentDestination = AppDestination.LOGIN
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
