package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.AuditLogEntity
import com.example.data.ExecutiveTaskEntity
import com.example.ui.theme.*

@Composable
fun SecurityAuditDialog(
    auditLogs: List<AuditLogEntity>,
    onDismiss: () -> Unit,
    onClearLogs: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AegisSurfaceDark,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Security & Source Audit Log",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AegisSecurityGreen
                    )
                    IconButton(onClick = onDismiss) {
                        Text("X", color = AegisTextSecondary)
                    }
                }

                Text(
                    text = "Every query is screened for threats, PII-redacted, and traced across parallel source calls.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AegisTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(auditLogs) { log ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AegisSurfaceVariantDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        color = if (log.securityStatus.contains("BLOCKED")) AegisAlertRed else AegisSecurityGreen,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = log.securityStatus,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = log.domain,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AegisGoldPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Query: ${log.query}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = AegisTextPrimary
                                )
                                Text(
                                    text = "Sanitized: ${log.sanitizedQuery}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AegisTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Parallel Sources: ${log.parallelSourcesCalled}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AegisCyanAccent
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onClearLogs) {
                        Text("Clear Logs", color = AegisAlertRed)
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = AegisGoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun ExecutiveTasksDialog(
    tasks: List<ExecutiveTaskEntity>,
    onDismiss: () -> Unit,
    onAddTask: (String, String, String, String) -> Unit,
    onToggleTask: (ExecutiveTaskEntity) -> Unit,
    onDeleteTask: (Long) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AegisSurfaceDark,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(4.dp)
        ) {
            TaskDashboardContent(
                tasks = tasks,
                onAddTask = onAddTask,
                onToggleTask = onToggleTask,
                onDeleteTask = onDeleteTask,
                onDismiss = onDismiss
            )
        }
    }
}
