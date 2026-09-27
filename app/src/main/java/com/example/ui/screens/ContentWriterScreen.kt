package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContentFormat
import com.example.data.model.Tone
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ContentWriterScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val selectedFormat by viewModel.selectedFormat.collectAsState()
    val selectedTone by viewModel.selectedTone.collectAsState()
    val topicInput by viewModel.contentTopicInput.collectAsState()
    val generatedContent by viewModel.generatedContent.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "✍️ كاتب المحتوى الذكي (AI Copywriter)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "اكتب بوستات سوشيال ميديا وإيميلات وسكريبتات بأي لهجة وأسلوب تريده",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Format Selector
        item {
            Text(
                text = "نوع المحتوى المطلوب:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ContentFormat.values()) { format ->
                    val isSelected = selectedFormat == format
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setContentFormat(format) },
                        label = { Text(format.titleAr, fontSize = 12.sp) },
                        modifier = Modifier.testTag("content_format_${format.id}")
                    )
                }
            }
        }

        // Tone Selector
        item {
            Text(
                text = "أسلوب ونبرة الصوت (Tone of Voice):",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Tone.values()) { tone ->
                    val isSelected = selectedTone == tone
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setContentTone(tone) },
                        label = { Text(tone.titleAr, fontSize = 12.sp) },
                        modifier = Modifier.testTag("content_tone_${tone.id}")
                    )
                }
            }
        }

        // Topic / Details Input
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "عن ماذا تريد أن تكتب؟",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { viewModel.setContentTopic(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp)
                            .testTag("content_topic_input"),
                        placeholder = {
                            Text("اكتب فكرة سريعة أو نقاط رئيسية، مثلاً: إعلان عن خدمة صيانة منزلية سريعة بأسعار منافسة...")
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = { viewModel.generateContent() },
                        enabled = topicInput.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_content_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جاري الكتابة بالذكاء الاصطناعي...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("كتابة المحتوى الآن ✨", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Generated Output Card
        if (generatedContent.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generated_content_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
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
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                Text(
                                    text = "المحتوى الجاهز للنشر:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Generated Content", generatedContent))
                                        Toast.makeText(context, "تم نسخ المحتوى بنجاح!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("copy_content_button")
                                ) {
                                    Icon(Icons.Outlined.ContentCopy, contentDescription = "نسخ النص")
                                }
                            }
                        }

                        HorizontalDivider()

                        SelectionContainer {
                            Text(
                                text = generatedContent,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionContainer(content: @Composable () -> Unit) {
    androidx.compose.foundation.text.selection.SelectionContainer {
        content()
    }
}
