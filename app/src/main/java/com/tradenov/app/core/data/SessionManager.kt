package com.tradenov.app.core.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.tradenov.app.core.designsystem.AppThemeMode
import com.tradenov.app.core.model.ExpertApprovalStatus
import com.tradenov.app.core.model.User
import com.tradenov.app.core.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tradenov_secure_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _currentUser = MutableStateFlow<User?>(loadUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private fun loadUser(): User? {
        val userJson = prefs.getString("user_profile", null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun loadThemeMode(): AppThemeMode {
        val modeStr = prefs.getString("theme_mode", AppThemeMode.LIGHT.name) ?: AppThemeMode.LIGHT.name
        return try {
            AppThemeMode.valueOf(modeStr)
        } catch (e: Exception) {
            AppThemeMode.LIGHT
        }
    }

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit()
            .putString("user_profile", userJson)
            .putString("auth_token", user.token)
            .apply()
        _currentUser.value = user
    }

    fun updateWalletBalance(newBalance: Double) {
        val current = _currentUser.value ?: return
        val updated = current.copy(walletBalance = newBalance)
        saveUser(updated)
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        _currentUser.value = null
    }

    fun isLoggedIn(): Boolean = _currentUser.value != null

    companion object {
        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
