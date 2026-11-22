package com.example.data.remote

import android.content.Context
import androidx.annotation.Keep
import com.example.R
import com.example.core.firebase.OperationType
import com.example.core.firebase.handleFirestoreError
import com.example.domain.model.CodeProjectModel
import com.example.domain.model.LeaderboardMember
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

@Keep
@IgnoreExtraProperties
data class CloudUserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Codeverse Explorer",
    val avatarPreset: String = "cyber_bot",
    val languageCode: String = "en",
    val dailyGoalMinutes: Long = 15L,
    val learningTrack: String = "Full-Stack Web",
    val xp: Long = 120L,
    val level: Long = 1L,
    val streakDays: Long = 1L,
    val gems: Long = 120L,
    val completedLessonIds: List<String> = emptyList(),
    val unlockedAchievementIds: List<String> = emptyList(),
    val completedMissionIds: List<String> = emptyList(),
    val defeatedBossWorldIds: List<String> = emptyList(),
    val byteColorTheme: String = "lavender_prime",
    val byteAccessory: String = "none",
    val onboardingDone: Boolean = false,
    val proMember: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CloudCodeProject(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val htmlCode: String = "",
    val cssCode: String = "",
    val jsCode: String = "",
    val templateTag: String = "HTML/CSS/JS",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CloudLeaderboardEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Coder",
    val avatarPreset: String = "cyber_bot",
    val xp: Long = 0L,
    val level: Long = 1L,
    val streakDays: Long = 1L,
    val league: String = "Cyber League",
    val publicVisible: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

class CloudProgressRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProfile(userId: String): Flow<CloudUserProfile?> = flow {
        val uid = requireUserId()
        check(uid == userId) { "Refusing cross-user profile read." }
        val path = "users/$uid"
        emitAll(
            db.collection("users").document(uid)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            CloudUserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else null
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == uid) {
                        handleFirestoreError(fsErr ?: error, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    suspend fun ensureProfileInitialized(
        userId: String,
        displayName: String,
        languageCode: String,
        dailyGoalMinutes: Int,
        learningTrack: String,
        onboardingDone: Boolean
    ): Result<Unit> {
        val path = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Refusing cross-user initialization." }
            val safeName = displayName.trim().take(50).ifBlank { "Codeverse Coder" }
            val safeLang = if (languageCode in setOf("en", "hi", "hinglish")) languageCode else "en"
            val safeGoal = dailyGoalMinutes.coerceIn(5, 120)
            val safeTrack = learningTrack.trim().take(40).ifBlank { "Full-Stack Web" }

            val docRef = db.collection("users").document(uid)
            val snap = docRef.get().await()
            if (!snap.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "avatarPreset" to "cyber_bot",
                    "languageCode" to safeLang,
                    "dailyGoalMinutes" to safeGoal,
                    "learningTrack" to safeTrack,
                    "xp" to 120,
                    "level" to 1,
                    "streakDays" to 1,
                    "gems" to 150,
                    "completedLessonIds" to emptyList<String>(),
                    "unlockedAchievementIds" to listOf("ach_welcome"),
                    "completedMissionIds" to emptyList<String>(),
                    "defeatedBossWorldIds" to emptyList<String>(),
                    "byteColorTheme" to "lavender_prime",
                    "byteAccessory" to "none",
                    "onboardingDone" to onboardingDone,
                    "proMember" to false,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
                syncLeaderboardEntry(
                    userId = uid,
                    displayName = safeName,
                    avatarPreset = "cyber_bot",
                    xp = 120,
                    level = 1,
                    streakDays = 1
                )
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateProfileFields(
        userId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        val path = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Refusing cross-user update." }
            val allowed = updates.toMutableMap()
            allowed["updatedAt"] = FieldValue.serverTimestamp()
            db.collection("users").document(uid).update(allowed).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun syncLeaderboardEntry(
        userId: String,
        displayName: String,
        avatarPreset: String,
        xp: Int,
        level: Int,
        streakDays: Int
    ): Result<Unit> {
        val path = "leaderboard/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Refusing cross-user leaderboard write." }
            val docRef = db.collection("leaderboard").document(uid)
            val snap = docRef.get().await()
            val safeName = displayName.trim().take(50).ifBlank { "Codeverse Coder" }
            val safePreset = avatarPreset.trim().take(30).ifBlank { "cyber_bot" }
            val league = when {
                xp >= 2000 -> "Quantum League"
                xp >= 900 -> "Neon League"
                else -> "Cyber League"
            }
            if (!snap.exists()) {
                val createPayload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "avatarPreset" to safePreset,
                    "xp" to xp.coerceIn(0, 10_000_000),
                    "level" to level.coerceIn(1, 999),
                    "streakDays" to streakDays.coerceIn(0, 10_000),
                    "league" to league,
                    "publicVisible" to true,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(createPayload).await()
            } else {
                val updatePayload = mapOf(
                    "displayName" to safeName,
                    "avatarPreset" to safePreset,
                    "xp" to xp.coerceIn(0, 10_000_000),
                    "level" to level.coerceIn(1, 999),
                    "streakDays" to streakDays.coerceIn(0, 10_000),
                    "league" to league,
                    "publicVisible" to true,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updatePayload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeLeaderboard(currentUserId: String): Flow<List<LeaderboardMember>> = flow {
        val uid = requireUserId()
        check(uid == currentUserId) { "Refusing mismatched leaderboard query." }
        val path = "leaderboard"
        emitAll(
            db.collection("leaderboard")
                .whereEqualTo("publicVisible", true)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CloudLeaderboardEntry::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).map { entry ->
                        LeaderboardMember(
                            userId = entry.userId,
                            displayName = entry.displayName,
                            avatarPreset = entry.avatarPreset,
                            xp = entry.xp.toInt(),
                            level = entry.level.toInt(),
                            streakDays = entry.streakDays.toInt(),
                            league = entry.league,
                            isCurrentUser = entry.userId == uid
                        )
                    }.sortedByDescending { it.xp }
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == uid) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeUserProjects(userId: String): Flow<List<CodeProjectModel>> = flow {
        val uid = requireUserId()
        check(uid == userId) { "Refusing cross-user project read." }
        val path = "users/$uid/projects"
        emitAll(
            db.collection("users").document(uid).collection("projects")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CloudCodeProject::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).map { proj ->
                        CodeProjectModel(
                            id = proj.id,
                            title = proj.title,
                            description = proj.description,
                            htmlCode = proj.htmlCode,
                            cssCode = proj.cssCode,
                            jsCode = proj.jsCode,
                            templateTag = proj.templateTag,
                            updatedAtMillis = (proj.updatedAt ?: proj.createdAt)?.toDate()?.time
                                ?: System.currentTimeMillis()
                        )
                    }.sortedByDescending { it.updatedAtMillis }
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == uid) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun saveProject(userId: String, project: CodeProjectModel): Result<Unit> {
        val safeId = project.id.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(64).ifBlank {
            "proj_${System.currentTimeMillis()}"
        }
        val path = "users/$userId/projects/$safeId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Refusing cross-user project write." }
            val docRef = db.collection("users").document(uid).collection("projects").document(safeId)
            val snap = docRef.get().await()
            val title = project.title.trim().take(80).ifBlank { "Untitled Project" }
            val desc = project.description.trim().take(300)
            val html = project.htmlCode.take(50_000)
            val css = project.cssCode.take(50_000)
            val js = project.jsCode.take(50_000)
            val tag = project.templateTag.trim().take(30).ifBlank { "HTML/CSS/JS" }

            if (!snap.exists()) {
                val createMap = mapOf(
                    "userId" to uid,
                    "title" to title,
                    "description" to desc,
                    "htmlCode" to html,
                    "cssCode" to css,
                    "jsCode" to js,
                    "templateTag" to tag,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(createMap).await()
            } else {
                val updateMap = mapOf(
                    "title" to title,
                    "description" to desc,
                    "htmlCode" to html,
                    "cssCode" to css,
                    "jsCode" to js,
                    "templateTag" to tag,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updateMap).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteProject(userId: String, projectId: String): Result<Unit> {
        val path = "users/$userId/projects/$projectId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Refusing cross-user project delete." }
            db.collection("users").document(uid)
                .collection("projects").document(projectId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }
}
