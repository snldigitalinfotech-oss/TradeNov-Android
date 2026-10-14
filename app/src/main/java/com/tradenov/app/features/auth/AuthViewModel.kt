package com.tradenov.app.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradenov.app.core.data.SessionManager
import com.tradenov.app.core.data.TradeNovRepository
import com.tradenov.app.core.model.ExpertApprovalStatus
import com.tradenov.app.core.model.User
import com.tradenov.app.core.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthNavigationEvent {
    object NavigateToTraderHome : AuthNavigationEvent()
    object NavigateToExpertDashboard : AuthNavigationEvent()
    object NavigateToPendingApproval : AuthNavigationEvent()
    object NavigateToOtpVerify : AuthNavigationEvent()
    data class ShowError(val message: String) : AuthNavigationEvent()
}

class AuthViewModel(
    private val repository: TradeNovRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _currentUser = sessionManager.currentUser
    val currentUser: StateFlow<User?> = _currentUser

    fun checkExistingSession(onResult: (AuthNavigationEvent?) -> Unit) {
        val user = currentUser.value
        if (user != null) {
            when {
                user.role == UserRole.EXPERT && user.expertStatus == ExpertApprovalStatus.PENDING_APPROVAL -> {
                    onResult(AuthNavigationEvent.NavigateToPendingApproval)
                }
                user.role == UserRole.EXPERT -> {
                    onResult(AuthNavigationEvent.NavigateToExpertDashboard)
                }
                else -> {
                    onResult(AuthNavigationEvent.NavigateToTraderHome)
                }
            }
        } else {
            onResult(null)
        }
    }

    fun login(emailOrPhone: String, pass: String, onNavigate: (AuthNavigationEvent) -> Unit) {
        if (emailOrPhone.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Please enter both email/phone and password"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.login(emailOrPhone, pass)
            _isLoading.value = false

            result.onSuccess { user ->
                when {
                    user.role == UserRole.EXPERT && user.expertStatus == ExpertApprovalStatus.PENDING_APPROVAL -> {
                        onNavigate(AuthNavigationEvent.NavigateToPendingApproval)
                    }
                    user.role == UserRole.EXPERT -> {
                        onNavigate(AuthNavigationEvent.NavigateToExpertDashboard)
                    }
                    else -> {
                        onNavigate(AuthNavigationEvent.NavigateToTraderHome)
                    }
                }
            }.onFailure { err ->
                _errorMessage.value = err.message ?: "Authentication failed. Please check credentials."
                onNavigate(AuthNavigationEvent.ShowError(_errorMessage.value!!))
            }
        }
    }

    fun register(
        name: String,
        email: String,
        phone: String,
        pass: String,
        role: UserRole,
        onNavigate: (AuthNavigationEvent) -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || pass.length < 6) {
            _errorMessage.value = "Please fill all fields properly (Password min 6 chars)"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.register(name, email, phone, pass, role)
            _isLoading.value = false

            result.onSuccess { user ->
                if (user.role == UserRole.EXPERT && user.expertStatus == ExpertApprovalStatus.PENDING_APPROVAL) {
                    onNavigate(AuthNavigationEvent.NavigateToPendingApproval)
                } else {
                    onNavigate(AuthNavigationEvent.NavigateToTraderHome)
                }
            }.onFailure { err ->
                _errorMessage.value = err.message ?: "Registration failed."
                onNavigate(AuthNavigationEvent.ShowError(_errorMessage.value!!))
            }
        }
    }

    fun verifyOtp(email: String, otp: String, onNavigate: (AuthNavigationEvent) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.verifyOtp(email, otp)
            _isLoading.value = false
            if (res.isSuccess) {
                val user = currentUser.value
                if (user?.role == UserRole.EXPERT) {
                    onNavigate(AuthNavigationEvent.NavigateToPendingApproval)
                } else {
                    onNavigate(AuthNavigationEvent.NavigateToTraderHome)
                }
            } else {
                _errorMessage.value = "Invalid OTP. Please try again."
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        sessionManager.clearSession()
        onLoggedOut()
    }
}
