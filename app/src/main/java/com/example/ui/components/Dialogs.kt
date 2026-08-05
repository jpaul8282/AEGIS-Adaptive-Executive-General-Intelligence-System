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
    var showAddForm by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var taskCategory by remember { mutableStateOf("SECURITY") }
    var taskPriority by remember { mutableStateOf("HIGH") }
    var taskDueDate by remember { mutableStateOf("Today") }

    val categories = listOf("SECURITY", "DATA", "SALES", "HEALTH", "MATH", "EXECUTIVE")
    val priorities = listOf("URGENT", "HIGH", "MEDIUM", "LOW")
    val dueDates = listOf("Today", "Tomorrow", "This Week", "Next Week")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AegisSurfaceDark,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(12.dp)
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
                    Column {
                        Text(
                            text = "Executive Items & Directives",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = AegisGoldPrimary
                        )
                        Text(
                            text = "${tasks.count { !it.isCompleted }} Active Items Pending",
                            style = MaterialTheme.typography.labelSmall,
                            color = AegisCyanAccent
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Text("X", color = AegisTextSecondary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (!showAddForm) {
                    Button(
                        onClick = { showAddForm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AegisGoldPrimary, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_task_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Executive Item Needed", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AegisSurfaceVariantDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "New Executive Directive Item",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = AegisGoldPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = taskTitle,
                                onValueChange = { taskTitle = it },
                                label = { Text("Item Title / Description") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Category:", style = MaterialTheme.typography.labelSmall, color = AegisTextSecondary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                categories.take(4).forEach { cat ->
                                    FilterChip(
                                        selected = taskCategory == cat,
                                        onClick = { taskCategory = cat },
                                        label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Priority:", style = MaterialTheme.typography.labelSmall, color = AegisTextSecondary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                priorities.forEach { prio ->
                                    FilterChip(
                                        selected = taskPriority == prio,
                                        onClick = { taskPriority = prio },
                                        label = { Text(prio, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { showAddForm = false }) {
                                    Text("Cancel", color = AegisTextSecondary)
                                }
                                Button(
                                    onClick = {
                                        if (taskTitle.isNotBlank()) {
                                            onAddTask(taskTitle, taskCategory, taskPriority, taskDueDate)
                                            taskTitle = ""
                                            showAddForm = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AegisCyanAccent, contentColor = Color.Black)
                                ) {
                                    Text("Save Item")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No active executive items. Tap above to add items you need.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AegisTextSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tasks) { task ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (task.isCompleted) AegisBorderDark else AegisSurfaceVariantDark
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        IconButton(
                                            onClick = { onToggleTask(task) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = "Toggle completion",
                                                tint = if (task.isCompleted) AegisSecurityGreen else AegisGoldPrimary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = task.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = if (task.isCompleted) AegisTextMuted else AegisTextPrimary
                                            )
                                            Text(
                                                text = "${task.category} • ${task.priority} • Due: ${task.dueDate}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AegisCyanAccent
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteTask(task.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Item",
                                            tint = AegisAlertRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
