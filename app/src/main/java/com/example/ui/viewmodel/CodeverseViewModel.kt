package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.CodeverseI18n
import com.example.core.i18n.LocalizedStrings
import com.example.data.local.CodeverseDatabase
import com.example.data.local.CompletedLessonEntity
import com.example.data.local.LocalPreferences
import com.example.data.local.ProjectEntity
import com.example.data.local.UserPreferencesStore
import com.example.data.remote.AiTutorRepository
import com.example.data.remote.CloudProgressRepository
import com.example.data.seed.CurriculumSeed
import com.example.domain.model.ByteAccessory
import com.example.domain.model.ByteColorTheme
import com.example.domain.model.ByteExpression
import com.example.domain.model.CodeFileTab
import com.example.domain.model.CodeProjectModel
import com.example.domain.model.ConsoleLogEntry
import com.example.domain.model.LeaderboardMember
import com.example.domain.model.LessonResultSummary
import com.example.domain.model.TutorMessage
import com.example.domain.model.TutorQuickAction
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val VM_TAG = "CodeverseVM"

data class CodeverseUiState(
    val userId: String = "",
    val displayName: String = "Codeverse Explorer",
    val email: String = "",
    val language: AppLanguage = AppLanguage.ENGLISH,
    val strings: LocalizedStrings = CodeverseI18n.stringsFor(AppLanguage.ENGLISH),
    val dailyGoalMinutes: Int = 15,
    val learningTrack: String = "Full-Stack Web",
    val onboardingDone: Boolean = false,
    val darkThemeEnabled: Boolean = true,
    val voiceNarrationEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val dailyReminderEnabled: Boolean = true,
    val xp: Int = 120,
    val level: Int = 1,
    val streakDays: Int = 1,
    val gems: Int = 150,
    val proMember: Boolean = false,
    val completedLessonIds: Set<String> = emptySet(),
    val unlockedAchievementIds: Set<String> = setOf("ach_welcome"),
    val completedMissionIds: Set<String> = emptySet(),
    val defeatedBossWorldIds: Set<String> = emptySet(),
    val byteColorTheme: ByteColorTheme = ByteColorTheme.LAVENDER_PRIME,
    val byteAccessory: ByteAccessory = ByteAccessory.NONE,
    val ownedThemeIds: Set<String> = setOf("lavender_prime"),
    val ownedAccessoryIds: Set<String> = setOf("none"),
    val lastLessonResult: LessonResultSummary? = null,
    val savedProjects: List<CodeProjectModel> = CurriculumSeed.starterProjects,
    val leaderboard: List<LeaderboardMember> = CurriculumSeed.baselineLeaguePeers,
    val cloudSyncError: String? = null,
    val statusBanner: String? = null,
    // Playground editor state
    val activeProjectId: String = CurriculumSeed.starterProjects.first().id,
    val activeProjectTitle: String = CurriculumSeed.starterProjects.first().title,
    val activeProjectDescription: String = CurriculumSeed.starterProjects.first().description,
    val activeFileTab: CodeFileTab = CodeFileTab.HTML,
    val htmlCode: String = CurriculumSeed.starterProjects.first().htmlCode,
    val cssCode: String = CurriculumSeed.starterProjects.first().cssCode,
    val jsCode: String = CurriculumSeed.starterProjects.first().jsCode,
    val previewNonce: Int = 1,
    val consoleLogs: List<ConsoleLogEntry> = emptyList(),
    // AI Tutor state
    val tutorMessages: List<TutorMessage> = listOf(
        TutorMessage(
            id = "welcome_byte",
            isFromUser = false,
            text = "Hey coder! I'm BYTE, powered by Gemini. Paste any HTML, CSS, JavaScript, React, or SQL snippet—or tap a quick action below to debug, get hints, or generate practice quizzes!"
        )
    ),
    val tutorIsStreaming: Boolean = false,
    val tutorError: String? = null,
    val tutorByteExpression: ByteExpression = ByteExpression.HAPPY
)

