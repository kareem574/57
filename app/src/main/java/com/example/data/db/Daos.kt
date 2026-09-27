package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, suggestedOrder ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY suggestedOrder ASC, createdAt DESC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleCompleted(id: Int, completed: Boolean)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE category = :category ORDER BY timestamp ASC")
    fun getMessagesByCategory(category: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessages(limit: Int = 10): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE category = :category")
    suspend fun clearCategoryMessages(category: String)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}

@Dao
interface UserPreferenceDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getPreferences(): Flow<UserPreferenceEntity?>

    @Query("SELECT * FROM user_preferences WHERE id = 1")
    suspend fun getPreferencesSync(): UserPreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(prefs: UserPreferenceEntity)

    @Query("UPDATE user_preferences SET messagesUsedToday = messagesUsedToday + 1 WHERE id = 1")
    suspend fun incrementMessageCount()

    @Query("UPDATE user_preferences SET photosUsedToday = photosUsedToday + 1 WHERE id = 1")
    suspend fun incrementPhotoCount()

    @Query("UPDATE user_preferences SET filesUsedToday = filesUsedToday + 1 WHERE id = 1")
    suspend fun incrementFileCount()

    @Query("UPDATE user_preferences SET tier = :tier WHERE id = 1")
    suspend fun updateTier(tier: String)
}
