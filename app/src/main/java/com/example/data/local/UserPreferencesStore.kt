package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.codeverseDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "codeverse_user_prefs"
)

data class LocalPreferences(
    val languageCode: String = "en",
    val dailyGoalMinutes: Int = 15,
    val learningTrack: String = "Full-Stack Web",
    val onboardingDone: Boolean = false,
    val darkThemeEnabled: Boolean = true,
    val voiceNarrationEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val dailyReminderEnabled: Boolean = true,
    val byteColorTheme: String = "lavender_prime",
    val byteAccessory: String = "none"
)

class UserPreferencesStore(private val context: Context) {
    private object Keys {
        val LANGUAGE = stringPreferencesKey("language_code")
        val DAILY_GOAL = intPreferencesKey("daily_goal_minutes")
        val LEARNING_TRACK = stringPreferencesKey("learning_track")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val VOICE_NARRATION = booleanPreferencesKey("voice_narration")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val DAILY_REMINDER = booleanPreferencesKey("daily_reminder")
        val BYTE_THEME = stringPreferencesKey("byte_theme")
        val BYTE_ACCESSORY = stringPreferencesKey("byte_accessory")
    }

    val preferencesFlow: Flow<LocalPreferences> = context.codeverseDataStore.data.map { prefs ->
        LocalPreferences(
            languageCode = prefs[Keys.LANGUAGE] ?: "en",
            dailyGoalMinutes = prefs[Keys.DAILY_GOAL] ?: 15,
            learningTrack = prefs[Keys.LEARNING_TRACK] ?: "Full-Stack Web",
            onboardingDone = prefs[Keys.ONBOARDING_DONE] ?: false,
            darkThemeEnabled = prefs[Keys.DARK_THEME] ?: true,
            voiceNarrationEnabled = prefs[Keys.VOICE_NARRATION] ?: true,
            soundEffectsEnabled = prefs[Keys.SOUND_EFFECTS] ?: true,
            dailyReminderEnabled = prefs[Keys.DAILY_REMINDER] ?: true,
            byteColorTheme = prefs[Keys.BYTE_THEME] ?: "lavender_prime",
            byteAccessory = prefs[Keys.BYTE_ACCESSORY] ?: "none"
        )
    }

    suspend fun setLanguage(code: String) {
        context.codeverseDataStore.edit { it[Keys.LANGUAGE] = code }
    }

    suspend fun setLearningGoal(minutes: Int, track: String) {
        context.codeverseDataStore.edit {
            it[Keys.DAILY_GOAL] = minutes
            it[Keys.LEARNING_TRACK] = track
        }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.codeverseDataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.codeverseDataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    suspend fun setVoiceNarration(enabled: Boolean) {
        context.codeverseDataStore.edit { it[Keys.VOICE_NARRATION] = enabled }
    }

    suspend fun setSoundEffects(enabled: Boolean) {
        context.codeverseDataStore.edit { it[Keys.SOUND_EFFECTS] = enabled }
    }

    suspend fun setDailyReminder(enabled: Boolean) {
        context.codeverseDataStore.edit { it[Keys.DAILY_REMINDER] = enabled }
    }

    suspend fun setByteAppearance(colorThemeId: String, accessoryId: String) {
        context.codeverseDataStore.edit {
            it[Keys.BYTE_THEME] = colorThemeId
            it[Keys.BYTE_ACCESSORY] = accessoryId
        }
    }
}