class CodeverseViewModel(
    private val userId: String,
    private val initialDisplayName: String,
    private val initialEmail: String,
    private val localDb: CodeverseDatabase,
    private val prefsStore: UserPreferencesStore,
    private val cloudRepo: CloudProgressRepository,
    private val aiTutorRepo: AiTutorRepository = AiTutorRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CodeverseUiState(
            userId = userId,
            displayName = initialDisplayName.ifBlank { "Codeverse Explorer" },
            email = initialEmail
        )
    )
    val uiState: StateFlow<CodeverseUiState> = _uiState.asStateFlow()

    private var tutorJob: Job? = null

    init {
        observeLocalPreferences()
        observeLocalProjects()
        if (userId.isNotBlank()) {
            initializeAndObserveCloud()
        }
    }

    private fun observeLocalPreferences() {
        viewModelScope.launch {
            prefsStore.preferencesFlow.collect { prefs: LocalPreferences ->
                val lang = AppLanguage.fromCode(prefs.languageCode)
                _uiState.update { state ->
                    state.copy(
                        language = lang,
                        strings = CodeverseI18n.stringsFor(lang),
                        dailyGoalMinutes = prefs.dailyGoalMinutes,
                        learningTrack = prefs.learningTrack,
                        onboardingDone = prefs.onboardingDone,
                        darkThemeEnabled = prefs.darkThemeEnabled,
                        voiceNarrationEnabled = prefs.voiceNarrationEnabled,
                        soundEffectsEnabled = prefs.soundEffectsEnabled,
                        dailyReminderEnabled = prefs.dailyReminderEnabled,
                        byteColorTheme = ByteColorTheme.fromId(prefs.byteColorTheme),
                        byteAccessory = ByteAccessory.fromId(prefs.byteAccessory)
                    )
                }
            }
        }
    }

    private fun observeLocalProjects() {
        viewModelScope.launch {
            localDb.projectDao().observeAllProjects().collect { entities ->
                if (entities.isEmpty()) {
                    CurriculumSeed.starterProjects.forEach { starter ->
                        localDb.projectDao().upsertProject(starter.toEntity())
                    }
                } else {
                    val models = entities.map { it.toModel() }
                    _uiState.update { it.copy(savedProjects = models) }
                }
            }
        }
    }

    private fun initializeAndObserveCloud() {
        viewModelScope.launch {
            val s = _uiState.value
            cloudRepo.ensureProfileInitialized(
                userId = userId,
                displayName = s.displayName,
                languageCode = s.language.code,
                dailyGoalMinutes = s.dailyGoalMinutes,
                learningTrack = s.learningTrack,
                onboardingDone = s.onboardingDone
            ).onFailure { err ->
                Log.w(VM_TAG, "Cloud profile init deferred: ${err.message}")
            }
        }

        // Observe private user profile with two-tier Flow error unwrapping
        viewModelScope.launch {
            cloudRepo.observeUserProfile(userId)
                .catch { error ->
                    val fsError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsError == null && error is CancellationException) throw error
                    val root = fsError ?: error
                    Log.w(VM_TAG, "Profile sync error", root)
                    _uiState.update { it.copy(cloudSyncError = root.localizedMessage) }
                }
                .collect { cloudProfile ->
                    if (cloudProfile != null) {
                        val lang = AppLanguage.fromCode(cloudProfile.languageCode)
                        val theme = ByteColorTheme.fromId(cloudProfile.byteColorTheme)
                        val acc = ByteAccessory.fromId(cloudProfile.byteAccessory)
                        _uiState.update { st ->
                            st.copy(
                                displayName = cloudProfile.displayName.ifBlank { st.displayName },
                                language = lang,
                                strings = CodeverseI18n.stringsFor(lang),
                                dailyGoalMinutes = cloudProfile.dailyGoalMinutes.toInt(),
                                learningTrack = cloudProfile.learningTrack,
                                xp = cloudProfile.xp.toInt(),
                                level = cloudProfile.level.toInt(),
                                streakDays = cloudProfile.streakDays.toInt(),
                                gems = cloudProfile.gems.toInt(),
                                completedLessonIds = cloudProfile.completedLessonIds.toSet(),
                                unlockedAchievementIds = cloudProfile.unlockedAchievementIds.toSet(),
                                completedMissionIds = cloudProfile.completedMissionIds.toSet(),
                                defeatedBossWorldIds = cloudProfile.defeatedBossWorldIds.toSet(),
                                byteColorTheme = theme,
                                byteAccessory = acc,
                                ownedThemeIds = st.ownedThemeIds + theme.id,
                                ownedAccessoryIds = st.ownedAccessoryIds + acc.id,
                                onboardingDone = cloudProfile.onboardingDone || st.onboardingDone,
                                proMember = cloudProfile.proMember,
                                cloudSyncError = null
                            )
                        }
                    }
                }
        }

        // Observe cloud projects and sync into local Room cache
        viewModelScope.launch {
            cloudRepo.observeUserProjects(userId)
                .catch { error ->
                    val fsError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsError == null && error is CancellationException) throw error
                    Log.w(VM_TAG, "Cloud project sync error", fsError ?: error)
                }
                .collect { cloudProjects ->
                    if (cloudProjects.isNotEmpty()) {
                        localDb.projectDao().upsertAll(cloudProjects.map { it.toEntity() })
                    }
                }
        }

        // Observe real-time leaderboard combined with community league peers
        viewModelScope.launch {
            cloudRepo.observeLeaderboard(userId)
                .catch { error ->
                    val fsError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsError == null && error is CancellationException) throw error
                    Log.w(VM_TAG, "Leaderboard observe error", fsError ?: error)
                }
                .collect { cloudEntries ->
                    val merged = (cloudEntries + CurriculumSeed.baselineLeaguePeers)
                        .distinctBy { it.userId }
                        .sortedByDescending { it.xp }
                    _uiState.update { it.copy(leaderboard = merged) }
                }
        }
    }

    // --- ONBOARDING & SETTINGS ACTIONS ---

    fun selectLanguage(language: AppLanguage) {
        viewModelScope.launch {
            prefsStore.setLanguage(language.code)
            _uiState.update {
                it.copy(
                    language = language,
                    strings = CodeverseI18n.stringsFor(language)
                )
            }
            if (userId.isNotBlank()) {
                cloudRepo.updateProfileFields(userId, mapOf("languageCode" to language.code))
            }
        }
    }

    fun saveLearningGoal(minutes: Int, track: String) {
        val safeMinutes = minutes.coerceIn(5, 120)
        viewModelScope.launch {
            prefsStore.setLearningGoal(safeMinutes, track)
            _uiState.update { it.copy(dailyGoalMinutes = safeMinutes, learningTrack = track) }
            if (userId.isNotBlank()) {
                cloudRepo.updateProfileFields(
                    userId,
                    mapOf("dailyGoalMinutes" to safeMinutes, "learningTrack" to track)
                )
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            prefsStore.setOnboardingDone(true)
            _uiState.update { it.copy(onboardingDone = true) }
            if (userId.isNotBlank()) {
                cloudRepo.updateProfileFields(userId, mapOf("onboardingDone" to true))
            }
        }
    }

    fun toggleDarkTheme(enabled: Boolean) {
        viewModelScope.launch { prefsStore.setDarkTheme(enabled) }
    }

    fun toggleVoiceNarration(enabled: Boolean) {
        viewModelScope.launch { prefsStore.setVoiceNarration(enabled) }
    }

    fun toggleSoundEffects(enabled: Boolean) {
        viewModelScope.launch { prefsStore.setSoundEffects(enabled) }
    }

    fun toggleDailyReminder(enabled: Boolean) {
        viewModelScope.launch { prefsStore.setDailyReminder(enabled) }
    }

    fun updateDisplayName(newName: String) {
        val safe = newName.trim().take(50)
        if (safe.isBlank()) return
        _uiState.update { it.copy(displayName = safe) }
        viewModelScope.launch {
            if (userId.isNotBlank()) {
                cloudRepo.updateProfileFields(userId, mapOf("displayName" to safe))
                val s = _uiState.value
                cloudRepo.syncLeaderboardEntry(userId, safe, "cyber_bot", s.xp, s.level, s.streakDays)
            }
        }
    }

    fun activateProPlan(active: Boolean) {
        _uiState.update {
            it.copy(
                proMember = active,
                statusBanner = if (active) "Codeverse Pro unlocked! Unlimited Hearts & Pro Tracks active." else "Returned to Free Explorer plan."
            )
        }
        viewModelScope.launch {
            if (userId.isNotBlank()) {
                cloudRepo.updateProfileFields(userId, mapOf("proMember" to active))
            }
        }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(statusBanner = null) }
    }

    // --- LEARNING, GAMIFICATION & BOSS BATTLES ---

    fun completeLessonQuiz(
        lessonId: String,
        lessonTitle: String,
        worldId: String,
        correctCount: Int,
        totalCount: Int,
        baseXp: Int,
        baseGems: Int
    ) {
        val accuracy = if (totalCount > 0) ((correctCount * 100) / totalCount) else 100
        val bonusXp = if (accuracy == 100) 25 else 0
        val earnedXp = baseXp + bonusXp
        val earnedGems = baseGems + if (accuracy >= 80) 10 else 0

        val summary = LessonResultSummary(
            lessonId = lessonId,
            lessonTitle = lessonTitle,
            correctCount = correctCount,
            totalCount = totalCount,
            xpEarned = earnedXp,
            gemsEarned = earnedGems,
            accuracyPercent = accuracy
        )

        val current = _uiState.value
        val newXp = current.xp + earnedXp
        val newLevel = calculateLevel(newXp)
        val newGems = current.gems + earnedGems
        val newLessons = current.completedLessonIds + lessonId
        val newMissions = current.completedMissionIds + "mission_lesson"
        val newAchievements = buildSet {
            addAll(current.unlockedAchievementIds)
            add("ach_first_lesson")
            if (newXp >= 500) add("ach_streak_5")
        }

        _uiState.update {
            it.copy(
                xp = newXp,
                level = newLevel,
                gems = newGems,
                completedLessonIds = newLessons,
                completedMissionIds = newMissions,
                unlockedAchievementIds = newAchievements,
                lastLessonResult = summary
            )
        }

        viewModelScope.launch {
            localDb.completedLessonDao().upsertCompletedLesson(
                CompletedLessonEntity(
                    lessonId = lessonId,
                    worldId = worldId,
                    bestAccuracyPercent = accuracy,
                    completedAtMillis = System.currentTimeMillis()
                )
            )
            syncProgressToCloud()
        }
    }

    fun recordBossVictory(worldId: String, xpEarned: Int, gemsEarned: Int) {
        val current = _uiState.value
        val newXp = current.xp + xpEarned
        val newLevel = calculateLevel(newXp)
        val newGems = current.gems + gemsEarned
        val newBosses = current.defeatedBossWorldIds + worldId
        val newMissions = current.completedMissionIds + "mission_boss"
        val newAchievements = buildSet {
            addAll(current.unlockedAchievementIds)
            add("ach_boss_slayer")
            if (newXp >= 500) add("ach_streak_5")
        }

        _uiState.update {
            it.copy(
                xp = newXp,
                level = newLevel,
                gems = newGems,
                defeatedBossWorldIds = newBosses,
                completedMissionIds = newMissions,
                unlockedAchievementIds = newAchievements,
                statusBanner = "Boss Defeated! +$xpEarned XP & +$gemsEarned Gems"
            )
        }
        viewModelScope.launch { syncProgressToCloud() }
    }

    fun recordPracticeXp(xpEarned: Int, gemsEarned: Int) {
        val current = _uiState.value
        val newXp = current.xp + xpEarned
        val newLevel = calculateLevel(newXp)
        val newGems = current.gems + gemsEarned
        _uiState.update {
            it.copy(
                xp = newXp,
                level = newLevel,
                gems = newGems,
                statusBanner = "Practice Complete! +$xpEarned XP & +$gemsEarned Gems"
            )
        }
        viewModelScope.launch { syncProgressToCloud() }
    }

    // --- BYTE MASCOT CUSTOMIZATION ---

    fun equipOrBuyTheme(theme: ByteColorTheme) {
        val current = _uiState.value
        val alreadyOwned = theme.id in current.ownedThemeIds || theme.gemCost == 0
        if (!alreadyOwned && current.gems < theme.gemCost) {
            _uiState.update { it.copy(statusBanner = "Need ${theme.gemCost} Gems to unlock ${theme.title}.") }
            return
        }
        val remainingGems = if (alreadyOwned) current.gems else current.gems - theme.gemCost
        _uiState.update {
            it.copy(
                byteColorTheme = theme,
                gems = remainingGems,
                ownedThemeIds = it.ownedThemeIds + theme.id,
                unlockedAchievementIds = it.unlockedAchievementIds + "ach_stylist",
                statusBanner = "${theme.title} equipped on BYTE!"
            )
        }
        viewModelScope.launch {
            prefsStore.setByteAppearance(theme.id, _uiState.value.byteAccessory.id)
            syncProgressToCloud()
        }
    }

    fun equipOrBuyAccessory(accessory: ByteAccessory) {
        val current = _uiState.value
        val alreadyOwned = accessory.id in current.ownedAccessoryIds || accessory.gemCost == 0
        if (!alreadyOwned && current.gems < accessory.gemCost) {
            _uiState.update { it.copy(statusBanner = "Need ${accessory.gemCost} Gems to unlock ${accessory.title}.") }
            return
        }
        val remainingGems = if (alreadyOwned) current.gems else current.gems - accessory.gemCost
        _uiState.update {
            it.copy(
                byteAccessory = accessory,
                gems = remainingGems,
                ownedAccessoryIds = it.ownedAccessoryIds + accessory.id,
                unlockedAchievementIds = it.unlockedAchievementIds + "ach_stylist",
                statusBanner = "${accessory.title} equipped on BYTE!"
            )
        }
        viewModelScope.launch {
            prefsStore.setByteAppearance(_uiState.value.byteColorTheme.id, accessory.id)
            syncProgressToCloud()
        }
    }

    // --- CODING PLAYGROUND ACTIONS ---

    fun selectCodeTab(tab: CodeFileTab) {
        _uiState.update { it.copy(activeFileTab = tab) }
    }

    fun updateActiveCode(newCode: String) {
        _uiState.update { state ->
            when (state.activeFileTab) {
                CodeFileTab.HTML -> state.copy(htmlCode = newCode)
                CodeFileTab.CSS -> state.copy(cssCode = newCode)
                CodeFileTab.JS -> state.copy(jsCode = newCode)
            }
        }
    }

    fun updateProjectMeta(title: String, description: String) {
        _uiState.update { it.copy(activeProjectTitle = title, activeProjectDescription = description) }
    }

    fun runPlaygroundPreview() {
        val wasMissionDone = "mission_playground" in _uiState.value.completedMissionIds
        _uiState.update {
            it.copy(
                previewNonce = it.previewNonce + 1,
                consoleLogs = listOf(
                    ConsoleLogEntry(
                        id = System.currentTimeMillis(),
                        level = "log",
                        message = "Compiled HTML + CSS + JS bundle successfully."
                    )
                ),
                completedMissionIds = it.completedMissionIds + "mission_playground",
                xp = if (!wasMissionDone) it.xp + 40 else it.xp,
                gems = if (!wasMissionDone) it.gems + 10 else it.gems
            )
        }
        if (!wasMissionDone) {
            viewModelScope.launch { syncProgressToCloud() }
        }
    }

    fun appendConsoleLog(level: String, message: String) {
        _uiState.update { state ->
            val next = (state.consoleLogs + ConsoleLogEntry(System.nanoTime(), level, message)).takeLast(30)
            state.copy(consoleLogs = next)
        }
    }

    fun resetPlaygroundCode() {
        val defaultStarter = CurriculumSeed.starterProjects.first()
        _uiState.update {
            it.copy(
                htmlCode = defaultStarter.htmlCode,
                cssCode = defaultStarter.cssCode,
                jsCode = defaultStarter.jsCode,
                consoleLogs = emptyList(),
                previewNonce = it.previewNonce + 1,
                statusBanner = "Reset editor to Neon Profile Card template."
            )
        }
    }

    fun loadProjectIntoPlayground(project: CodeProjectModel) {
        _uiState.update {
            it.copy(
                activeProjectId = project.id,
                activeProjectTitle = project.title,
                activeProjectDescription = project.description,
                htmlCode = project.htmlCode,
                cssCode = project.cssCode,
                jsCode = project.jsCode,
                activeFileTab = CodeFileTab.HTML,
                consoleLogs = emptyList(),
                previewNonce = it.previewNonce + 1
            )
        }
    }

    fun createNewProject(title: String = "My Codeverse App") {
        val newId = "proj_${System.currentTimeMillis()}"
        _uiState.update {
            it.copy(
                activeProjectId = newId,
                activeProjectTitle = title,
                activeProjectDescription = "Custom HTML, CSS & JavaScript sandbox",
                htmlCode = "<main class=\"box\">\n  <h1>$title</h1>\n  <p>Edit HTML, CSS, and JS then press Run!</p>\n  <button onclick=\"ping()\">Say Hello</button>\n</main>",
                cssCode = "body { background: #191B30; color: #fff; font-family: system-ui; padding: 24px; }\n.box { background: #252747; padding: 20px; border-radius: 18px; border: 1px solid #3e4274; }\nbutton { background: #8050E8; color: #fff; border: none; padding: 10px 18px; border-radius: 10px; font-weight: 700; }",
                jsCode = "function ping() {\n  console.log('Hello from $title!');\n  alert('BYTE says: Great job!');\n}",
                activeFileTab = CodeFileTab.HTML,
                previewNonce = it.previewNonce + 1
            )
        }
    }

    fun saveCurrentProject(customTitle: String? = null) {
        val s = _uiState.value
        val finalTitle = (customTitle ?: s.activeProjectTitle).trim().take(80).ifBlank { "Untitled App" }
        val project = CodeProjectModel(
            id = s.activeProjectId.ifBlank { "proj_${System.currentTimeMillis()}" },
            title = finalTitle,
            description = s.activeProjectDescription.ifBlank { "Built in Codeverse Playground" },
            htmlCode = s.htmlCode,
            cssCode = s.cssCode,
            jsCode = s.jsCode,
            templateTag = "HTML/CSS/JS",
            updatedAtMillis = System.currentTimeMillis()
        )

        _uiState.update {
            it.copy(
                activeProjectTitle = finalTitle,
                unlockedAchievementIds = it.unlockedAchievementIds + "ach_playground_creator",
                statusBanner = "Saved \"$finalTitle\" to Project Gallery!"
            )
        }

        viewModelScope.launch {
            localDb.projectDao().upsertProject(project.toEntity())
            if (userId.isNotBlank()) {
                cloudRepo.saveProject(userId, project)
                syncProgressToCloud()
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            localDb.projectDao().deleteProjectById(projectId)
            if (userId.isNotBlank()) {
                cloudRepo.deleteProject(userId, projectId)
            }
            _uiState.update { it.copy(statusBanner = "Project deleted.") }
        }
    }

    // --- REAL GEMINI AI TUTOR STREAMING ---

    fun sendTutorPrompt(rawInput: String, action: TutorQuickAction? = null) {
        val cleaned = rawInput.trim()
        if (cleaned.isEmpty() || _uiState.value.tutorIsStreaming) return

        val finalPrompt = if (action != null) "${action.promptPrefix}$cleaned" else cleaned
        val userMsg = TutorMessage(
            id = "u_${System.currentTimeMillis()}",
            isFromUser = true,
            text = if (action != null) "[${action.label}]\n$cleaned" else cleaned
        )
        val assistantMsgId = "a_${System.currentTimeMillis() + 1}"
        val placeholderMsg = TutorMessage(
            id = assistantMsgId,
            isFromUser = false,
            text = "",
            isStreaming = true
        )

        val historyPairs = _uiState.value.tutorMessages.map { it.isFromUser to it.text }
        val wasMissionDone = "mission_tutor" in _uiState.value.completedMissionIds

        _uiState.update {
            it.copy(
                tutorMessages = it.tutorMessages + userMsg + placeholderMsg,
                tutorIsStreaming = true,
                tutorError = null,
                tutorByteExpression = ByteExpression.THINKING,
                completedMissionIds = it.completedMissionIds + "mission_tutor",
                unlockedAchievementIds = it.unlockedAchievementIds + "ach_ai_explorer",
                xp = if (!wasMissionDone) it.xp + 45 else it.xp,
                gems = if (!wasMissionDone) it.gems + 15 else it.gems
            )
        }

        if (!wasMissionDone) {
            viewModelScope.launch { syncProgressToCloud() }
        }

        tutorJob?.cancel()
        tutorJob = viewModelScope.launch {
            try {
                aiTutorRepo.streamTutorResponse(
                    userPrompt = finalPrompt,
                    language = _uiState.value.language,
                    conversationContext = historyPairs
                ).collect { accumulated ->
                    _uiState.update { state ->
                        val updatedList = state.tutorMessages.map { msg ->
                            if (msg.id == assistantMsgId) msg.copy(text = accumulated, isStreaming = true)
                            else msg
                        }
                        state.copy(
                            tutorMessages = updatedList,
                            tutorByteExpression = ByteExpression.FOCUSED
                        )
                    }
                }
                _uiState.update { state ->
                    val finalized = state.tutorMessages.map { msg ->
                        if (msg.id == assistantMsgId) msg.copy(isStreaming = false) else msg
                    }
                    state.copy(
                        tutorMessages = finalized,
                        tutorIsStreaming = false,
                        tutorByteExpression = ByteExpression.HAPPY
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(VM_TAG, "AI Tutor failed", e)
                _uiState.update { state ->
                    val cleanedList = state.tutorMessages.filterNot { it.id == assistantMsgId && it.text.isBlank() }
                        .map { if (it.id == assistantMsgId) it.copy(isStreaming = false) else it }
                    state.copy(
                        tutorMessages = cleanedList,
                        tutorIsStreaming = false,
                        tutorError = e.localizedMessage ?: "Unable to reach BYTE AI right now.",
                        tutorByteExpression = ByteExpression.CONFUSED
                    )
                }
            }
        }
    }

    fun clearTutorError() {
        _uiState.update { it.copy(tutorError = null) }
    }

    fun clearTutorChat() {
        tutorJob?.cancel()
        _uiState.update {
            it.copy(
                tutorIsStreaming = false,
                tutorError = null,
                tutorByteExpression = ByteExpression.HAPPY,
                tutorMessages = listOf(
                    TutorMessage(
                        id = "reset_${System.currentTimeMillis()}",
                        isFromUser = false,
                        text = "Fresh terminal! Ask me to explain code, find bugs, give hints, or create a coding quiz."
                    )
                )
            )
        }
    }

    private suspend fun syncProgressToCloud() {
        if (userId.isBlank()) return
        val s = _uiState.value
        val updates = mapOf(
            "xp" to s.xp,
            "level" to s.level,
            "streakDays" to s.streakDays,
            "gems" to s.gems,
            "completedLessonIds" to s.completedLessonIds.toList().take(200),
            "unlockedAchievementIds" to s.unlockedAchievementIds.toList().take(100),
            "completedMissionIds" to s.completedMissionIds.toList().take(50),
            "defeatedBossWorldIds" to s.defeatedBossWorldIds.toList().take(20),
            "byteColorTheme" to s.byteColorTheme.id,
            "byteAccessory" to s.byteAccessory.id
        )
        cloudRepo.updateProfileFields(userId, updates)
        cloudRepo.syncLeaderboardEntry(
            userId = userId,
            displayName = s.displayName,
            avatarPreset = "cyber_bot",
            xp = s.xp,
            level = s.level,
            streakDays = s.streakDays
        )
    }

    private fun calculateLevel(xp: Int): Int = (xp / 250) + 1
}

private fun CodeProjectModel.toEntity() = ProjectEntity(
    id = id,
    title = title,
    description = description,
    htmlCode = htmlCode,
    cssCode = cssCode,
    jsCode = jsCode,
    templateTag = templateTag,
    updatedAtMillis = updatedAtMillis
)

private fun ProjectEntity.toModel() = CodeProjectModel(
    id = id,
    title = title,
    description = description,
    htmlCode = htmlCode,
    cssCode = cssCode,
    jsCode = jsCode,
    templateTag = templateTag,
    updatedAtMillis = updatedAtMillis
)
