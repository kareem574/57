package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.data.api.GeminiApiClient
import com.example.data.db.*
import com.example.data.model.Tone
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class AiRepository(private val chatDao: ChatDao, private val userPreferenceDao: UserPreferenceDao) {

    private val baseSystemInstruction = """
أنت المساعد الذكي "k&m Ai" (مساعدي الذكي الشخصي)، تم تصميم وتطوير التطبيق بواسطة "كريم شعبان".
شعارك: "قول لي محتاج إيه... وأنا أساعدك."

قواعد الرد الإلزامية:
1. أسلوبك دائماً: "مختصر + واضح + مفيد + مباشر وعملي".
2. تفهم اللغة العربية الفصحى بامتياز، واللهجة المصرية بشكل طبيعي وعفوي وودود، واللغة الإنجليزية.
3. إذا طلب المستخدم حلاً لمشكلة، أعطِ خطوات عملية مرقمة مباشرة دون مقدمات طويلة ولا كلام معقد (مثال: "تمام، اعمل 3 خطوات: 1... 2... 3...").
4. قسّم الإجابات إلى نقاط واضحة سهلة القراءة.
5. في المسائل الحسابية، احسب بدقة متناهية واعرض النتيجة وخطوة التحقق.
6. في البرمجة، قدّم كوداً نظيفاً وقابلاً للتشغيل فوراً.
7. لا تخترع معلومات أبداً. إذا لم تكن متأكداً من معلومة أو سعر أو تاريخ متغير، قل ذلك بوضوح وفرّق بين الحقائق والافتراضات.
8. تجنب الحشو والإنشاء الإنشائي الطويل.
""".trimIndent()

    suspend fun askAssistant(
        prompt: String,
        category: String = "chat",
        userTone: Tone = Tone.EGYPTIAN,
        bitmaps: List<Bitmap> = emptyList(),
        model: String = "gemini-3.5-flash"
    ): Result<String> {
        val toneInstruction = "الأسلوب المفضل للمستخدم: ${userTone.promptDescriptor}."
        val fullSystemInstruction = "$baseSystemInstruction\n$toneInstruction"

        // Record user message
        chatDao.insertMessage(
            ChatMessageEntity(
                role = "user",
                text = prompt,
                category = category,
                hasAttachment = bitmaps.isNotEmpty(),
                attachmentType = if (bitmaps.isNotEmpty()) "image" else ""
            )
        )
        userPreferenceDao.incrementMessageCount()

        val result = GeminiApiClient.generateContent(
            prompt = prompt,
            systemInstruction = fullSystemInstruction,
            model = model,
            imageBitmaps = bitmaps
        )

        result.onSuccess { responseText ->
            chatDao.insertMessage(
                ChatMessageEntity(
                    role = "assistant",
                    text = responseText,
                    category = category
                )
            )
        }

        return result
    }

    suspend fun analyzeDocumentOrData(
        content: String,
        actionRequest: String
    ): Result<String> {
        val prompt = """
المحتوى / البيانات المراد معالجتها:
'''
$content
'''

المطلوب بدقة: $actionRequest
نفّذ المطلوب بأسلوب مهني منظم في نقاط واضحة أو جداول، واستخرج أهم الأرقام والملاحظات والحلول المباشرة.
""".trimIndent()

        userPreferenceDao.incrementFileCount()
        return GeminiApiClient.generateContent(
            prompt = prompt,
            systemInstruction = "$baseSystemInstruction\nأنت خبير في تحليل البيانات والملفات والتقارير المالية والإدارية.",
            model = "gemini-3.5-flash"
        )
    }

    suspend fun generateContentCopy(
        formatTitle: String,
        topic: String,
        tone: Tone
    ): Result<String> {
        val prompt = """
المطلوب كتابة: $formatTitle
الموضوع / الفكرة: $topic
أسلوب الكتابة: ${tone.promptDescriptor}

التعليمات:
- جهّز المحتوى ليكون جاهزاً للنسخ والنشر مباشرة.
- ضع هاشتاجات مناسبة وإيموجي ذكي إذا كان مناسباً للسوشيال ميديا.
- اجعل البداية جذابة والسطور مريحة للعين.
""".trimIndent()

        userPreferenceDao.incrementMessageCount()
        return GeminiApiClient.generateContent(
            prompt = prompt,
            systemInstruction = baseSystemInstruction,
            model = "gemini-3.5-flash"
        )
    }

    suspend fun generatePosterContent(
        categoryTitle: String,
        briefIdea: String
    ): Result<String> {
        val prompt = """
المطلوب تجهيز نصوص وتصميم إعلاني لبوستر:
التصنيف: $categoryTitle
الفكرة: $briefIdea

أعطني النتيجة بصيغة JSON نظيفة فقط بدون أي نصوص خارجية تحتوي على:
{
  "companyName": "اسم مقترح أو المذكور",
  "headline": "عنوان رئيسي جذاب وقصير",
  "subheadline": "شرح تسويقي في سطر واحد",
  "discountText": "نص العرض أو الخصم مثل 'خصم 30%'",
  "ctaText": "جملة الحث على الشراء مثل 'اطلب الآن'"
}
""".trimIndent()

        return GeminiApiClient.generateContent(
            prompt = prompt,
            systemInstruction = "أنت مصمم إعلانات ومسوق إبداعي. أجب فقط بـ JSON المطلوب بدقة.",
            model = "gemini-3.5-flash"
        )
    }
}

