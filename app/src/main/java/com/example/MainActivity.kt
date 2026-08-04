package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AegisViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AegisTheme {
                AegisMainScreen()
            }
        }
    }
}

@Composable
fun AegisMainScreen(
    viewModel: AegisViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val selectedDomain by viewModel.selectedDomain.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val lastSecurityStatus by viewModel.lastSecurityStatus.collectAsStateWithLifecycle()
    val queryText by viewModel.currentQueryText.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val currentSessionId by viewModel.currentSessionId.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()

    var showAuditDialog by remember { mutableStateOf(false) }
    var showTasksDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showSessionsDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToBottom by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex < messages.size - 3
        }
    }

    // Auto-scroll chat to latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("aegis_main_scaffold"),
        topBar = {
            Column {
                AegisHeader(
                    selectedDomain = selectedDomain,
                    lastSecurityStatus = lastSecurityStatus,
                    authState = authState,
                    onOpenAuthDialog = { showAuthDialog = true },
                    onOpenSessions = { showSessionsDialog = true },
                    onOpenAuditLogs = {
                        showAuditDialog = true
                    },
                    onOpenTasks = {
                        showTasksDialog = true
                    }
                )
                DomainSelectorBar(
                    selectedDomain = selectedDomain,
                    onSelectDomain = { viewModel.selectDomain(it) }
                )
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                QuickPromptSuggestions(
                    selectedDomain = selectedDomain,
                    onPromptSelected = { prompt ->
                        viewModel.updateQueryText(prompt)
                    }
                )
                QueryInputField(
                    queryText = queryText,
                    onQueryChanged = { viewModel.updateQueryText(it) },
                    onSubmit = { viewModel.submitQuery(it) },
                    isProcessing = isProcessing
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showScrollToBottom && messages.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    },
                    containerColor = AegisGoldPrimary,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .testTag("scroll_to_bottom_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Scroll to latest message"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AegisBackgroundCanvas()

            Column(modifier = Modifier.fillMaxSize()) {
                NeuralProcessingIndicator(isProcessing = isProcessing)

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
                ) {
                    items(messages) { message ->
                        ChatMessageItem(message = message)
                    }
                }
            }
        }

        if (showSessionsDialog) {
            ChatSessionsDialog(
                currentSessionId = currentSessionId,
                sessions = sessions,
                onDismiss = { showSessionsDialog = false },
                onSelectSession = { viewModel.selectSession(it) },
                onCreateNewSession = { viewModel.createNewSession(it) },
                onDeleteSession = { viewModel.deleteSession(it) }
            )
        }

        if (showAuthDialog) {
            AegisAuthDialog(
                authState = authState,
                onDismiss = { showAuthDialog = false },
                onSignInWithGoogle = { viewModel.signInWithGoogle() },
                onSignInWithEmail = { email, pass -> viewModel.signInWithEmail(email, pass) },
                onSignInAnonymously = { viewModel.signInAnonymously() },
                onSignOut = { viewModel.signOut() }
            )
        }

        if (showAuditDialog) {
            SecurityAuditDialog(
                auditLogs = auditLogs,
                onDismiss = { showAuditDialog = false },
                onClearLogs = { viewModel.clearAuditLogs() }
            )
        }

        if (showTasksDialog) {
            ExecutiveTasksDialog(
                tasks = tasks,
                onDismiss = { showTasksDialog = false },
                onAddTask = { title, cat, prio, due ->
                    viewModel.addTask(title, cat, prio, due)
                },
                onToggleTask = { viewModel.toggleTaskCompletion(it) },
                onDeleteTask = { viewModel.deleteTask(it) }
            )
        }
    }
}
