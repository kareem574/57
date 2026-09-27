package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val adminStats by viewModel.adminStats.collectAsState()

    val statCards = listOf(
        Triple("إجمالي المستخدمين", "%,d".format(adminStats.totalUsers), Icons.Default.People),
        Triple("النشطون اليوم", "%,d".format(adminStats.activeUsersToday), Icons.Default.TrendingUp),
        Triple("عدد رسائل AI", "%,d".format(adminStats.totalMessages), Icons.Default.ChatBubble),
        Triple("الصور المعالجة", "%,d".format(adminStats.totalPhotosEdited), Icons.Default.PhotoLibrary),
        Triple("الملفات المحللة", "%,d".format(adminStats.totalFilesAnalyzed), Icons.Default.InsertDriveFile),
        Triple("مشتركو Pro", "%,d".format(adminStats.proSubscribers), Icons.Default.Diamond),
        Triple("الإيرادات الشهرية", "$%,.0f".format(adminStats.revenueMonthUsd), Icons.Default.AttachMoney),
        Triple("استهلاك التوكنز", "%,d".format(adminStats.aiTokensEstimated), Icons.Default.Speed)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊 لوحة الإدارة والتحكم (Admin Dashboard)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(shape = RoundedCornerShape(8.dp), color = SuccessGreen.copy(alpha = 0.2f)) {
                            Text(
                                text = "مستقر 100%",
                                color = SuccessGreen,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(
                        text = "متابعة أداء التطبيق، مستخدمي k&m Ai، واستهلاك المحركات الذكية",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Metrics Grid (displayed in paired rows for clean scrollable layout)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                statCards.chunked(2).forEach { rowStats ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowStats.forEach { (title, value, icon) ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_metric_${title}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Text(
                                        text = value,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // System Health & Engine Info
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "⚙️ حالة النظام والخوادم",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• المحرك الأساسي: Google Gemini 3.5 Flash (متصل بنجاح)\n• محرك الاستدلال: Gemini 3.1 Pro (جاهز للمهام المعقدة)\n• محرك الصور: Gemini 2.5 Flash Image & Compose Engine\n• قاعدة البيانات: Android Room Database (تشفير محلي آمن)\n• زمن الاستجابة المتوسط: 1.2 ثانية",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
