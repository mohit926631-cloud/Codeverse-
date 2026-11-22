package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.core.tts.ByteVoiceController
import com.example.data.local.CodeverseDatabase
import com.example.data.local.UserPreferencesStore
import com.example.data.remote.CloudProgressRepository
import com.example.data.remote.signOutFromCodeverse
import com.example.data.seed.CurriculumSeed
import com.example.ui.components.CodeverseBottomBar
import com.example.ui.components.CodeverseNavigationRail
import com.example.ui.components.PrimaryTab
import com.example.ui.screens.code.CodingPlaygroundScreen
import com.example.ui.screens.code.LiveCodePreviewScreen
import com.example.ui.screens.code.ProjectGalleryScreen
import com.example.ui.screens.learn.CourseDetailsScreen
import com.example.ui.screens.learn.ExerciseQuizScreen
import com.example.ui.screens.learn.HomeDashboardScreen
import com.example.ui.screens.learn.InteractiveLessonScreen
import com.example.ui.screens.learn.LearningRoadmapScreen
import com.example.ui.screens.learn.LessonResultsScreen
import com.example.ui.screens.onboarding.LearningGoalSetupScreen
import com.example.ui.screens.onboarding.MeetByteMascotScreen
import com.example.ui.screens.practice.BossBattleScreen
import com.example.ui.screens.practice.PracticeGameSelectionScreen
import com.example.ui.screens.profile.AchievementsScreen
import com.example.ui.screens.profile.LeaderboardScreen
import com.example.ui.screens.profile.MascotCustomizationStudioScreen
import com.example.ui.screens.profile.PremiumSubscriptionScreen
import com.example.ui.screens.profile.SettingsScreen
import com.example.ui.screens.profile.UserProfileScreen
import com.example.ui.screens.tutor.AiTutorChatScreen
import com.example.ui.theme.CodeverseTheme
import com.example.ui.viewmodel.CodeverseViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AuthenticatedCodeverseShell(
    currentUserId: String,
    displayName: String,
    email: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val voiceController = remember(context) { ByteVoiceController(context) }

    DisposableEffect(voiceController) {
        onDispose { voiceController.release() }
    }

    // ViewModel keyed strictly by authenticated userId with R.string.firestore_database_id resolved at DI boundary
    val viewModel: CodeverseViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val firestore = FirebaseFirestore.getInstance(databaseId)
                CodeverseViewModel(
                    userId = currentUserId,
                    initialDisplayName = displayName,
                    initialEmail = email,
                    localDb = CodeverseDatabase.getInstance(app),
                    prefsStore = UserPreferencesStore(app),
                    cloudRepo = CloudProgressRepository(firestore)
                )
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.statusBanner) {
        val msg = uiState.statusBanner
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissBanner()
        }
    }

    val speakIfEnabled: (String) -> Unit = { line ->
        if (uiState.voiceNarrationEnabled) {
            voiceController.speak(line, uiState.language.code)
        }
    }

    val navigatePrimaryTab: (PrimaryTab) -> Unit = { tab ->
        navController.navigate(tab.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val startDestination = remember(currentUserId) {
        if (uiState.onboardingDone) CodeverseRoutes.HOME else CodeverseRoutes.GOAL_SETUP
    }

    CodeverseTheme(darkTheme = uiState.darkThemeEnabled) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavigationRail = maxWidth >= 640.dp
            val showPrimaryNav = currentRoute in CodeverseRoutes.primaryTabRoutes

            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (showPrimaryNav && !useNavigationRail) {
                        CodeverseBottomBar(
                            currentRoute = currentRoute,
                            strings = uiState.strings,
                            onSelectTab = navigatePrimaryTab
                        )
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (showPrimaryNav && useNavigationRail) {
                        CodeverseNavigationRail(
                            currentRoute = currentRoute,
                            strings = uiState.strings,
                            onSelectTab = navigatePrimaryTab
                        )
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {
                            // 5. Learning Goal Setup
                            composable(CodeverseRoutes.GOAL_SETUP) {
                                LearningGoalSetupScreen(
                                    initialMinutes = uiState.dailyGoalMinutes,
                                    initialTrack = uiState.learningTrack,
                                    onSaveGoal = { minutes, track ->
                                        viewModel.saveLearningGoal(minutes, track)
                                        navController.navigate(CodeverseRoutes.MEET_BYTE)
                                    }
                                )
                            }

                            // 6. Meet BYTE Mascot
                            composable(CodeverseRoutes.MEET_BYTE) {
                                MeetByteMascotScreen(
                                    welcomeLine = uiState.strings.byteWelcomeLine,
                                    colorTheme = uiState.byteColorTheme,
                                    accessory = uiState.byteAccessory,
                                    onSpeak = speakIfEnabled,
                                    onEnterApp = {
                                        viewModel.completeOnboarding()
                                        navController.navigate(CodeverseRoutes.HOME) {
                                            popUpTo(CodeverseRoutes.GOAL_SETUP) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // 7. Home Dashboard (Tab 1: Learn)
                            composable(CodeverseRoutes.HOME) {
                                HomeDashboardScreen(
                                    uiState = uiState,
                                    onSpeakByte = speakIfEnabled,
                                    onOpenRoadmap = { navController.navigate(CodeverseRoutes.ROADMAP) },
                                    onOpenWorld = { wid -> navController.navigate(CodeverseRoutes.courseDetails(wid)) },
                                    onStartLesson = { lid -> navController.navigate(CodeverseRoutes.interactiveLesson(lid)) },
                                    onOpenPlayground = { navigatePrimaryTab(PrimaryTab.CODE) },
                                    onOpenBossBattle = { wid -> navController.navigate(CodeverseRoutes.bossBattle(wid)) },
                                    onOpenAiTutor = { navigatePrimaryTab(PrimaryTab.AI_TUTOR) },
                                    onOpenGallery = { navController.navigate(CodeverseRoutes.PROJECT_GALLERY) },
                                    onOpenMascotStudio = { navController.navigate(CodeverseRoutes.MASCOT_STUDIO) }
                                )
                            }

                            // 8. Learning Roadmap (All 7 Worlds)
                            composable(CodeverseRoutes.ROADMAP) {
                                LearningRoadmapScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onOpenWorld = { wid -> navController.navigate(CodeverseRoutes.courseDetails(wid)) },
                                    onOpenBossBattle = { wid -> navController.navigate(CodeverseRoutes.bossBattle(wid)) }
                                )
                            }

                            // 9. Course Details
                            composable(
                                route = CodeverseRoutes.COURSE_DETAILS,
                                arguments = listOf(navArgument("worldId") { type = NavType.StringType })
                            ) { entry ->
                                val worldId = entry.arguments?.getString("worldId") ?: "world_1"
                                CourseDetailsScreen(
                                    worldId = worldId,
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onStartLesson = { lid -> navController.navigate(CodeverseRoutes.interactiveLesson(lid)) },
                                    onStartBossBattle = { wid -> navController.navigate(CodeverseRoutes.bossBattle(wid)) }
                                )
                            }

                            // 10. Interactive Lesson
                            composable(
                                route = CodeverseRoutes.INTERACTIVE_LESSON,
                                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
                            ) { entry ->
                                val lessonId = entry.arguments?.getString("lessonId") ?: "w1_m1_l1"
                                InteractiveLessonScreen(
                                    lessonId = lessonId,
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onSpeakByte = speakIfEnabled,
                                    onStartQuiz = { lid -> navController.navigate(CodeverseRoutes.exerciseQuiz(lid)) }
                                )
                            }

                            // 11. Exercise & Quiz
                            composable(
                                route = CodeverseRoutes.EXERCISE_QUIZ,
                                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
                            ) { entry ->
                                val lessonId = entry.arguments?.getString("lessonId") ?: "w1_m1_l1"
                                val lesson = remember(lessonId) {
                                    CurriculumSeed.findLesson(lessonId) ?: CurriculumSeed.allLessons().first()
                                }
                                ExerciseQuizScreen(
                                    lessonId = lessonId,
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onSpeakByte = speakIfEnabled,
                                    onCompleteQuiz = { correct, total ->
                                        viewModel.completeLessonQuiz(
                                            lessonId = lesson.id,
                                            lessonTitle = lesson.title,
                                            worldId = lesson.worldId,
                                            correctCount = correct,
                                            totalCount = total,
                                            baseXp = lesson.xpReward,
                                            baseGems = lesson.gemReward
                                        )
                                        navController.navigate(CodeverseRoutes.LESSON_RESULTS) {
                                            popUpTo(CodeverseRoutes.INTERACTIVE_LESSON) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // 12. Lesson Results
                            composable(CodeverseRoutes.LESSON_RESULTS) {
                                LessonResultsScreen(
                                    uiState = uiState,
                                    onSpeakByte = speakIfEnabled,
                                    onContinueRoadmap = {
                                        navController.navigate(CodeverseRoutes.ROADMAP) {
                                            popUpTo(CodeverseRoutes.HOME) { inclusive = false }
                                        }
                                    },
                                    onOpenPlayground = { navigatePrimaryTab(PrimaryTab.CODE) }
                                )
                            }

                            // 13. Coding Playground (Tab 2: Code)
                            composable(CodeverseRoutes.PLAYGROUND) {
                                CodingPlaygroundScreen(
                                    uiState = uiState,
                                    onSelectTab = viewModel::selectCodeTab,
                                    onUpdateCode = viewModel::updateActiveCode,
                                    onRunPreview = {
                                        viewModel.runPlaygroundPreview()
                                        navController.navigate(CodeverseRoutes.LIVE_PREVIEW)
                                    },
                                    onResetCode = viewModel::resetPlaygroundCode,
                                    onSaveProject = { title -> viewModel.saveCurrentProject(title) },
                                    onOpenGallery = { navController.navigate(CodeverseRoutes.PROJECT_GALLERY) }
                                )
                            }

                            // 14. Live Code Preview
                            composable(CodeverseRoutes.LIVE_PREVIEW) {
                                LiveCodePreviewScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onReload = viewModel::runPlaygroundPreview,
                                    onConsoleMessage = viewModel::appendConsoleLog
                                )
                            }

                            // 15. Practice Game Selection (Tab 3: Practice)
                            composable(CodeverseRoutes.PRACTICE) {
                                PracticeGameSelectionScreen(
                                    uiState = uiState,
                                    onLaunchBossBattle = { wid -> navController.navigate(CodeverseRoutes.bossBattle(wid)) },
                                    onPracticeGameWon = viewModel::recordPracticeXp
                                )
                            }

                            // 16. Boss Battle
                            composable(
                                route = CodeverseRoutes.BOSS_BATTLE,
                                arguments = listOf(navArgument("worldId") { type = NavType.StringType })
                            ) { entry ->
                                val worldId = entry.arguments?.getString("worldId") ?: "world_1"
                                BossBattleScreen(
                                    worldId = worldId,
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onSpeakByte = speakIfEnabled,
                                    onVictory = viewModel::recordBossVictory
                                )
                            }

                            // 17. AI Tutor Chat (Tab 4: AI Tutor)
                            composable(CodeverseRoutes.AI_TUTOR) {
                                AiTutorChatScreen(
                                    uiState = uiState,
                                    onSendPrompt = viewModel::sendTutorPrompt,
                                    onSpeakText = speakIfEnabled,
                                    onClearChat = viewModel::clearTutorChat,
                                    onDismissError = viewModel::clearTutorError
                                )
                            }

                            // 18. User Profile (Tab 5: Profile)
                            composable(CodeverseRoutes.PROFILE) {
                                UserProfileScreen(
                                    uiState = uiState,
                                    onUpdateDisplayName = viewModel::updateDisplayName,
                                    onOpenLeaderboard = { navController.navigate(CodeverseRoutes.LEADERBOARD) },
                                    onOpenAchievements = { navController.navigate(CodeverseRoutes.ACHIEVEMENTS) },
                                    onOpenMascotStudio = { navController.navigate(CodeverseRoutes.MASCOT_STUDIO) },
                                    onOpenSettings = { navController.navigate(CodeverseRoutes.SETTINGS) },
                                    onOpenPremium = { navController.navigate(CodeverseRoutes.PREMIUM) },
                                    onOpenGallery = { navController.navigate(CodeverseRoutes.PROJECT_GALLERY) }
                                )
                            }

                            // 19. Leaderboard
                            composable(CodeverseRoutes.LEADERBOARD) {
                                LeaderboardScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            // 20. Achievements
                            composable(CodeverseRoutes.ACHIEVEMENTS) {
                                AchievementsScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            // 21. Mascot Customization Studio
                            composable(CodeverseRoutes.MASCOT_STUDIO) {
                                MascotCustomizationStudioScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onSelectTheme = viewModel::equipOrBuyTheme,
                                    onSelectAccessory = viewModel::equipOrBuyAccessory
                                )
                            }

                            // 22. Settings
                            composable(CodeverseRoutes.SETTINGS) {
                                SettingsScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onSelectLanguage = viewModel::selectLanguage,
                                    onToggleDarkTheme = viewModel::toggleDarkTheme,
                                    onToggleVoiceNarration = viewModel::toggleVoiceNarration,
                                    onToggleSoundEffects = viewModel::toggleSoundEffects,
                                    onToggleDailyReminder = viewModel::toggleDailyReminder,
                                    onSignOut = {
                                        voiceController.stop()
                                        signOutFromCodeverse(
                                            credentialManager = credentialManager,
                                            scope = scope
                                        )
                                    }
                                )
                            }

                            // 23. Premium Subscription
                            composable(CodeverseRoutes.PREMIUM) {
                                PremiumSubscriptionScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onToggleProPreview = viewModel::activateProPlan
                                )
                            }

                            // 24. Project Gallery
                            composable(CodeverseRoutes.PROJECT_GALLERY) {
                                ProjectGalleryScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onCreateNewProject = {
                                        viewModel.createNewProject("Codeverse Build #${uiState.savedProjects.size + 1}")
                                        navigatePrimaryTab(PrimaryTab.CODE)
                                    },
                                    onOpenProject = { proj ->
                                        viewModel.loadProjectIntoPlayground(proj)
                                        navigatePrimaryTab(PrimaryTab.CODE)
                                    },
                                    onDeleteProject = viewModel::deleteProject
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
