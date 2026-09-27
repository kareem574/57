package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Calendar
import java.util.Locale

class SpeechHelper(private val context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    init {
        try {
            textToSpeech = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = textToSpeech?.setLanguage(Locale("ar"))
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech?.setLanguage(Locale.US)
                    }
                    isTtsReady = true
                }
            }
        } catch (e: Exception) {
            Log.e("SpeechHelper", "TTS init error", e)
        }
    }

    fun speak(text: String) {
        if (isTtsReady && text.isNotBlank()) {
            // Strip markdown asterisks or hash symbols for clean speech
            val cleaned = text.replace(Regex("[*#_`~]"), "")
            textToSpeech?.speak(cleaned, TextToSpeech.QUEUE_FLUSH, null, "km_ai_tts")
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}

object TimeGreetingHelper {
    fun getGreeting(): Pair<String, String> {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 4..11 -> Pair("صباح الخير يا بطل 👋", "يوم جديد وإنجازات عظيمة، أنا في خدمتك")
            in 12..16 -> Pair("طاب مساؤك يا فنان ☀️", "أنا جاهز لمساعدتك في أي مهمة أو فكرة")
            in 17..22 -> Pair("مساء الورد والسعادة 🌙", "كيف أقدر أسهل عليك يومك الليلة؟")
            else -> Pair("أهلاً بك في سكون الليل 🌌", "أفكار الليل ومشاريع الغد، شاركني سؤالك")
        }
    }
}
