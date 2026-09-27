package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.TaskEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TasksScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val tasks by viewModel.tasks.collectAsState()
    val aiDailyPlan by viewModel.aiDailyPlan.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskPriority by remember { mutableStateOf("normal") }
    var newTaskDueTime by remember { mutableStateOf("") }

    val completedCount = tasks.count { it.isCompleted }
    val pendingCount = tasks.count { !it.isCompleted }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "إضافة مهمة") },
                text = { Text("مهمة جديدة") },
                modifier = Modifier.testTag("add_task_fab"),
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header & AI Optimizer Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "📅 يومي المنظم (My Day)",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$pendingCount متبقية | $completedCount تم إنجازها",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (completedCount > 0) {
                                TextButton(onClick = { viewModel.clearCompletedTasks() }) {
                                    Text("مسح المكتمل", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.planMyDay() },
                            enabled = !isLoading && pendingCount > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("plan_my_day_ai_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigoDark),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري تحليل المهام وبناء الجدول...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تنظيم وترتيب يومي بذكاء k&m Ai 🚀", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Tasks List
            if (tasks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🎉", fontSize = 40.sp)
                        Text(
                            text = "لا توجد مهام حالياً!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "اضغط على زر '+' بالأسفل لإضافة مهامك اليومية وسيقوم المساعد الذكي بتنظيمها لك.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(tasks, key = { it.id }) { task ->
                    TaskRowCard(
                        task = task,
                        onToggle = { viewModel.toggleTask(task.id, it) },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة مهمة جديدة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("عنوان المهمة") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_task_title_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = newTaskDueTime,
                        onValueChange = { newTaskDueTime = it },
                        label = { Text("الموعد / الساعة (اختياري)") },
                        placeholder = { Text("مثال: 02:00 م أو بعد الظهر") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Text(text = "مستوى الأولوية:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("urgent", "عاجل", ErrorRose),
                            Triple("important", "مهم", WarningAmber),
                            Triple("normal", "عادي", InfoBlue)
                        ).forEach { (id, label, color) ->
                            FilterChip(
                                selected = newTaskPriority == id,
                                onClick = { newTaskPriority = id },
                                label = { Text(label, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            viewModel.addTask(
                                title = newTaskTitle,
                                priority = newTaskPriority,
                                dueTime = newTaskDueTime
                            )
                            newTaskTitle = ""
                            newTaskDueTime = ""
                            newTaskPriority = "normal"
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_task_button")
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // AI Daily Plan Dialog
    if (aiDailyPlan != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDailyPlanDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "✨", fontSize = 18.sp)
                    Text("جدول يومك المقترح من k&m Ai", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Text(
                            text = aiDailyPlan!!,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.dismissDailyPlanDialog() }) {
                    Text("ممتاز، سأبدأ الآن!")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
fun TaskRowCard(
    task: TaskEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
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
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onToggle,
                    modifier = Modifier.testTag("task_checkbox_${task.id}")
                )
                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    if (task.dueTime.isNotBlank()) {
                        Text(
                            text = "⏰ ${task.dueTime}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (task.priority) {
                        "urgent" -> ErrorRose.copy(alpha = 0.15f)
                        "important" -> WarningAmber.copy(alpha = 0.15f)
                        else -> InfoBlue.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = when (task.priority) {
                            "urgent" -> "عاجل"
                            "important" -> "مهم"
                            else -> "عادي"
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (task.priority) {
                            "urgent" -> ErrorRose
                            "important" -> WarningAmber
                            else -> InfoBlue
                        }
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "حذف المهمة",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
