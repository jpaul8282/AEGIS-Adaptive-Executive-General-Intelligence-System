package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.auth.AegisUser
import com.example.auth.AuthState
import com.example.ui.theme.*

@Composable
fun AegisAuthDialog(
    authState: AuthState,
    onDismiss: () -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInWithEmail: (String, String) -> Unit,
    onSignInAnonymously: () -> Unit,
    onSignOut: () -> Unit
) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AegisSurfaceDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("auth_dialog_surface")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Shield Icon Header
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AegisGoldContainer)
                        .border(1.dp, AegisGoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "AEGIS Security Access",
                        tint = AegisGoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "AEGIS Security Clearance",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AegisTextPrimary
                )

                Text(
                    text = "Firebase Auth & Credential Manager Layer",
                    style = MaterialTheme.typography.labelSmall,
                    color = AegisCyanAccent
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (authState) {
                    is AuthState.SignedIn -> {
                        val user = authState.user
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AegisSurfaceVariantDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = AegisSecurityGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = user.displayName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AegisTextPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AegisTextSecondary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    color = AegisBorderDark,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "${user.securityLevel} • ${user.authProvider}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = AegisGoldPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = onSignOut,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AegisAlertRed,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("sign_out_button")
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign Out")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Revoke Security Session")
                                }
                            }
                        }
                    }

                    is AuthState.Loading -> {
                        CircularProgressIndicator(color = AegisGoldPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Authenticating with Credential Manager...",
                            style = MaterialTheme.typography.bodySmall,
                            color = AegisTextSecondary
                        )
                    }

                    is AuthState.SignedOut, is AuthState.Error -> {
                        if (authState is AuthState.Error) {
                            Text(
                                text = authState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = AegisAlertRed
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Option 1: Google Sign-In via Credential Manager
                        Button(
                            onClick = onSignInWithGoogle,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AegisGoldPrimary,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_sign_in_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Google Sign-In"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sign in with Google",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider(color = AegisBorderDark)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Option 2: Email & Password
                        OutlinedTextField(
                            value = emailText,
                            onValueChange = { emailText = it },
                            label = { Text("Executive Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input_field"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = passwordText,
                            onValueChange = { passwordText = it },
                            label = { Text("Passcode / Password") },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input_field"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                if (emailText.isNotBlank() && passwordText.isNotBlank()) {
                                    onSignInWithEmail(emailText, passwordText)
                                }
                            },
                            enabled = emailText.isNotBlank() && passwordText.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_sign_in_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Email / Password Sign In")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Option 3: Guest Clearance
                        TextButton(
                            onClick = onSignInAnonymously,
                            modifier = Modifier.testTag("anonymous_guest_button")
                        ) {
                            Text(
                                text = "Continue as Executive Guest",
                                color = AegisTextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onDismiss) {
                    Text("Close", color = AegisTextSecondary)
                }
            }
        }
    }
}
