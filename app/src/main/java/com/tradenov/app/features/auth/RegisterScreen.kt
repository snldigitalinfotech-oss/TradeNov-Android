package com.tradenov.app.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import com.tradenov.app.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradenov.app.core.designsystem.*
import com.tradenov.app.core.model.UserRole

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateLogin: () -> Unit,
    onRegisterSuccess: (AuthNavigationEvent) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.TRADER) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var termsDialogTitle by remember { mutableStateOf("") }
    var termsDialogText by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Official Brand Logo
            Image(
                painter = painterResource(id = R.drawable.tradenov_logo),
                contentDescription = "TradeNov Official Logo",
                modifier = Modifier
                    .height(44.dp)
                    .fillMaxWidth(0.6f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Create Your Account",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Join TradeNov and trade with institutional precision",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Role Selector Cards (Trader vs Expert)
            Text(
                text = "Select Account Type",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Trader Card
                val isTraderSelected = selectedRole == UserRole.TRADER
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CardShape)
                        .clickable { selectedRole = UserRole.TRADER },
                    shape = CardShape,
                    color = if (isTraderSelected) OrangeLight else MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isTraderSelected) OrangePrimary else MaterialTheme.colorScheme.outline
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = if (isTraderSelected) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Trader",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isTraderSelected) OrangePrimary else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Browse & Buy Tips",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Expert Card
                val isExpertSelected = selectedRole == UserRole.EXPERT
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CardShape)
                        .clickable { selectedRole = UserRole.EXPERT },
                    shape = CardShape,
                    color = if (isExpertSelected) OrangeLight else MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isExpertSelected) OrangePrimary else MaterialTheme.colorScheme.outline
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = if (isExpertSelected) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Analyst / Expert",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isExpertSelected) OrangePrimary else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Publish Tips & Earn",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error banner
            if (errorMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ButtonShape,
                    color = LossRedLight
                ) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(color = LossRed),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Input fields
            TradeNovTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                placeholder = "e.g. Rahul Sharma",
                leadingIcon = Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(14.dp))

            TradeNovTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                placeholder = "rahul@example.com",
                leadingIcon = Icons.Outlined.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(14.dp))

            TradeNovTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Mobile Number",
                placeholder = "+91 9876543210",
                leadingIcon = Icons.Outlined.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(14.dp))

            TradeNovTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password (min 6 characters)",
                placeholder = "Create a strong password",
                leadingIcon = Icons.Outlined.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mandatory Terms & Privacy Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { agreedToTerms = !agreedToTerms },
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "I have read and agree to TradeNov's Terms & Conditions, Privacy Policy, and Refund Policy.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Terms",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OrangePrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable {
                                termsDialogTitle = "Terms & Conditions"
                                termsDialogText = "By creating an account on TradeNov, you agree to our Terms of Service. Trading securities and derivatives carries market risks. TradeNov is an informational and analytics platform connecting certified experts and traders."
                                showTermsDialog = true
                            }
                        )
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OrangePrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable {
                                termsDialogTitle = "Privacy Policy"
                                termsDialogText = "TradeNov respects your privacy. We collect registered name, email, and mobile number to authenticate your account and securely deliver signals. Your personal financial data is encrypted."
                                showTermsDialog = true
                            }
                        )
                        Text(
                            text = "Refund Policy",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OrangePrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable {
                                termsDialogTitle = "Cancellation & Refund Policy"
                                termsDialogText = "All purchases of tips and subscription passes can be cancelled if a signal is invalid before trade execution. Wallet top-ups can be refunded to the original payment source within 7 days upon support review."
                                showTermsDialog = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TradeNovPrimaryButton(
                text = if (selectedRole == UserRole.EXPERT) "Register as Expert" else "Create Trader Account",
                onClick = {
                    if (!agreedToTerms) return@TradeNovPrimaryButton
                    viewModel.register(name, email, phone, password, selectedRole) { event ->
                        onRegisterSuccess(event)
                    }
                },
                isLoading = isLoading,
                enabled = agreedToTerms
            )

            if (showTermsDialog) {
                AlertDialog(
                    onDismissRequest = { showTermsDialog = false },
                    title = { Text(termsDialogTitle, fontWeight = FontWeight.Bold) },
                    text = { Text(termsDialogText, style = MaterialTheme.typography.bodyMedium) },
                    confirmButton = {
                        TextButton(onClick = { showTermsDialog = false }) {
                            Text("I Understand", color = OrangePrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    ),
                    modifier = Modifier.clickable { onNavigateLogin() }
                )
            }
        }
    }
}
