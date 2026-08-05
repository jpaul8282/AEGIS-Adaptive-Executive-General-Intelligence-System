package com.example.ui.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ExecutiveTaskEntity
import com.example.ui.theme.*
import com.example.viewmodel.AegisViewModel

enum class TaskFilter(val label: String) {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    ALL("All Tasks")
}

/**
 * TaskDashboard Screen Component
 * Fetches and manages active tasks from AegisViewModel to support adaptive executive task workflow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDashboard(
    viewModel: AegisViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    TaskDashboardContent(
        tasks = tasks,
        onAddTask = { title, category, priority, dueDate ->
            viewModel.addTask(title, category, priority, dueDate)
        },
        onToggleTask = { task ->
            viewModel.toggleTaskCompletion(task)
        },
        onDeleteTask = { taskId ->
            viewModel.deleteTask(taskId)
        },
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDashboardContent(
    tasks: List<ExecutiveTaskEntity>,
    onAddTask: (title: String, category: String, priority: String, dueDate: String) -> Unit,
    onToggleTask: (ExecutiveTaskEntity) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(TaskFilter.ACTIVE) }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddTaskModal by remember { mutableStateOf(false) }

    // Categories list extracted from existing tasks + defaults
    val categories = remember {
        listOf("ALL", "SECURITY", "DATA", "MATH", "ART", "SALES", "HEALTH", "EXECUTIVE")
    }

    // Filter logic
    val filteredTasks = remember(tasks, selectedFilter, selectedCategory, searchQuery) {
        tasks.filter { task ->
            val matchesFilter = when (selectedFilter) {
                TaskFilter.ACTIVE -> !task.isCompleted
                TaskFilter.COMPLETED -> task.isCompleted
                TaskFilter.ALL -> true
            }
            val matchesCategory = selectedCategory == "ALL" || task.category.contains(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() || task.title.contains(searchQuery, ignoreCase = true) || task.category.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesCategory && matchesSearch
        }
    }

    val activeCount = remember(tasks) { tasks.count { !it.isCompleted } }
    val completedCount = remember(tasks) { tasks.count { it.isCompleted } }
    val urgentCount = remember(tasks) { tasks.count { !it.isCompleted && (it.priority.equals("HIGH", true) || it.priority.equals("URGENT", true)) } }

    Surface(
        color = AegisSurfaceDark,
        modifier = modifier
            .fillMaxSize()
            .testTag("task_dashboard_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Dashboard Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Task,
                        contentDescription = "Executive Task Dashboard",
                        tint = AegisGoldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Executive Task Dashboard",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = AegisTextPrimary
                        )
                        Text(
                            text = "AEGIS Adaptive Workflow & Directive Tracker",
                            style = MaterialTheme.typography.labelSmall,
                            color = AegisCyanAccent
                        )
                    }
                }

                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("close_task_dashboard")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dashboard",
                            tint = AegisTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics Summary Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Active Directives",
                    value = activeCount.toString(),
                    color = AegisGoldPrimary,
                    icon = Icons.Default.PendingActions,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Urgent Focus",
                    value = urgentCount.toString(),
                    color = AegisAlertRed,
                    icon = Icons.Default.Warning,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Completed",
                    value = completedCount.toString(),
                    color = AegisSecurityGreen,
                    icon = Icons.Default.TaskAlt,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search input field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search directives by title or category...", color = AegisTextMuted) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = AegisCyanAccent)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Search", tint = AegisTextSecondary)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AegisGoldPrimary,
                    unfocusedBorderColor = AegisBorderDark,
                    focusedContainerColor = AegisSurfaceVariantDark,
                    unfocusedContainerColor = AegisSurfaceDark,
                    focusedTextColor = AegisTextPrimary,
                    unfocusedTextColor = AegisTextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Status Filter Segmented Chips (Active, Completed, All)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilter.values().forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AegisGoldPrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = AegisSurfaceVariantDark,
                            labelColor = AegisTextPrimary
                        ),
                        modifier = Modifier.testTag("filter_status_${filter.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Scrollable Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AegisCyanAccent,
                            selectedLabelColor = Color.Black,
                            containerColor = AegisSurfaceVariantDark,
                            labelColor = AegisTextSecondary
                        ),
                        modifier = Modifier.testTag("filter_category_$category")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add Task Header Button / FAB
            Button(
                onClick = { showAddTaskModal = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AegisGoldPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_new_task_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Executive Task Needed", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Task List
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = "No tasks",
                            tint = AegisTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No matching tasks found." else "No active directives registered.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AegisTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'Add Executive Task Needed' above to create one.",
                            style = MaterialTheme.typography.labelSmall,
                            color = AegisTextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskDashboardItemCard(
                            task = task,
                            onToggleCompletion = { onToggleTask(task) },
                            onDelete = { onDeleteTask(task.id) }
                        )
                    }
                }
            }
        }

        // Add Task Dialog Modal
        if (showAddTaskModal) {
            AddTaskModalDialog(
                onDismiss = { showAddTaskModal = false },
                onConfirmAdd = { title, cat, prio, due ->
                    onAddTask(title, cat, prio, due)
                    showAddTaskModal = false
                }
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurfaceVariantDark),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorderDark),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = AegisTextSecondary
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
fun TaskDashboardItemCard(
    task: ExecutiveTaskEntity,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) AegisBorderDark.copy(alpha = 0.4f) else AegisSurfaceVariantDark
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.isCompleted) AegisBorderDark else AegisCyanAccent.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}")
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
                    onClick = onToggleCompletion,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("task_checkbox_${task.id}")
                ) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (task.isCompleted) "Mark Incomplete" else "Mark Complete",
                        tint = if (task.isCompleted) AegisSecurityGreen else AegisGoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (task.isCompleted) AegisTextMuted else AegisTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AegisSurfaceDark)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.category,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = AegisCyanAccent
                            )
                        }

                        // Priority Badge
                        val priorityColor = when (task.priority.uppercase()) {
                            "URGENT", "HIGH" -> AegisAlertRed
                            "MEDIUM" -> AegisGoldPrimary
                            else -> AegisTextSecondary
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(priorityColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.priority.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = priorityColor
                            )
                        }

                        // Due date
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AegisTextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = task.dueDate,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = AegisTextMuted
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("delete_task_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = AegisAlertRed.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AddTaskModalDialog(
    onDismiss: () -> Unit,
    onConfirmAdd: (title: String, category: String, priority: String, dueDate: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("SECURITY") }
    var selectedPriority by remember { mutableStateOf("HIGH") }
    var selectedDueDate by remember { mutableStateOf("Today") }

    val categories = listOf("SECURITY", "DATA", "MATH", "ART", "SALES", "HEALTH", "EXECUTIVE")
    val priorities = listOf("URGENT", "HIGH", "MEDIUM", "LOW")
    val dueDates = listOf("Today", "Tomorrow", "This Week", "Next Week")

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AegisSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisGoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_task_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create Executive Task Directive",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AegisGoldPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = AegisTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Directive / Description") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AegisGoldPrimary,
                        unfocusedBorderColor = AegisBorderDark,
                        focusedTextColor = AegisTextPrimary,
                        unfocusedTextColor = AegisTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_task_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Category Domain:", style = MaterialTheme.typography.labelSmall, color = AegisTextSecondary)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AegisCyanAccent,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Priority Level:", style = MaterialTheme.typography.labelSmall, color = AegisTextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    priorities.forEach { prio ->
                        FilterChip(
                            selected = selectedPriority == prio,
                            onClick = { selectedPriority = prio },
                            label = { Text(prio, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AegisGoldPrimary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Due Date:", style = MaterialTheme.typography.labelSmall, color = AegisTextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    dueDates.forEach { dateStr ->
                        FilterChip(
                            selected = selectedDueDate == dateStr,
                            onClick = { selectedDueDate = dateStr },
                            label = { Text(dateStr, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AegisSurfaceVariantDark,
                                selectedLabelColor = AegisTextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = AegisTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirmAdd(title.trim(), selectedCategory, selectedPriority, selectedDueDate)
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AegisGoldPrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("save_task_submit_button")
                    ) {
                        Text("Save Task Directive", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
