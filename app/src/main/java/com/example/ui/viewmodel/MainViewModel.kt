package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiClient
import com.example.data.db.*
import com.example.data.model.*
import com.example.data.repository.*
import com.example.util.SpeechHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val aiRepository = AiRepository(db.chatDao(), db.userPreferenceDao())
    val taskRepository = TaskRepository(db.taskDao(), db.userPreferenceDao())
    val userSettingsRepository = UserSettingsRepository(db.userPreferenceDao(), db.chatDao(), db.taskDao())

    val speechHelper = SpeechHelper(application)

    // Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<AppScreen>()

    // Global loading and status
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Chat State
    val chatMessages: StateFlow<List<ChatMessageEntity>> = db.chatDao().getMessagesByCategory("chat")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _attachedBitmap = MutableStateFlow<Bitmap?>(null)
    val attachedBitmap: StateFlow<Bitmap?> = _attachedBitmap.asStateFlow()

    private val _selectedModel = MutableStateFlow("gemini-3.5-flash")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    // Tasks State
    val tasks: StateFlow<List<TaskEntity>> = taskRepository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _aiDailyPlan = MutableStateFlow<String?>(null)
    val aiDailyPlan: StateFlow<String?> = _aiDailyPlan.asStateFlow()

    // User Preferences State
    val userPreferences: StateFlow<UserPreferenceEntity?> = userSettingsRepository.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Poster Generator State
    private val _posterData = MutableStateFlow(PosterData())
    val posterData: StateFlow<PosterData> = _posterData.asStateFlow()

    // Photo Editor State
    private val _photoBitmap = MutableStateFlow<Bitmap?>(null)
    val photoBitmap: StateFlow<Bitmap?> = _photoBitmap.asStateFlow()

    private val _selectedFilter = MutableStateFlow<PhotoFilter>(PhotoFilter.ENHANCE)
    val selectedFilter: StateFlow<PhotoFilter> = _selectedFilter.asStateFlow()

    private val _photoCustomPrompt = MutableStateFlow("")
    val photoCustomPrompt: StateFlow<String> = _photoCustomPrompt.asStateFlow()

    private val _photoResultInfo = MutableStateFlow<String?>(null)
    val photoResultInfo: StateFlow<String?> = _photoResultInfo.asStateFlow()

    // Content Writer State
    private val _selectedFormat = MutableStateFlow(ContentFormat.FB_POST)
    val selectedFormat: StateFlow<ContentFormat> = _selectedFormat.asStateFlow()

    private val _selectedTone = MutableStateFlow(Tone.EGYPTIAN)
    val selectedTone: StateFlow<Tone> = _selectedTone.asStateFlow()

    private val _contentTopicInput = MutableStateFlow("")
    val contentTopicInput: StateFlow<String> = _contentTopicInput.asStateFlow()

    private val _generatedContent = MutableStateFlow("")
    val generatedContent: StateFlow<String> = _generatedContent.asStateFlow()

    // Work & File Assistant State
    private val _fileContentText = MutableStateFlow("")
    val fileContentText: StateFlow<String> = _fileContentText.asStateFlow()

    private val _fileAnalysisResult = MutableStateFlow("")
    val fileAnalysisResult: StateFlow<String> = _fileAnalysisResult.asStateFlow()

    // Admin Stats
    private val _adminStats = MutableStateFlow(AdminOverviewStats())
    val adminStats: StateFlow<AdminOverviewStats> = _adminStats.asStateFlow()

    init {
        viewModelScope.launch {
            userSettingsRepository.ensureInitialized()
        }
    }

    // Navigation Methods
    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            return true
        } else if (_currentScreen.value != AppScreen.HOME) {
            _currentScreen.value = AppScreen.HOME
            return true
        }
        return false
    }

    // Chat Actions
    fun setChatInput(text: String) {
        _chatInput.value = text
    }

    fun setAttachedBitmap(bitmap: Bitmap?) {
        _attachedBitmap.value = bitmap
    }

    fun setSelectedModel(model: String) {
        _selectedModel.value = model
    }

    fun sendChatMessage(overridePrompt: String? = null) {
        val message = overridePrompt ?: _chatInput.value
        if (message.isBlank() && _attachedBitmap.value == null) return

        val bitmapList = _attachedBitmap.value?.let { listOf(it) } ?: emptyList()
        _chatInput.value = ""
        _attachedBitmap.value = null
        _isLoading.value = true

        viewModelScope.launch {
            val userPref = userPreferences.value
            val tone = when (userPref?.preferredDialect) {
                "formal_arabic" -> Tone.FORMAL
                "english" -> Tone.PROFESSIONAL
                else -> Tone.EGYPTIAN
            }

            val result = aiRepository.askAssistant(
                prompt = message,
                category = "chat",
                userTone = tone,
                bitmaps = bitmapList,
                model = _selectedModel.value
            )

            _isLoading.value = false
            result.onFailure { error ->
                _statusMessage.value = error.message ?: "حدث خطأ غير متوقع"
            }
        }
    }

    fun speakText(text: String) {
        speechHelper.speak(text)
    }

    fun stopSpeaking() {
        speechHelper.stopSpeaking()
    }

    fun clearChat() {
        viewModelScope.launch {
            db.chatDao().clearCategoryMessages("chat")
        }
    }

    // Task Actions
    fun addTask(title: String, description: String = "", priority: String = "normal", dueTime: String = "") {
        if (title.isBlank()) return
        viewModelScope.launch {
            taskRepository.addTask(title, description, priority, dueTime)
        }
    }

    fun toggleTask(id: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleTask(id, isCompleted)
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            taskRepository.deleteTask(id)
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            taskRepository.clearCompleted()
        }
    }

    fun planMyDay() {
        _isLoading.value = true
        viewModelScope.launch {
            val pending = tasks.value.filter { !it.isCompleted }
            val result = taskRepository.planDayWithAi(pending)
            _isLoading.value = false
            result.onSuccess { planText ->
                _aiDailyPlan.value = planText
            }.onFailure { error ->
                _statusMessage.value = error.message ?: "فشل في تنظيم اليوم"
            }
        }
    }

    fun dismissDailyPlanDialog() {
        _aiDailyPlan.value = null
    }

    // Poster Actions
    fun updatePoster(data: PosterData) {
        _posterData.value = data
    }

    fun generatePosterCopy(brief: String) {
        if (brief.isBlank()) return
        _isLoading.value = true
        viewModelScope.launch {
            val result = aiRepository.generatePosterContent(_posterData.value.category.titleAr, brief)
            _isLoading.value = false
            result.onSuccess { jsonStr ->
                try {
                    val cleanJson = jsonStr.substringAfter("{").substringBeforeLast("}")
                    val fullJson = "{$cleanJson}"
                    val obj = JSONObject(fullJson)
                    _posterData.value = _posterData.value.copy(
                        companyName = obj.optString("companyName", _posterData.value.companyName),
                        headline = obj.optString("headline", _posterData.value.headline),
                        subheadline = obj.optString("subheadline", _posterData.value.subheadline),
                        discountText = obj.optString("discountText", _posterData.value.discountText),
                        ctaText = obj.optString("ctaText", _posterData.value.ctaText)
                    )
                } catch (e: Exception) {
                    _statusMessage.value = "تم توليد النص ولكن بتنسيق بديل"
                }
            }.onFailure { error ->
                _statusMessage.value = error.message ?: "فشل توليد البوستر"
            }
        }
    }

    // Photo Editor Actions
    fun setPhotoBitmap(bitmap: Bitmap?) {
        _photoBitmap.value = bitmap
    }

    fun setSelectedFilter(filter: PhotoFilter) {
        _selectedFilter.value = filter
    }

    fun setPhotoCustomPrompt(text: String) {
        _photoCustomPrompt.value = text
    }

    fun applyPhotoAiAction() {
        val currentBitmap = _photoBitmap.value
        val filter = _selectedFilter.value
        val prompt = _photoCustomPrompt.value.ifBlank { filter.prompt }

        _isLoading.value = true
        viewModelScope.launch {
            val bitmaps = if (currentBitmap != null) listOf(currentBitmap) else emptyList()
            val result = aiRepository.askAssistant(
                prompt = "طلب تعديل ومعالجة الصورة: $prompt. حلل الصورة وقدم إرشادات التعديل الدقيقة وأهم التغييرات المقترحة لتنفيذها مع توليد الكود البصري المناسب.",
                category = "photo",
                userTone = Tone.PROFESSIONAL,
                bitmaps = bitmaps,
                model = "gemini-3.5-flash"
            )
            _isLoading.value = false
            result.onSuccess { info ->
                _photoResultInfo.value = info
            }.onFailure { error ->
                _statusMessage.value = error.message ?: "فشل في معالجة الصورة"
            }
        }
    }

    // Content Writer Actions
    fun setContentFormat(format: ContentFormat) {
        _selectedFormat.value = format
    }

    fun setContentTone(tone: Tone) {
        _selectedTone.value = tone
    }

    fun setContentTopic(topic: String) {
        _contentTopicInput.value = topic
    }

    fun generateContent() {
        if (_contentTopicInput.value.isBlank()) return
        _isLoading.value = true
        viewModelScope.launch {
            val result = aiRepository.generateContentCopy(
                formatTitle = _selectedFormat.value.titleAr,
                topic = _contentTopicInput.value,
                tone = _selectedTone.value
            )
            _isLoading.value = false
            result.onSuccess { text ->
                _generatedContent.value = text
            }.onFailure { error ->
                _statusMessage.value = error.message ?: "فشل في إنشاء المحتوى"
            }
        }
    }

    // Work & File Assistant Actions
    fun setFileContentText(text: String) {
        _fileContentText.value = text
    }

    fun analyzeFileAction(action: String) {
        val text = _fileContentText.value
        if (text.isBlank()) {
            _statusMessage.value = "يرجى لصق أو رفع نص أو بيانات الملف أولاً"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val result = aiRepository.analyzeDocumentOrData(text, action)
            _isLoading.value = false
            result.onSuccess { analysis ->
                _fileAnalysisResult.value = analysis
            }.onFailure { error ->
                _statusMessage.value = error.message ?: "فشل في تحليل الملف"
            }
        }
    }

    // Settings & Tiers
    fun updateTier(tier: String) {
        viewModelScope.launch {
            userSettingsRepository.updateTier(tier)
            _statusMessage.value = "تم تحديث الباقة بنجاح!"
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            userSettingsRepository.clearAllData()
            _statusMessage.value = "تم مسح كافة البيانات والمحادثات بنجاح"
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        speechHelper.shutdown()
    }
}
