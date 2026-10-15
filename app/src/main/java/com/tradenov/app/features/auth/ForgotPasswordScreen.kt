package com.tradenov.app.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.R
import com.tradenov.app.core.designsystem.*
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Email, 2: OTP & New Password, 3: Success
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var resendTimer by remember { mutableStateOf(60) }
    var localError by remember { mutableStateOf<String?>(null) }

    val isLoading by viewModel.isLoading.collectAsState()
    val serverError by viewModel.errorMessage.collectAsState()

    // Countdown timer for OTP resend
    LaunchedEffect(step, resendTimer) {
        if (step == 2 && resendTimer > 0) {
            delay(1000)
            resendTimer -= 1
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (step == 3) "Password Reset" else "Forgot Password",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Real Project Logo
            Image(
                painter = painterResource(id = R.drawable.tradenov_logo),
                contentDescription = "TradeNov Official Logo",
                modifier = Modifier
                    .height(44.dp)
                    .fillMaxWidth(0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = when (step) {
                    1 -> "Reset Your Password"
                    2 -> "Enter Verification Code"
                    else -> "Password Updated!"
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when (step) {
                    1 -> "Enter your registered email address to receive a secure OTP code."
                    2 -> "We've sent a 6-digit verification code to $email."
                    else -> "Your password has been successfully reset. You can now login with your new credentials."
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error Message
            val displayedError = localError ?: serverError
            if (displayedError != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ButtonShape,
                    color = LossRedLight
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = LossRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = displayedError,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LossRed,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // STEP 1: Email Form
            if (step == 1) {
                TradeNovTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        localError = null
                    },
                    label = "Registered Email Address",
                    placeholder = "name@example.com",
                    leadingIcon = Icons.Outlined.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(24.dp))

                TradeNovPrimaryButton(
                    text = "Send Verification Code",
                    onClick = {
                        if (email.isBlank() || !email.contains("@")) {
                            localError = "Please enter a valid registered email"
                            return@TradeNovPrimaryButton
                        }
                        localError = null
                        viewModel.requestForgotPassword(email) {
                            step = 2
                            resendTimer = 60
                        }
                    },
                    isLoading = isLoading
                )
            }

            // STEP 2: OTP & New Password Form
            if (step == 2) {
                TradeNovTextField(
                    value = otp,
                    onValueChange = {
                        if (it.length <= 6) otp = it
                        localError = null
                    },
                    label = "6-Digit Verification Code",
                    placeholder = "123456",
                    leadingIcon = Icons.Outlined.Pin,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Resend Code row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (resendTimer > 0) "Resend code in ${resendTimer}s" else "Resend Code",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (resendTimer > 0) LightTextMuted else OrangePrimary
                        ),
                        modifier = Modifier
                            .clickable(enabled = resendTimer == 0) {
                                resendTimer = 60
                                viewModel.requestForgotPassword(email) {}
                            }
                            .padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                TradeNovTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        localError = null
                    },
                    label = "New Password (min 6 chars)",
                    placeholder = "Enter new password",
                    leadingIcon = Icons.Outlined.Lock,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                TradeNovTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        localError = null
                    },
                    label = "Confirm New Password",
                    placeholder = "Re-enter new password",
                    leadingIcon = Icons.Outlined.Lock,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                TradeNovPrimaryButton(
                    text = "Reset Password & Save",
                    onClick = {
                        if (otp.length < 4) {
                            localError = "Please enter the verification code"
                            return@TradeNovPrimaryButton
                        }
                        if (newPassword.length < 6) {
                            localError = "Password must be at least 6 characters"
                            return@TradeNovPrimaryButton
                        }
                        if (newPassword != confirmPassword) {
                            localError = "Passwords do not match"
                            return@TradeNovPrimaryButton
                        }
                        localError = null
                        viewModel.submitResetPassword(email, otp, newPassword) {
                            step = 3
                        }
                    },
                    isLoading = isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = { step = 1 }) {
                    Text(
                        text = "Change Email Address",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }

            // STEP 3: Success Confirmation
            if (step == 3) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = ProfitGreenLight
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ProfitGreen,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TradeNovPrimaryButton(
                    text = "Sign In Now",
                    onClick = onResetSuccess
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            if (step != 3) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Remember your password? ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        ),
                        modifier = Modifier.clickable { onNavigateBack() }
                    )
                }
            }
        }
    }
}