class TaskRepository(
    private val taskDao: TaskDao,
    private val userPreferenceDao: UserPreferenceDao
) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<TaskEntity>> = taskDao.getPendingTasks()

    suspend fun addTask(title: String, description: String = "", priority: String = "normal", dueTime: String = ""): Long {
        return taskDao.insertTask(
            TaskEntity(
                title = title,
                description = description,
                priority = priority,
                dueTime = dueTime
            )
        )
    }

    suspend fun toggleTask(id: Int, isCompleted: Boolean) {
        taskDao.toggleCompleted(id, isCompleted)
    }

    suspend fun deleteTask(id: Int) {
        taskDao.deleteTaskById(id)
    }

    suspend fun clearCompleted() {
        taskDao.deleteCompletedTasks()
    }

    /**
     * AI-Powered Daily Schedule Optimizer ("نظم يومي")
     * Analyzes pending tasks and proposes an optimal schedule
     */
    suspend fun planDayWithAi(tasks: List<TaskEntity>): Result<String> {
        if (tasks.isEmpty()) {
            return Result.success("لا توجد مهام حالياً لتنظيمها. أضف بعض المهام وسأقوم بجدولتها فوراً!")
        }

        val tasksListString = tasks.mapIndexed { index, task ->
            "${index + 1}. ${task.title} (الأولوية: ${task.priority}, الوقت: ${task.dueTime.ifBlank { "غير محدد" }})"
        }.joinToString("\n")

        val prompt = """
لدى المستخدم هذه المهام اليوم:
$tasksListString

المطلوب:
رتّب هذه المهام في جدول يومي ذكي ومثالي مقسم حسب الساعات وفترات اليوم (صباحاً، بعد الظهر، مساءً) مع الأخذ في الاعتبار:
1. البدء بالمهام العاجلة والمهمة عندما تكون الطاقة عالية.
2. فترات راحة قصيرة وتركيز.
3. قدّم الجدول في نقاط مرقمة واضحة ومباشرة.
""".trimIndent()

        return GeminiApiClient.generateContent(
            prompt = prompt,
            systemInstruction = "أنت مخطط إنتاجية شخصي ذكي وعملي جداً. رتب المهام بنقاط واضحة ومحفزة.",
            model = "gemini-3.5-flash"
        )
    }
}

class UserSettingsRepository(
    private val userPreferenceDao: UserPreferenceDao,
    private val chatDao: ChatDao,
    private val taskDao: TaskDao
) {
    val preferences: Flow<UserPreferenceEntity?> = userPreferenceDao.getPreferences()

    suspend fun ensureInitialized() {
        val existing = userPreferenceDao.getPreferencesSync()
        if (existing == null) {
            userPreferenceDao.savePreferences(UserPreferenceEntity())
        }
    }

    suspend fun updatePreferences(prefs: UserPreferenceEntity) {
        userPreferenceDao.savePreferences(prefs)
    }

    suspend fun updateTier(tier: String) {
        userPreferenceDao.updateTier(tier)
    }

    suspend fun clearChatHistory() {
        chatDao.clearAllMessages()
    }

    suspend fun clearAllData() {
        chatDao.clearAllMessages()
        taskDao.clearAllTasks()
        userPreferenceDao.savePreferences(UserPreferenceEntity())
    }
}
