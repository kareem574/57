package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.PosterCategory
import com.example.data.model.PosterData
import com.example.data.model.PosterStyles
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun PosterGeneratorScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val posterData by viewModel.posterData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var briefIdea by remember { mutableStateOf("خصم 30% بمناسبة الافتتاح الجديد") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "🎨 صانع البوسترات الإعلانية الذكي",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "صمم بوستر إعلاني احترافي لشركتك أو منتجك خلال ثوانٍ مع معاينة حية",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Live Poster Visual Canvas (Real-time Rendered)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .testTag("poster_canvas_preview"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(posterData.style.bgColors))
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top Bar: Company Name & Category Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = posterData.style.primaryColor
                            ) {
                                Text(
                                    text = posterData.discountText,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = posterData.companyName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = posterData.style.secondaryColor
                            )
                        }

                        // Center: Headline & Subheadline
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = posterData.headline,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                lineHeight = 32.sp
                            )
                            Text(
                                text = posterData.subheadline,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // CTA Button on Poster
                        Surface(
                            shape = RoundedCornerShape(30.dp),
                            color = posterData.style.secondaryColor,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = posterData.ctaText,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        // Bottom Footer Contacts: Phone & Website
                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌐 ${posterData.website}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "📞 ${posterData.phone}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // Quick AI Idea Input
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "اكتب فكرة البوستر والذكاء الاصطناعي يكتب النصوص:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = briefIdea,
                        onValueChange = { briefIdea = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("poster_brief_input"),
                        placeholder = { Text("مثال: إعلان عروض صيفية للملابس بخصم 40% لفترة محدودة") },
                        shape = RoundedCornerShape(10.dp)
                    )
                    Button(
                        onClick = { viewModel.generatePosterCopy(briefIdea) },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("poster_generate_ai_copy_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("جاري صياغة البوستر بالذكاء الاصطناعي...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("توليد نصوص البوستر بالذكاء الاصطناعي ✨")
                        }
                    }
                }
            }
        }

        // Template Categories
        item {
            Text(text = "اختر نوع التصميم والقالب:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(PosterCategory.values()) { cat ->
                    val isSelected = posterData.category == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.updatePoster(
                                posterData.copy(
                                    category = cat,
                                    discountText = cat.defaultBadge
                                )
                            )
                        },
                        label = { Text(cat.titleAr, fontSize = 12.sp) },
                        modifier = Modifier.testTag("poster_category_${cat.id}")
                    )
                }
            }
        }

        // Color Palettes
        item {
            Text(text = "طابع الألوان:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(PosterStyles) { style ->
                    val isSelected = posterData.style.id == style.id
                    Surface(
                        onClick = { viewModel.updatePoster(posterData.copy(style = style)) },
                        shape = RoundedCornerShape(12.dp),
                        color = style.primaryColor.copy(alpha = if (isSelected) 1f else 0.3f),
                        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(style.secondaryColor)
                            )
                            Text(
                                text = style.titleAr,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Editable Details Inputs
        item {
            Text(text = "تعديل تفاصيل النصوص يدوياً:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = posterData.companyName,
                    onValueChange = { viewModel.updatePoster(posterData.copy(companyName = it)) },
                    label = { Text("اسم الشركة / المتجر") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = posterData.headline,
                    onValueChange = { viewModel.updatePoster(posterData.copy(headline = it)) },
                    label = { Text("العنوان الرئيسي للبوستر") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = posterData.discountText,
                    onValueChange = { viewModel.updatePoster(posterData.copy(discountText = it)) },
                    label = { Text("شارة العرض أو الخصم") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = posterData.phone,
                    onValueChange = { viewModel.updatePoster(posterData.copy(phone = it)) },
                    label = { Text("رقم الهاتف للتواصل") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Share & Copy Poster Details
        item {
            Button(
                onClick = {
                    val summary = "بوستر ${posterData.companyName}\n${posterData.headline}\n${posterData.subheadline}\n${posterData.discountText}\nللتواصل: ${posterData.phone} | ${posterData.website}"
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Poster Data", summary))
                    Toast.makeText(context, "تم نسخ تفاصيل وبيانات البوستر!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("copy_poster_data_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("نسخ ومشاركة محتوى البوستر", fontWeight = FontWeight.Bold)
            }
        }
    }
}
