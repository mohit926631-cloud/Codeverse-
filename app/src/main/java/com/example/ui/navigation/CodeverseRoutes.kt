package com.example.ui.navigation

object CodeverseRoutes {
    // Pre-Auth / Onboarding Routes (1-6)
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LANGUAGE_SELECTION = "language_selection"
    const val LOGIN_REGISTRATION = "login_registration"
    const val GOAL_SETUP = "goal_setup"
    const val MEET_BYTE = "meet_byte"

    // 5 Primary Tabs
    const val HOME = "home"
    const val PLAYGROUND = "playground"
    const val PRACTICE = "practice"
    const val AI_TUTOR = "ai_tutor"
    const val PROFILE = "profile"

    // Learning Flow Routes (8-12)
    const val ROADMAP = "roadmap"
    const val COURSE_DETAILS = "course_details/{worldId}"
    const val INTERACTIVE_LESSON = "interactive_lesson/{lessonId}"
    const val EXERCISE_QUIZ = "exercise_quiz/{lessonId}"
    const val LESSON_RESULTS = "lesson_results"

    // Code & Practice Secondary Routes (14, 16, 24)
    const val LIVE_PREVIEW = "live_preview"
    const val BOSS_BATTLE = "boss_battle/{worldId}"
    const val PROJECT_GALLERY = "project_gallery"

    // Profile Secondary Routes (19-23)
    const val LEADERBOARD = "leaderboard"
    const val ACHIEVEMENTS = "achievements"
    const val MASCOT_STUDIO = "mascot_studio"
    const val SETTINGS = "settings"
    const val PREMIUM = "premium"

    fun courseDetails(worldId: String) = "course_details/$worldId"
    fun interactiveLesson(lessonId: String) = "interactive_lesson/$lessonId"
    fun exerciseQuiz(lessonId: String) = "exercise_quiz/$lessonId"
    fun bossBattle(worldId: String) = "boss_battle/$worldId"

    val primaryTabRoutes = setOf(HOME, PLAYGROUND, PRACTICE, AI_TUTOR, PROFILE)
}
