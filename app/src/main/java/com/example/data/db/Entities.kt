package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val priority: String = "normal", // "urgent", "important", "normal"
    val dueTime: String = "",
    val isCompleted: Boolean = false,
    val suggestedOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val role: String, // "user", "assistant"
    val text: String,
    val category: String = "chat", // "chat", "photo", "poster", "content", "file", "work"
    val hasAttachment: Boolean = false,
    val attachmentType: String = "", // "image", "file"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey
    val id: Int = 1,
    val userName: String = "صديقي",
    val preferredDialect: String = "egyptian", // "egyptian", "formal_arabic", "english"
    val responseStyle: String = "concise", // "concise", "actionable", "detailed"
    val tier: String = "free", // "guest", "free", "pro"
    val messagesUsedToday: Int = 0,
    val photosUsedToday: Int = 0,
    val filesUsedToday: Int = 0,
    val customInstructions: String = "",
    val lastResetDate: Long = System.currentTimeMillis()
)
