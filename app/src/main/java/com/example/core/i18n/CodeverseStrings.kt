package com.example.core.i18n

enum class AppLanguage(val code: String, val nativeName: String, val subtitle: String) {
    ENGLISH("en", "English", "Global developer standard"),
    HINGLISH("hinglish", "Hinglish", "Hindi + English mix, code in English"),
    HINDI("hi", "हिन्दी", "सरल हिंदी निर्देश, कोड इंग्लिश में");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}

/**
 * Provides localized UI copy in English, Hinglish, and Hindi while keeping all
 * programming keywords, code blocks, and syntax strictly in English.
 */
data class LocalizedStrings(
    val tagline: String,
    val navLearn: String,
    val navCode: String,
    val navPractice: String,
    val navTutor: String,
    val navProfile: String,
    val continueLearning: String,
    val dailyMissions: String,
    val worldsRoadmap: String,
    val startLesson: String,
    val runCode: String,
    val saveProject: String,
    val livePreview: String,
    val resetCode: String,
    val bossBattle: String,
    val askByteTutor: String,
    val signInGoogle: String,
    val streakLabel: String,
    val xpLabel: String,
    val gemsLabel: String,
    val levelLabel: String,
    val leaderboardTitle: String,
    val achievementsTitle: String,
    val mascotStudioTitle: String,
    val settingsTitle: String,
    val projectGalleryTitle: String,
    val byteWelcomeLine: String,
    val byteCorrectLine: String,
    val byteWrongLine: String,
    val byteCompleteLine: String
)

object CodeverseI18n {
    fun stringsFor(language: AppLanguage): LocalizedStrings = when (language) {
        AppLanguage.ENGLISH -> LocalizedStrings(
            tagline = "Learn. Code. Play. Create.",
            navLearn = "Learn",
            navCode = "Code",
            navPractice = "Practice",
            navTutor = "AI Tutor",
            navProfile = "Profile",
            continueLearning = "Continue Learning",
            dailyMissions = "Daily Missions",
            worldsRoadmap = "7-World Full-Stack Roadmap",
            startLesson = "Start Lesson",
            runCode = "Run",
            saveProject = "Save",
            livePreview = "Preview",
            resetCode = "Reset",
            bossBattle = "Boss Battle",
            askByteTutor = "Ask BYTE AI anything about code...",
            signInGoogle = "Continue with Google",
            streakLabel = "Day Streak",
            xpLabel = "Total XP",
            gemsLabel = "Gems",
            levelLabel = "Level",
            leaderboardTitle = "Weekly Leaderboard",
            achievementsTitle = "Achievements",
            mascotStudioTitle = "BYTE Studio",
            settingsTitle = "Settings",
            projectGalleryTitle = "Project Gallery",
            byteWelcomeLine = "Hey coder! I'm BYTE, your futuristic coding copilot. Ready to level up?",
            byteCorrectLine = "Clean execution! Zero bugs detected — keep that combo going!",
            byteWrongLine = "Close call! Every bug is just a clue. Let's inspect and try again!",
            byteCompleteLine = "Mission complete! You just unlocked fresh XP and sharpened your stack!"
        )

        AppLanguage.HINGLISH -> LocalizedStrings(
            tagline = "Seekho. Code Karo. Khelo. Banao.",
            navLearn = "Seekho",
            navCode = "Code",
            navPractice = "Practice",
            navTutor = "AI Tutor",
            navProfile = "Profile",
            continueLearning = "Aage Seekhein",
            dailyMissions = "Daily Missions",
            worldsRoadmap = "7-World Full-Stack Roadmap",
            startLesson = "Lesson Shuru Karein",
            runCode = "Run Karein",
            saveProject = "Save Karein",
            livePreview = "Live Preview",
            resetCode = "Reset",
            bossBattle = "Boss Battle",
            askByteTutor = "BYTE AI se code ke baare mein kuch bhi poochein...",
            signInGoogle = "Google ke saath Continue karein",
            streakLabel = "Day Streak",
            xpLabel = "Total XP",
            gemsLabel = "Gems",
            levelLabel = "Level",
            leaderboardTitle = "Weekly Leaderboard",
            achievementsTitle = "Achievements",
            mascotStudioTitle = "BYTE Studio",
            settingsTitle = "Settings",
            projectGalleryTitle = "Project Gallery",
            byteWelcomeLine = "Hey coder! Main hoon BYTE, aapka futuristic coding dost. Shuru karein?",
            byteCorrectLine = "Zabardast execution! Ek bhi bug nahi — aise hi aage badho!",
            byteWrongLine = "Koi baat nahi! Har bug ek nayi seekh hai. Dobara try karo!",
            byteCompleteLine = "Shabaash! Mission complete ho gaya aur naye XP unlock ho gaye!"
        )

        AppLanguage.HINDI -> LocalizedStrings(
            tagline = "सीखें. कोड करें. खेलें. बनाएं.",
            navLearn = "सीखें",
            navCode = "कोड",
            navPractice = "अभ्यास",
            navTutor = "AI ट्यूटर",
            navProfile = "प्रोफ़ाइल",
            continueLearning = "आगे सीखें",
            dailyMissions = "दैनिक मिशन",
            worldsRoadmap = "7-वर्ल्ड फुल-स्टैक रोडमैप",
            startLesson = "पाठ शुरू करें",
            runCode = "चलाएं",
            saveProject = "सहेजें",
            livePreview = "प्रीव्यू",
            resetCode = "रीसेट",
            bossBattle = "बॉस बैटल",
            askByteTutor = "BYTE AI से कोड के बारे में कुछ भी पूछें...",
            signInGoogle = "Google से जारी रखें",
            streakLabel = "दिन की स्ट्रीक",
            xpLabel = "कुल XP",
            gemsLabel = "जेम्स",
            levelLabel = "लेवल",
            leaderboardTitle = "साप्ताहिक लीडरबोर्ड",
            achievementsTitle = "उपलब्धियां",
            mascotStudioTitle = "BYTE स्टूडियो",
            settingsTitle = "सेटिंग्स",
            projectGalleryTitle = "प्रोजेक्ट गैलरी",
            byteWelcomeLine = "नमस्ते कोडर! मैं BYTE हूँ, आपका फ्यूचरिस्टिक कोडिंग साथी। तैयार हैं?",
            byteCorrectLine = "शानदार! बिल्कुल सही कोड — ऐसे ही आगे बढ़ते रहें!",
            byteWrongLine = "कोई बात नहीं! हर त्रुटि हमें बेहतर बनाती है। फिर से प्रयास करें!",
            byteCompleteLine = "बधाई हो! आपने यह पाठ पूरा कर लिया और नए XP अर्जित किए!"
        )
    }
}
