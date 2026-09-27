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
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun WorkAssistantScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val fileText by viewModel.fileContentText.collectAsState()
    val analysisResult by viewModel.fileAnalysisResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val quickDataActions = listOf(
        Pair("لخص المحتوى", "لخص هذا الملف أو النص في أهم 5 نقاط رئيسية وملاحظات تنفيذية."),
        Pair("استخرج الأرقام", "استخرج كافة الأرقام والإحصائيات والنسب المئوية والتواريخ في جدول منظم."),
        Pair("اعمل جدول", "حول هذه البيانات إلى جدول منسق ومنظم بدقة."),
        Pair("ما أهم المشاكل؟", "حدد أهم المشاكل أو الثغرات والتحديات الموجودة في هذا التقرير واقترح حلولاً عملية."),
        Pair("حلل البيانات", "حلل هذه البيانات بعمق وقدّم رؤى واستنتاجات تفيد اتخاذ القرار."),
        Pair("اعمل تقرير", "اكتب تقريراً إدارياً شاملاً ومهنياً بناءً على هذه البيانات."),
        Pair("حساب الأرباح والرواتب", "احسب التكاليف والأرباح والرواتب وصافي الدخل بناءً على هذه المعطيات."),
        Pair("إعداد الاجتماع", "جهّز جدول أعمال كامل للاجتماع بناءً على هذا الموضوع والقرارات المطلوبة.")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "💼 مساعد العمل وتحليل الملفات",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "ألصق بياناتك أو نصوص ملفاتك (PDF, Excel, Word, CSV) واختر العملية المطلوبة",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Data / File Input Box
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "نص الملف أو البيانات:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            TextButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val item = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                                    if (!item.isNullOrBlank()) {
                                        viewModel.setFileContentText(item)
                                        Toast.makeText(context, "تم لصق البيانات!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("لصق", fontSize = 12.sp)
                            }
                            if (fileText.isNotBlank()) {
                                TextButton(onClick = { viewModel.setFileContentText("") }) {
                                    Text("مسح", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = fileText,
                        onValueChange = { viewModel.setFileContentText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp)
                            .testTag("file_content_input_field"),
                        placeholder = {
                            Text(
                                "ألصق هنا نصوص تقرير، جدول أرقام، أسعار، ملاحظات اجتماع، أو محتوى ملف تريد تحليله...",
                                fontSize = 13.sp
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Quick Analysis Actions
        item {
            Text(
                text = "اختر الإجراء المطلوب بالذكاء الاصطناعي:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                quickDataActions.chunked(2).forEach { rowActions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowActions.forEach { (label, actionPrompt) ->
                            Button(
                                onClick = { viewModel.analyzeFileAction(actionPrompt) },
                                enabled = fileText.isNotBlank() && !isLoading,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("work_action_${label}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = label,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Loading state
        if (isLoading) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("جاري استخراج وتحليل البيانات بواسطة k&m Ai...")
                }
            }
        }

        // Analysis Result Card
        if (analysisResult.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("work_analysis_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                    text = "نتيجة التحليل الذكي:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Analysis Result", analysisResult))
                                    Toast.makeText(context, "تم نسخ النتيجة!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Outlined.ContentCopy, contentDescription = "نسخ")
                            }
                        }

                        HorizontalDivider()

                        androidx.compose.foundation.text.selection.SelectionContainer {
                            Text(
                                text = analysisResult,
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
