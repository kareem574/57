package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class AppScreen {
    HOME,
    CHAT,
    PHOTO_EDITOR,
    POSTER_GENERATOR,
    CONTENT_WRITER,
    MY_DAY,
    WORK_ASSISTANT,
    SETTINGS,
    ADMIN_DASHBOARD
}

data class QuickAction(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val screen: AppScreen,
    val description: String,
    val initialPrompt: String = ""
)

enum class Tone(val id: String, val titleAr: String, val promptDescriptor: String) {
    FORMAL("formal", "رسمي", "بأسلوب رسمي ومحترم واحترافي"),
    EGYPTIAN("egyptian", "عامي مصري", "بلهجة عامية مصرية لطيفة ومباشرة ومحبوبة"),
    PROFESSIONAL("pro", "احترافي", "بأسلوب بيزنس واحترافي وعملي"),
    MARKETING("marketing", "تسويقي جذاب", "بأسلوب تسويقي مقنع وجذاب ويدفع لاتخاذ قرار"),
    CONCISE("concise", "مختصر ومباشر", "بأسلوب شديد الاختصار في نقاط بدون حشو"),
    PUNCHY("punchy", "قوي ومؤثر", "بكلمات قوية ومحفزة"),
    FRIENDLY("friendly", "ودي وبسيط", "بأسلوب ودي ومريح وسهل الفهم"),
    STRICT_FORMAL("strict", "رسمي جدًا", "بصيغة رسمية للغاية موجهة لجهات عليا")
}

enum class ContentFormat(val id: String, val titleAr: String, val category: String) {
    FB_POST("fb_post", "بوست فيسبوك", "سوشيال ميديا"),
    IG_CAPTION("ig_caption", "كابشن إنستجرام", "سوشيال ميديا"),
    TIKTOK_SCRIPT("tiktok", "سكريبت تيك توك", "فيديو"),
    YOUTUBE_SCRIPT("youtube", "سكريبت يوتيوب", "فيديو"),
    PRODUCT_DESC("product", "وصف منتج", "تسويق"),
    AD_COPY("ad", "إعلان ترويجي", "تسويق"),
    WHATSAPP_MSG("whatsapp", "رسالة واتساب", "مراسلات"),
    OFFICIAL_LETTER("letter", "خطاب رسمي", "أعمال"),
    BUSINESS_EMAIL("email", "إيميل بيزنس", "أعمال"),
    CV_SUMMARY("cv", "ملخص سيرة ذاتية (CV)", "وظائف"),
    JOB_POST("job", "إعلان وظيفة", "وظائف"),
    CONTENT_IDEAS("ideas", "أفكار محتوى أسبوعية", "أفكار")
}

data class PosterStyle(
    val id: String,
    val titleAr: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val textColor: Color,
    val bgColors: List<Color>
)

val PosterStyles = listOf(
    PosterStyle(
        id = "modern_indigo",
        titleAr = "نيلي عصري",
        primaryColor = Color(0xFF4F46E5),
        secondaryColor = Color(0xFF06B6D4),
        textColor = Color.White,
        bgColors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
    ),
    PosterStyle(
        id = "gold_luxury",
        titleAr = "ذهبي فاخر",
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFD97706),
        textColor = Color.White,
        bgColors = listOf(Color(0xFF18181B), Color(0xFF27272A))
    ),
    PosterStyle(
        id = "emerald_fresh",
        titleAr = "أخضر زمردي",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF059669),
        textColor = Color.White,
        bgColors = listOf(Color(0xFF064E3B), Color(0xFF022C22))
    ),
    PosterStyle(
        id = "crimson_sale",
        titleAr = "أحمر عروض وخصومات",
        primaryColor = Color(0xFFEF4444),
        secondaryColor = Color(0xFFDC2626),
        textColor = Color.White,
        bgColors = listOf(Color(0xFF7F1D1D), Color(0xFF450A0A))
    ),
    PosterStyle(
        id = "ramadan_spiritual",
        titleAr = "رمضاني / أعياد",
        primaryColor = Color(0xFF8B5CF6),
        secondaryColor = Color(0xFFFBBF24),
        textColor = Color.White,
        bgColors = listOf(Color(0xFF2E1065), Color(0xFF1E1B4B))
    )
)

enum class PosterCategory(val id: String, val titleAr: String, val defaultBadge: String) {
    SALE_DISCOUNT("sale", "خصومات وعروض", "خصم خاص 30%"),
    RESTAURANT("food", "مطاعم وكافيهات", "عرض الوجبة المميزة"),
    HIRING("job", "وظائف شاغرة", "انضم لفريقنا"),
    EVENT("event", "أحداث وحفلات", "الدعوة عامة"),
    STORE("store", "افتتاح متجر", "الافتتاح الكبير"),
    HOLIDAY("holiday", "تهنئة رمضان والعيد", "كل عام وأنتم بخير"),
    SERVICE("service", "خدمات وشركات", "أفضل جودة وخدمة")
}

data class PosterData(
    val category: PosterCategory = PosterCategory.SALE_DISCOUNT,
    val companyName: String = "كريم للحلول الذكية",
    val headline: String = "عرض حصري لفترة محدودة",
    val subheadline: String = "احصل على أفضل الخدمات بأعلى جودة مع خصومات خاصة",
    val discountText: String = "خصم 30%",
    val ctaText: String = "اطلب الآن واستفد من العرض",
    val phone: String = "01000000000",
    val website: String = "www.kmai-assistant.com",
    val style: PosterStyle = PosterStyles[0],
    val aspectRatio: String = "1:1" // "1:1", "9:16", "4:5"
)

enum class PhotoFilter(val id: String, val titleAr: String, val icon: String, val prompt: String) {
    ENHANCE("enhance", "تحسين الجودة والإضاءة", "✨", "تحسين جودة الصورة والألوان ووضوح التفاصيل الإضاءة العالية"),
    REMOVE_BG("remove_bg", "إزالة وتفريغ الخلفية", "✂️", "عزل العنصر الأساسي وإزالة الخلفية بدقة فائقة"),
    STUDIO_LIGHT("studio", "إضاءة استوديو احترافية", "💡", "تعديل الإضاءة لتكون إضاءة بورتريه استوديو ناعمة واحترافية"),
    PRO_HEADSHOT("headshot", "بروفايل رسمي ولينكد إن", "👔", "تحويل الصورة إلى صورة بروفايل بيزنس رسمية وجذابة"),
    COLOR_POP("color", "ألوان سينمائية نابضة", "🎨", "تعزيز تشبع الألوان والتباين وإعطاء مظهر سينمائي مبهر"),
    INSTA_FIT("insta", "تنسيق إنستجرام 1:1", "📸", "تنسيق الصورة بأبعاد إنستجرام المربعة مع إطار عصري"),
    STORY_FIT("story", "تنسيق ستوري وتيك توك 9:16", "📱", "تنسيق الصورة عمودياً لتناسب شاشة الستوري والتيك توك")
}

data class AdminOverviewStats(
    val totalUsers: Int = 18450,
    val activeUsersToday: Int = 2410,
    val totalMessages: Int = 142890,
    val totalPhotosEdited: Int = 18640,
    val totalFilesAnalyzed: Int = 9830,
    val proSubscribers: Int = 3840,
    val revenueMonthUsd: Double = 19200.0,
    val aiTokensEstimated: Long = 48500000L,
    val systemStatus: String = "يعمل بكفاءة 100% (Gemini 3.5 Flash & Pro)"
)
