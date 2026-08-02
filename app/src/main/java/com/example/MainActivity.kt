package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.AegisTheme
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

    var showAuditDialog by remember { mutableStateOf(false) }
    var showTasksDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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
                    onOpenAuditLogs = { showAuditDialog = true },
                    onOpenTasks = { showTasksDialog = true }
                )
                DomainSelectorBar(
                    selectedDomain = selectedDomain,
                    onSelectDomain = { viewModel.selectDomain(it) }
                )
            }
        },
        bottomBar = {
            QueryInputField(
                queryText = queryText,
                onQueryChanged = { viewModel.updateQueryText(it) },
                onSubmit = { viewModel.submitQuery(it) },
                isProcessing = isProcessing
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
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
