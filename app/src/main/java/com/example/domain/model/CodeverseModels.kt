package com.example.domain.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.NeonLavender
import com.example.ui.theme.SoftGreen
import com.example.ui.theme.WarmYellow

// --- BYTE MASCOT MODELS ---

enum class ByteExpression(val label: String) {
    HAPPY("Happy"),
    EXCITED("Excited"),
    THINKING("Thinking"),
    CONFUSED("Confused"),
    ENCOURAGING("Encouraging"),
    CELEBRATING("Celebrating"),
    FOCUSED("Focused")
}

enum class ByteColorTheme(
    val id: String,
    val title: String,
    val bodyPrimary: Color,
    val bodySecondary: Color,
    val accent: Color,
    val eyeGlow: Color,
    val gemCost: Int
) {
    LAVENDER_PRIME(
        id = "lavender_prime",
        title = "Lavender Prime",
        bodyPrimary = Color(0xFFF3F1FF),
        bodySecondary = NeonLavender,
        accent = CyberPurple,
        eyeGlow = CyberCyan,
        gemCost = 0
    ),
    CYBER_CYAN(
        id = "cyber_cyan",
        title = "Cyber Cyan",
        bodyPrimary = Color(0xFFEAFBFF),
        bodySecondary = Color(0xFF7BE7FF),
        accent = CyberCyan,
        eyeGlow = Color(0xFF38E59D),
        gemCost = 80
    ),
    SOLAR_GOLD(
        id = "solar_gold",
        title = "Solar Nova",
        bodyPrimary = Color(0xFFFFF8E6),
        bodySecondary = Color(0xFFFFD87A),
        accent = WarmYellow,
        eyeGlow = Color(0xFF22C5E9),
        gemCost = 140
    ),
    EMERALD_PULSE(
        id = "emerald_pulse",
        title = "Matrix Mint",
        bodyPrimary = Color(0xFFE9FFF6),
        bodySecondary = Color(0xFF7BF2BE),
        accent = SoftGreen,
        eyeGlow = NeonLavender,
        gemCost = 180
    ),
    MIDNIGHT_STEALTH(
        id = "midnight_stealth",
        title = "Stealth Neon",
        bodyPrimary = Color(0xFF323663),
        bodySecondary = Color(0xFF545A96),
        accent = CyberPurple,
        eyeGlow = CyberCyan,
        gemCost = 250
    );

    companion object {
        fun fromId(id: String): ByteColorTheme =
            entries.firstOrNull { it.id == id } ?: LAVENDER_PRIME
    }
}

enum class ByteAccessory(
    val id: String,
    val title: String,
    val description: String,
    val gemCost: Int
) {
    NONE("none", "Classic Antenna", "Original sleek Codeverse antenna", 0),
    NEON_VISOR("neon_visor", "Holo Visor", "Translucent cyan coding HUD", 60),
    DEV_HEADSET("dev_headset", "Synth Headset", "Studio-grade neon headphones", 110),
    CODE_CROWN("code_crown", "Binary Crown", "For full-stack champions", 220),
    PIXEL_CAPE("pixel_cape", "Quantum Cape", "Purple energy holo-cape", 175);

    companion object {
        fun fromId(id: String): ByteAccessory =
            entries.firstOrNull { it.id == id } ?: NONE
    }
}

// --- CURRICULUM MODELS ---

data class LearningWorld(
    val id: String,
    val number: Int,
    val title: String,
    val subtitle: String,
    val badgeTag: String,
    val accentHex: Long,
    val modules: List<CourseModule>,
    val bossName: String,
    val bossTitle: String,
    val bossQuestions: List<QuizQuestion>
)

data class CourseModule(
    val id: String,
    val worldId: String,
    val title: String,
    val summary: String,
    val estimatedMinutes: Int,
    val xpReward: Int,
    val lessons: List<Lesson>
)

data class Lesson(
    val id: String,
    val moduleId: String,
    val worldId: String,
    val title: String,
    val subtitle: String,
    val xpReward: Int,
    val gemReward: Int,
    val steps: List<LessonStep>,
    val quizQuestions: List<QuizQuestion>
)

data class LessonStep(
    val id: String,
    val title: String,
    val explanation: String,
    val codeSnippet: String,
    val codeLanguage: String,
    val byteTip: String,
    val interactiveChallenge: InlineChallenge? = null
)

data class InlineChallenge(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val feedbackCorrect: String,
    val feedbackWrong: String
)

data class QuizQuestion(
    val id: String,
    val prompt: String,
    val codeContext: String?,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val xpValue: Int = 25
)

// --- GAMIFICATION MODELS ---

data class DailyMission(
    val id: String,
    val title: String,
    val subtitle: String,
    val xpReward: Int,
    val gemReward: Int,
    val targetCount: Int
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val xpBonus: Int,
    val category: String
)

data class LeaderboardMember(
    val userId: String,
    val displayName: String,
    val avatarPreset: String,
    val xp: Int,
    val level: Int,
    val streakDays: Int,
    val league: String,
    val isCurrentUser: Boolean = false
)

data class LessonResultSummary(
    val lessonId: String,
    val lessonTitle: String,
    val correctCount: Int,
    val totalCount: Int,
    val xpEarned: Int,
    val gemsEarned: Int,
    val accuracyPercent: Int
)

// --- PLAYGROUND MODELS ---

enum class CodeFileTab(val fileName: String, val extension: String) {
    HTML("index.html", "HTML"),
    CSS("styles.css", "CSS"),
    JS("app.js", "JS")
}

data class CodeProjectModel(
    val id: String,
    val title: String,
    val description: String,
    val htmlCode: String,
    val cssCode: String,
    val jsCode: String,
    val templateTag: String,
    val updatedAtMillis: Long
)

data class ConsoleLogEntry(
    val id: Long,
    val level: String, // "log", "warn", "error"
    val message: String
)

// --- AI TUTOR MODELS ---

enum class TutorQuickAction(val label: String, val promptPrefix: String) {
    EXPLAIN_CODE("Explain Code", "Explain what this code does clearly with a quick summary and key takeaway:\n\n"),
    FIND_ERRORS("Find Errors", "Inspect this code for bugs, syntax mistakes, or logic errors and show the corrected version:\n\n"),
    GIVE_HINT("Give Hint", "Give me a gentle Socratic hint without giving away the full solution right away for:\n\n"),
    LINE_BY_LINE("Line by Line", "Walk through this code line by line so a beginner can understand every statement:\n\n"),
    PRACTICE_QUESTIONS("Practice Quiz", "Generate 3 short multiple-choice coding practice questions with answers about:\n\n")
}

data class TutorMessage(
    val id: String,
    val isFromUser: Boolean,
    val text: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false
)
