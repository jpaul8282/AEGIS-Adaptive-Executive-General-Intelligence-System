package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "aegis_audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val query: String,
    val domain: String,
    val sanitizedQuery: String,
    val securityStatus: String, // "PASSED", "BLOCKED", "PII_STRIPPED"
    val parallelSourcesCalled: String, // Comma-separated sources
    val confidenceScore: Float,
    val responseSummary: String,
    val wasFallback: Boolean = false
)

@Entity(tableName = "aegis_conversations")
data class ConversationMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sender: String, // "USER" or "AEGIS"
    val domain: String, // "SECURITY", "DATA_ANALYSIS", "MATH", "ART", "SALES", "HEALTH", "EXECUTIVE"
    val content: String,
    val sourcesUsedJson: String = "[]",
    val securityLevel: String = "SHIELD_ACTIVE",
    val confidence: Float = 0.95f
)

@Entity(tableName = "aegis_executive_tasks")
data class ExecutiveTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "SECURITY_AUDIT", "DATA_ANALYSIS", "MEETING_SUMMARY", "HEALTH_CHECK", "GENERAL"
    val priority: String, // "HIGH", "MEDIUM", "LOW"
    val isCompleted: Boolean = false,
    val dueDate: String,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Dao
interface AegisDao {
    @Query("SELECT * FROM aegis_audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    @Query("DELETE FROM aegis_audit_logs")
    suspend fun clearAuditLogs()

    @Query("SELECT * FROM aegis_conversations ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ConversationMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ConversationMessageEntity)

    @Query("DELETE FROM aegis_conversations")
    suspend fun clearMessages()

    @Query("SELECT * FROM aegis_executive_tasks ORDER BY isCompleted ASC, id DESC")
    fun getAllTasks(): Flow<List<ExecutiveTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ExecutiveTaskEntity)

    @Update
    suspend fun updateTask(task: ExecutiveTaskEntity)

    @Query("DELETE FROM aegis_executive_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)
}

@Database(
    entities = [AuditLogEntity::class, ConversationMessageEntity::class, ExecutiveTaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AegisDatabase : RoomDatabase() {
    abstract fun aegisDao(): AegisDao
}
