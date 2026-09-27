package com.example.data.api

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

    val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Converts a Bitmap to JPEG Base64
     */
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 85): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Calls Gemini API with structured instructions
     * Model options:
     * - "gemini-3.5-flash" (default fast & intelligent)
     * - "gemini-3.1-pro-preview" (deep reasoning)
     */
    suspend fun generateContent(
        prompt: String,
        systemInstruction: String = "",
        model: String = "gemini-3.5-flash",
        imageBitmaps: List<Bitmap> = emptyList(),
        temperature: Float = 0.7f
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("لم يتم العثور على مفتاح Gemini API. يرجى إضافة GEMINI_API_KEY في إعدادات Secrets.")
            )
        }

        try {
            val url = "$BASE_URL$model:generateContent?key=$apiKey"

            val rootJson = JSONObject()

            // System instruction
            if (systemInstruction.isNotBlank()) {
                val sysPart = JSONObject().put("text", systemInstruction)
                val sysContent = JSONObject().put("parts", JSONArray().put(sysPart))
                rootJson.put("systemInstruction", sysContent)
            }

            // Contents
            val contentsArray = JSONArray()
            val userContent = JSONObject()
            val partsArray = JSONArray()

            // Add text part
            if (prompt.isNotBlank()) {
                partsArray.put(JSONObject().put("text", prompt))
            }

            // Add images if any
            for (bitmap in imageBitmaps) {
                val b64 = bitmapToBase64(bitmap)
                val inlineData = JSONObject()
                    .put("mimeType", "image/jpeg")
                    .put("data", b64)
                partsArray.put(JSONObject().put("inlineData", inlineData))
            }

            userContent.put("parts", partsArray)
            contentsArray.put(userContent)
            rootJson.put("contents", contentsArray)

            // Generation config
            val genConfig = JSONObject()
                .put("temperature", temperature)
                .put("topP", 0.95)
                .put("topK", 40)
            rootJson.put("generationConfig", genConfig)

            val body = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "API Error (${response.code}): $responseBody")
                return@withContext Result.failure(Exception("خطأ في الاتصال بالذكاء الاصطناعي (${response.code})"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val contentObj = firstCandidate.optJSONObject("content")
                val responseParts = contentObj?.optJSONArray("parts")
                if (responseParts != null && responseParts.length() > 0) {
                    val sb = StringBuilder()
                    for (i in 0 until responseParts.length()) {
                        val part = responseParts.getJSONObject(i)
                        sb.append(part.optString("text", ""))
                    }
                    return@withContext Result.success(sb.toString().trim())
                }
            }

            Result.failure(Exception("لم يتم استلام رد من الذكاء الاصطناعي"))
        } catch (e: Exception) {
            Log.e(TAG, "Request failed", e)
            Result.failure(e)
        }
    }
}
