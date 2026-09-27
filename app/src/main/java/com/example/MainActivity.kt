package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppScreen
import com.example.ui.components.KmBottomNavigation
import com.example.ui.components.KmTopAppBar
import com.example.ui.components.LoadingDialog
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val statusMessage by viewModel.statusMessage.collectAsState()
                val userPref by viewModel.userPreferences.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(statusMessage) {
                    statusMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearStatusMessage()
                    }
                }

                val screenTitle = when (currentScreen) {
                    AppScreen.HOME -> "k&m Ai"
                    AppScreen.CHAT -> "المحادثة الذكية"
                    AppScreen.PHOTO_EDITOR -> "تعديل الصور"
                    AppScreen.POSTER_GENERATOR -> "تصميم البوسترات"
                    AppScreen.CONTENT_WRITER -> "كتابة المحتوى"
                    AppScreen.MY_DAY -> "مهامي وتنظيم اليوم"
                    AppScreen.WORK_ASSISTANT -> "مساعد العمل والملفات"
                    AppScreen.SETTINGS -> "الإعدادات والذاكرة"
                    AppScreen.ADMIN_DASHBOARD -> "لوحة الإدارة"
                }

                val screenSubtitle = when (currentScreen) {
                    AppScreen.HOME -> "قول لي محتاج إيه... وأنا أساعدك"
                    AppScreen.CHAT -> "Gemini 3.5 Flash & Pro"
                    AppScreen.PHOTO_EDITOR -> "تحسين وتفريغ وتنسيق"
                    AppScreen.POSTER_GENERATOR -> "إعلانات وعروض تجارية"
                    AppScreen.CONTENT_WRITER -> "سوشيال ميديا وأعمال"
                    AppScreen.MY_DAY -> "جدول يومي ذكي"
                    AppScreen.WORK_ASSISTANT -> "تحليل بيانات وتقارير"
                    AppScreen.SETTINGS -> "مصمم التطبيق: كريم شعبان"
                    AppScreen.ADMIN_DASHBOARD -> "إحصائيات النظام ومعدلات الاستخدام"
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        KmTopAppBar(
                            title = screenTitle,
                            subtitle = screenSubtitle,
                            showBackButton = currentScreen != AppScreen.HOME,
                            onBackClick = { viewModel.navigateBack() },
                            onAdminClick = {
                                if (currentScreen != AppScreen.ADMIN_DASHBOARD) {
                                    viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                                } else {
                                    viewModel.navigateTo(AppScreen.HOME)
                                }
                            },
                            onSettingsClick = {
                                if (currentScreen != AppScreen.SETTINGS) {
                                    viewModel.navigateTo(AppScreen.SETTINGS)
                                } else {
                                    viewModel.navigateTo(AppScreen.HOME)
                                }
                            },
                            tier = userPref?.tier ?: "Free"
                        )
                    },
                    bottomBar = {
                        KmBottomNavigation(
                            currentScreen = currentScreen,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "screen_transition"
                        ) { screen ->
                            when (screen) {
                                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                                AppScreen.CHAT -> ChatScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                                AppScreen.PHOTO_EDITOR -> PhotoEditorScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                                AppScreen.POSTER_GENERATOR -> PosterGeneratorScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                                AppScreen.CONTENT_WRITER -> ContentWriterScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                                AppScreen.MY_DAY -> TasksScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                                AppScreen.WORK_ASSISTANT -> WorkAssistantScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                                AppScreen.SETTINGS -> SettingsScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                                AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                        }

                        if (isLoading) {
                            LoadingDialog()
                        }
                    }
                }
            }
        }
    }
}
