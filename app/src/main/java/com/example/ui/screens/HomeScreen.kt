package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppScreen
import com.example.data.model.QuickAction
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.util.TimeGreetingHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val greeting = remember { TimeGreetingHelper.getGreeting() }
    val userPref by viewModel.userPreferences.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val chatInput by viewModel.chatInput.collectAsState()
    val attachedBitmap by viewModel.attachedBitmap.collectAsState()

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel.setAttachedBitmap(bitmap)
            } catch (e: Exception) {
                // Handled gracefully
            }
        }
    }

    val quickActions = remember {
        listOf(
            QuickAction("ask", "اسألني", "🤖", AppScreen.CHAT, "إجابات ذكية وسريعة"),
            QuickAction("photo", "تعديل صورة", "📸", AppScreen.PHOTO_EDITOR, "تحسين وتفريغ واحترافية"),
            QuickAction("poster", "صمم بوستر", "🎨", AppScreen.POSTER_GENERATOR, "إعلانات وعروض وتصميمات"),
            QuickAction("write", "اكتب بوست", "✍️", AppScreen.CONTENT_WRITER, "سوشيال ميديا وإيميلات"),
            QuickAction("summarize", "لخص ملف", "📄", AppScreen.WORK_ASSISTANT, "ملفات PDF وExcel وWord"),
            QuickAction("plan", "نظم يومي", "📅", AppScreen.MY_DAY, "جدول ذكي لمهام اليوم"),
            QuickAction("tasks", "مهامي", "✅", AppScreen.MY_DAY, "إدارة الأولويات والإنجاز"),
            QuickAction("ideas", "أفكار", "💡", AppScreen.CHAT, "عصف ذهني للمشاريع", initialPrompt = "اقترح علي 5 أفكار مشاريع ومحتوى إبداعي ومربح لعام 2026"),
            QuickAction("data", "تحليل بيانات", "📊", AppScreen.WORK_ASSISTANT, "أرقام وجداول وتقارير"),
            QuickAction("search", "ابحث لي", "🌐", AppScreen.CHAT, "بحث وتوثيق مصادر", initialPrompt = "ابحث لي عن أحدث التطورات والمعلومات الدقيقة حول: "),
            QuickAction("calc", "احسب لي", "🧮", AppScreen.CHAT, "حسابات دقيقة وأرباح", initialPrompt = "احسب لي بدقة مع الخطوات والتحقق: ")
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Time-based Greeting & Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("header_greeting_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(PrimaryIndigoDark.copy(alpha = 0.4f), SecondaryCyanDark.copy(alpha = 0.3f))
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = greeting.first,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "أنا مساعدك الذكي k&m Ai، قول لي محتاج إيه؟",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(SecondaryCyanDark, PrimaryIndigoDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "k&m",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Slogan Tag
                    Text(
                        text = "« قول لي محتاج إيه... وأنا أساعدك »",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 2. Large Central Conversation Input Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_prompt_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "مساعدك الشخصي في خدمتك",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { viewModel.setChatInput(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 96.dp)
                            .testTag("home_prompt_input_field"),
                        placeholder = {
                            Text(
                                text = "اكتب سؤالك أو اطلب مني أي حاجة في شغلك أو دراستك أو صورك...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    )

                    // Attached preview if any
                    if (attachedBitmap != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "صورة مرفقة",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "تم إرفاق صورة جاهزة للتحليل",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { viewModel.setAttachedBitmap(null) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "إزالة الصورة",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Action Buttons Row: Mic, Photo, File, Send
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Mic Button
                            FilledTonalIconButton(
                                onClick = {
                                    viewModel.navigateTo(AppScreen.CHAT)
                                    viewModel.setChatInput("أهلاً، أنا أتحدث معك بصوتي: ")
                                },
                                modifier = Modifier.testTag("action_voice_mic_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "محادثة صوتية",
                                    tint = PrimaryIndigoDark
                                )
                            }

                            // Photo Upload
                            FilledTonalIconButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier.testTag("action_upload_photo_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "رفع صورة",
                                    tint = SecondaryCyanDark
                                )
                            }

                            // File Upload
                            FilledTonalIconButton(
                                onClick = { viewModel.navigateTo(AppScreen.WORK_ASSISTANT) },
                                modifier = Modifier.testTag("action_upload_file_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.UploadFile,
                                    contentDescription = "رفع ملف",
                                    tint = AccentPurpleDark
                                )
                            }
                        }

                        // Send Button
                        Button(
                            onClick = {
                                if (chatInput.isNotBlank() || attachedBitmap != null) {
                                    val text = chatInput
                                    viewModel.navigateTo(AppScreen.CHAT)
                                    viewModel.sendChatMessage(text)
                                } else {
                                    viewModel.navigateTo(AppScreen.CHAT)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("home_send_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("إرسال", fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "إرسال",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Shortcuts / Quick Actions Section Header
        item {
            Text(
                text = "الخدمات والاختصارات الذكية",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Flow layout for Quick Action chips
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickActions.forEach { action ->
                    Surface(
                        onClick = {
                            viewModel.navigateTo(action.screen)
                            if (action.initialPrompt.isNotBlank()) {
                                viewModel.setChatInput(action.initialPrompt)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.testTag("quick_action_${action.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = action.iconEmoji, fontSize = 16.sp)
                            Text(
                                text = action.title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 4. Tasks & Daily Glance
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_tasks_preview_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "📅", fontSize = 18.sp)
                            Text(
                                text = "مهامي اليوم (My Day)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(
                            onClick = { viewModel.navigateTo(AppScreen.MY_DAY) },
                            modifier = Modifier.testTag("view_all_tasks_button")
                        ) {
                            Text("عرض الكل", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    val pendingTasks = tasks.filter { !it.isCompleted }.take(3)
                    if (pendingTasks.isEmpty()) {
                        Text(
                            text = "لا توجد مهام حالياً. اضغط لإضافة مهمة وسيقوم الذكاء الاصطناعي بجدولتها!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        pendingTasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Checkbox(
                                        checked = task.isCompleted,
                                        onCheckedChange = { viewModel.toggleTask(task.id, it) },
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (task.priority) {
                                        "urgent" -> ErrorRose.copy(alpha = 0.2f)
                                        "important" -> WarningAmber.copy(alpha = 0.2f)
                                        else -> InfoBlue.copy(alpha = 0.2f)
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
                                        color = when (task.priority) {
                                            "urgent" -> ErrorRose
                                            "important" -> WarningAmber
                                            else -> InfoBlue
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.navigateTo(AppScreen.MY_DAY)
                                viewModel.planMyDay()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_quick_plan_my_day_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryIndigoDark
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("نظم لي يومي بالذكاء الاصطناعي 🚀", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5. App Designer & Info Banner
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "✨", fontSize = 12.sp)
                        Text(
                            text = "مصمم ومطور التطبيق: كريم شعبان",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
