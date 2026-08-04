package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.auth.AuthState
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.AegisViewModel
import java.util.Locale

@Composable
fun AegisHeader(
    selectedDomain: AegisDomain,
    lastSecurityStatus: String?,
    authState: AuthState,
    onOpenAuthDialog: () -> Unit,
    onOpenSessions: () -> Unit,
    onOpenAuditLogs: () -> Unit,
    onOpenTasks: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(AegisGoldPrimary, AegisCyanAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "AEGIS Shield",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AEGIS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Adaptive Executive Intelligence",
                            style = MaterialTheme.typography.labelSmall,
                            color = AegisCyanAccent
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onOpenSessions,
                        modifier = Modifier.testTag("open_sessions_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Chat Sessions History",
                            tint = AegisCyanAccent
                        )
                    }

                    IconButton(
                        onClick = onOpenAuthDialog,
                        modifier = Modifier.testTag("open_auth_dialog_button")
                    ) {
                        Icon(
                            imageVector = if (authState is AuthState.SignedIn) Icons.Default.AccountCircle else Icons.Default.Lock,
                            contentDescription = "Security Auth Clearance",
                            tint = if (authState is AuthState.SignedIn) AegisGoldPrimary else AegisTextSecondary
                        )
                    }

                    IconButton(
                        onClick = onOpenTasks,
                        modifier = Modifier.testTag("open_tasks_button")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = AegisGoldPrimary) {
                                    Text("Tasks", color = Color.Black)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = "Executive Tasks",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenAuditLogs,
                        modifier = Modifier.testTag("open_audit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Security Audit Logs",
                            tint = AegisSecurityGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Security Shield Active Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AegisBorderDark)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulsingStatusDot(color = AegisSecurityGreen, sizeDp = 8)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Security Shield: ACTIVE",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = AegisSecurityGreen
                    )
                }

                Text(
                    text = lastSecurityStatus ?: "Parallel Sources Armed",
                    style = MaterialTheme.typography.labelSmall,
                    color = AegisTextSecondary
                )
            }
        }
    }
}

@Composable
fun DomainSelectorBar(
    selectedDomain: AegisDomain,
    onSelectDomain: (AegisDomain) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(AegisDomain.entries.toTypedArray()) { domain ->
            val isSelected = domain == selectedDomain
            FilterChip(
                selected = isSelected,
                onClick = { onSelectDomain(domain) },
                label = {
                    Text(
                        text = domain.displayName,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = getDomainIcon(domain),
                        contentDescription = domain.displayName,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AegisGoldContainer,
                    selectedLabelColor = AegisGoldPrimary,
                    selectedLeadingIconColor = AegisGoldPrimary,
                    containerColor = AegisSurfaceDark,
                    labelColor = AegisTextSecondary,
                    iconColor = AegisTextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = AegisBorderDark,
                    selectedBorderColor = AegisGoldPrimary
                ),
                modifier = Modifier.testTag("domain_chip_${domain.name}")
            )
        }
    }
}

fun getDomainIcon(domain: AegisDomain): ImageVector {
    return when (domain) {
        AegisDomain.SECURITY -> Icons.Default.Shield
        AegisDomain.DATA_ANALYSIS -> Icons.Default.Analytics
        AegisDomain.MATH_SCIENCE -> Icons.Default.Calculate
        AegisDomain.ART_CREATIVE -> Icons.Default.Palette
        AegisDomain.SALES_ENTERPRISE -> Icons.AutoMirrored.Filled.TrendingUp
        AegisDomain.HEALTH_WELLNESS -> Icons.Default.MedicalServices
        AegisDomain.EXECUTIVE -> Icons.Default.Schedule
    }
}

@Composable
fun ChatMessageItem(message: ConversationMessageEntity) {
    val isUser = message.sender == "USER"
    val domain = runCatching { AegisDomain.valueOf(message.domain) }.getOrDefault(AegisDomain.SECURITY)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = getDomainIcon(domain),
                    contentDescription = null,
                    tint = AegisCyanAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AEGIS • ${domain.displayName}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AegisCyanAccent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = AegisSurfaceVariantDark,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Confidence: ${(message.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = AegisGoldPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) MaterialTheme.colorScheme.primaryContainer
                    else AegisSurfaceDark
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) AegisGoldPrimary else AegisBorderDark,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                if (isUser) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    AegisMarkdownText(
                        markdown = message.content,
                        textColor = AegisTextPrimary
                    )
                }

                if (!isUser && message.sourcesUsedJson != "[]" && message.sourcesUsedJson.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = AegisBorderDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Sources",
                            tint = AegisSecurityGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sources: ${message.sourcesUsedJson}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AegisTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QueryInputField(
    queryText: String,
    onQueryChanged: (String) -> Unit,
    onSubmit: (String) -> Unit,
    isProcessing: Boolean
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                val newQuery = if (queryText.isBlank()) spokenText else "$queryText $spokenText"
                onQueryChanged(newQuery)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchVoiceInput(context, speechLauncher) { isListening = true }
        } else {
            Toast.makeText(context, "Microphone permission is required for voice input.", Toast.LENGTH_SHORT).show()
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (isListening) {
                AudioWaveformVisualizer(
                    isListening = true,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
            // Voice Input / Microphone Button
            IconButton(
                onClick = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        launchVoiceInput(context, speechLauncher) { isListening = true }
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isListening) AegisAlertRed else AegisSurfaceDark)
                    .border(1.dp, if (isListening) AegisAlertRed else AegisBorderDark, CircleShape)
                    .testTag("voice_input_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice Prompt Input",
                    tint = if (isListening) Color.White else AegisCyanAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = queryText,
                onValueChange = onQueryChanged,
                placeholder = {
                    Text(
                        if (isListening) "Listening to voice directive..." else "Ask AEGIS anything (Security, Sales, Health, Math...)",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("query_input_field"),
                singleLine = false,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (queryText.isNotBlank() && !isProcessing) {
                        onSubmit(queryText)
                    }
                }),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AegisGoldPrimary,
                    unfocusedBorderColor = AegisBorderDark,
                    focusedContainerColor = AegisSurfaceDark,
                    unfocusedContainerColor = AegisSurfaceDark
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (queryText.isNotBlank() && !isProcessing) {
                        onSubmit(queryText)
                    }
                },
                enabled = queryText.isNotBlank() && !isProcessing,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (queryText.isNotBlank() && !isProcessing) AegisGoldPrimary else AegisBorderDark
                    )
                    .testTag("submit_query_button")
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Submit Query",
                        tint = if (queryText.isNotBlank()) Color.Black else AegisTextMuted
                    )
                }
            }
        }
    }
}
}

private fun launchVoiceInput(
    context: android.content.Context,
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>,
    onStarted: () -> Unit
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your directive to AEGIS...")
    }
    try {
        onStarted()
        launcher.launch(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Voice recognition service is not available on this device.", Toast.LENGTH_SHORT).show()
    }
}
