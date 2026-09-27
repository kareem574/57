package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ChatMessageEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages by viewModel.chatMessages.collectAsState()
    val chatInput by viewModel.chatInput.collectAsState()
    val attachedBitmap by viewModel.attachedBitmap.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()

    // Scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

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

    val samplePrompts = listOf(
        "عندي اجتماع بكرة ومش عارف أجهز إيه",
        "احسب لي تكلفة وأرباح مشروع تجاري",
        "اكتب لي خطة دراسية مكثفة لأسبوعين",
        "ابحث لي عن أحدث أخبار وتطورات اليوم"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Model Selection Bar
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "محرك الذكاء الاصطناعي:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedModel == "gemini-3.5-flash",
                        onClick = { viewModel.setSelectedModel("gemini-3.5-flash") },
                        label = { Text("سريع (Flash)", fontSize = 11.sp) },
                        modifier = Modifier.testTag("model_flash_chip")
                    )
                    FilterChip(
                        selected = selectedModel == "gemini-3.1-pro-preview",
                        onClick = { viewModel.setSelectedModel("gemini-3.1-pro-preview") },
                        label = { Text("استدلال عميق (Pro)", fontSize = 11.sp) },
                        modifier = Modifier.testTag("model_pro_chip")
                    )
                }
            }
        }

        // Chat Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🤖", fontSize = 28.sp)
                            }
                        }
                        Text(
                            text = "مرحباً بك في المحادثة الذكية!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "اسألني عن أي موضوع في شغلك أو دراستك أو خططك، وسأجيبك بنقاط عملية ومباشرة.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        // Sample prompts
                        Text(
                            text = "اقتراحات سريعة للبدء:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        samplePrompts.forEach { prompt ->
                            SuggestionChip(
                                onClick = { viewModel.sendChatMessage(prompt) },
                                label = { Text(prompt, fontSize = 12.sp) },
                                modifier = Modifier.testTag("suggestion_chip")
                            )
                        }
                    }
                }
            } else {
                items(messages, key = { it.id }) { msg ->
                    ChatBubbleItem(
                        message = msg,
                        onSpeak = { viewModel.speakText(msg.text) },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("k&m Ai Message", msg.text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ النص!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "المساعد الذكي يفكر ويجهز الإجابة...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Attached Bitmap Preview Banner
        if (attachedBitmap != null) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = PrimaryIndigoDark)
                        Text("تم إرفاق صورة مع الرسالة", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { viewModel.setAttachedBitmap(null) }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "حذف المرفق", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Input Field Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Attach image
                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.testTag("chat_attach_image_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddPhotoAlternate,
                        contentDescription = "إرفاق صورة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Mic
                IconButton(
                    onClick = {
                        viewModel.setChatInput(chatInput + " ")
                        Toast.makeText(context, "تحدث الآن...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("chat_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "تسجيل صوتي",
                        tint = PrimaryIndigoDark
                    )
                }

                // Text Field
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { viewModel.setChatInput(it) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text_field"),
                    placeholder = { Text("اكتب رسالتك للمساعد...", fontSize = 13.sp) },
                    maxLines = 4,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                // Send Button
                FilledIconButton(
                    onClick = { viewModel.sendChatMessage() },
                    enabled = (chatInput.isNotBlank() || attachedBitmap != null) && !isLoading,
                    modifier = Modifier.testTag("chat_send_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "إرسال",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(
    message: ChatMessageEntity,
    onSpeak: () -> Unit,
    onCopy: () -> Unit
) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            ),
            border = if (!isUser) CardDefaults.outlinedCardBorder() else null,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Header if assistant
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🤖", fontSize = 14.sp)
                            Text(
                                text = "k&m Ai",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row {
                            IconButton(onClick = onSpeak, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Outlined.VolumeUp,
                                    contentDescription = "استماع صوتي",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "نسخ",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 6.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }

                // Message text
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
