package com.example.ui.screens.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.CodeverseI18n
import com.example.data.remote.launchInteractiveGoogleSignIn
import com.example.domain.model.ByteAccessory
import com.example.domain.model.ByteColorTheme
import com.example.domain.model.ByteExpression
import com.example.ui.components.ByteMascot
import com.example.ui.components.CodeverseCard
import com.example.ui.components.CodeverseGradientButton
import com.example.ui.components.CodeverseSecondaryButton
import com.example.ui.components.SyntaxCodeBlock
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CoralError
import com.example.ui.theme.CoralErrorContainer
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepSpaceGradient
import com.example.ui.theme.NeonLavender
import com.example.ui.theme.OutlineNavy
import com.example.ui.theme.SoftGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 1. SPLASH SCREEN
@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val scale = remember { Animatable(0.84f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(650)) }
        launch { scale.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) }
        delay(1450)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .padding(28.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.codeverse_logo),
                contentDescription = "CODEVERSE logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(164.dp)
                    .clip(RoundedCornerShape(34.dp))
                    .border(2.dp, CyberCyan.copy(alpha = 0.55f), RoundedCornerShape(34.dp))
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "CODEVERSE",
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Learn. Code. Play. Create.",
                style = MaterialTheme.typography.titleMedium,
                color = CyberCyan
            )
            Spacer(Modifier.height(32.dp))
            ByteMascot(
                expression = ByteExpression.EXCITED,
                size = 86.dp
            )
        }
    }
}

// 2. ONBOARDING SCREEN
private data class OnboardingSlide(
    val title: String,
    val description: String,
    val badge: String,
    val expression: ByteExpression
)

@Composable
fun OnboardingScreen(
    onContinue: () -> Unit,
    onSkipToAuth: () -> Unit
) {
    val slides = remember {
        listOf(
            OnboardingSlide(
                title = "Master Full-Stack in 7 Worlds",
                description = "Go from HTML & CSS foundations all the way to JavaScript, React, Node.js, Databases, Git, and Capstone architecture in bite-sized lessons.",
                badge = "7-WORLD ROADMAP",
                expression = ByteExpression.EXCITED
            ),
            OnboardingSlide(
                title = "Code & Preview Live on Mobile",
                description = "Write HTML, CSS, and JavaScript in a sandboxed multi-file playground with instant preview, console feedback, and cloud project gallery.",
                badge = "LIVE PLAYGROUND",
                expression = ByteExpression.FOCUSED
            ),
            OnboardingSlide(
                title = "Level Up with BYTE & Boss Battles",
                description = "Earn XP, streaks, gems, and badges. Defeat coding bosses and pair-program with BYTE AI Tutor in English, Hinglish, or Hindi.",
                badge = "GAMIFIED AI TUTOR",
                expression = ByteExpression.CELEBRATING
            )
        )
    }
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.codeverse_logo),
                        contentDescription = "CODEVERSE emblem",
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "CODEVERSE",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonLavender,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onSkipToAuth)
                        .testTag("onboarding_skip_button")
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(196.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(CyberPurple.copy(alpha = 0.34f), DeepNavy)
                                )
                            )
                            .border(1.5.dp, CyberCyan.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        ByteMascot(
                            expression = slide.expression,
                            size = 152.dp
                        )
                    }
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = slide.badge,
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberCyan,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(CyberCyan.copy(alpha = 0.14f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = slide.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 18.dp)
            ) {
                repeat(slides.size) { index ->
                    val active = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (active) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (active) CyberCyan else OutlineNavy)
                    )
                }
            }

            CodeverseGradientButton(
                text = if (pagerState.currentPage < slides.lastIndex) "Next" else "Choose Your Language",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = {
                    if (pagerState.currentPage < slides.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onContinue()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "onboarding_next_button"
            )
        }
    }
}

// 3. LANGUAGE SELECTION SCREEN
@Composable
fun LanguageSelectionScreen(
    selectedLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onContinue: () -> Unit
) {
    val previewStrings = CodeverseI18n.stringsFor(selectedLanguage)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Language,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Select Learning Language",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Learn explanations in English, Hinglish, or Hindi. Programming syntax always stays in authentic English.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            AppLanguage.entries.forEach { lang ->
                val isSelected = lang == selectedLanguage
                CodeverseCard(
                    borderColor = if (isSelected) CyberCyan else OutlineNavy,
                    backgroundColor = if (isSelected) Color(0xFF282B54) else CardNavy,
                    onClick = { onSelectLanguage(lang) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lang_option_${lang.code}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lang.nativeName,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = lang.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Selected",
                                tint = SoftGreen
                            )
                        }
                    }
                }
            }

            CodeverseCard(borderColor = CyberPurple.copy(alpha = 0.6f)) {
                Text(
                    text = "Live Preview (${selectedLanguage.nativeName})",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonLavender
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = previewStrings.byteWelcomeLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(10.dp))
                SyntaxCodeBlock(
                    code = "const coder = \"Ready\";\nconsole.log(coder);",
                    languageLabel = "JS (Always English)"
                )
            }

            Spacer(Modifier.weight(1f))

            CodeverseGradientButton(
                text = "Continue",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                testTag = "language_continue_button"
            )
        }
    }
}

// 4. LOGIN AND REGISTRATION SCREEN (Real Google Sign-In via Credential Manager)
@Composable
fun LoginRegistrationScreen(
    selectedLanguage: AppLanguage,
    onAuthenticated: () -> Unit,
    onChangeLanguage: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val strings = remember(selectedLanguage) { CodeverseI18n.stringsFor(selectedLanguage) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 540.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.codeverse_logo),
                contentDescription = "CODEVERSE logo",
                modifier = Modifier
                    .size(116.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .border(1.5.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(26.dp))
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "Welcome to CODEVERSE",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = strings.tagline,
                style = MaterialTheme.typography.titleSmall,
                color = CyberCyan
            )
            Spacer(Modifier.height(20.dp))

            CodeverseCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ByteMascot(
                        expression = if (errorMessage != null) ByteExpression.ENCOURAGING else ByteExpression.HAPPY,
                        size = 68.dp
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "One Account. All 7 Worlds.",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Sign in or create your learner profile securely with Google to sync XP, streaks, playground projects, and leaderboard rankings.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AuthFeatureChip(Icons.Filled.Code, "Cloud Projects")
                    AuthFeatureChip(Icons.Filled.SportsEsports, "XP & Leagues")
                    AuthFeatureChip(Icons.Filled.Security, "Zero-Trust Sync")
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(14.dp))
                CodeverseCard(
                    borderColor = CoralError,
                    backgroundColor = CoralErrorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardNavy)
                        .border(1.dp, CyberCyan, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = CyberCyan,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Authenticating with Google...",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                    }
                }
            } else {
                CodeverseGradientButton(
                    text = strings.signInGoogle,
                    icon = Icons.Filled.Login,
                    onClick = {
                        errorMessage = null
                        isLoading = true
                        launchInteractiveGoogleSignIn(
                            context = context,
                            credentialManager = credentialManager,
                            scope = scope,
                            onSuccess = {
                                isLoading = false
                                onAuthenticated()
                            },
                            onCancelled = {
                                isLoading = false
                            },
                            onError = { msg ->
                                isLoading = false
                                errorMessage = msg
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "google_sign_in_button"
                )
            }

            Spacer(Modifier.height(12.dp))

            CodeverseSecondaryButton(
                text = "Language: ${selectedLanguage.nativeName}",
                icon = Icons.Filled.Language,
                onClick = onChangeLanguage,
                modifier = Modifier.fillMaxWidth(),
                testTag = "auth_language_button"
            )
        }
    }
}

@Composable
private fun AuthFeatureChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

// 5. LEARNING GOAL SETUP SCREEN
@Composable
fun LearningGoalSetupScreen(
    initialMinutes: Int,
    initialTrack: String,
    onSaveGoal: (Int, String) -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(initialMinutes) }
    var selectedTrack by remember { mutableStateOf(initialTrack) }

    val goals = listOf(
        10 to "Casual • 10 min/day",
        15 to "Regular • 15 min/day (Recommended)",
        30 to "Serious • 30 min/day",
        45 to "Pro Sprint • 45 min/day"
    )
    val tracks = listOf(
        "Full-Stack Web" to "HTML, CSS, JS, React, Node.js, SQL & Capstone",
        "Frontend Specialist" to "Web Foundations, Modern JS & React UI Systems",
        "Backend & Databases" to "Node.js, Express, REST APIs, SQL & MongoDB",
        "Zero-to-One Beginner" to "Gentle guided roadmap starting from Internet basics"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Set Your Daily Coding Goal",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Consistency beats intensity. Pick a realistic target to build an unbroken streak.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            goals.forEach { (minutes, label) ->
                val active = selectedMinutes == minutes
                CodeverseCard(
                    borderColor = if (active) CyberCyan else OutlineNavy,
                    backgroundColor = if (active) Color(0xFF282B54) else CardNavy,
                    onClick = { selectedMinutes = minutes },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_minutes_$minutes")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.ElectricBolt,
                                contentDescription = null,
                                tint = if (active) WarmYellow else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary
                            )
                        }
                        if (active) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Selected", tint = SoftGreen)
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = "Primary Learning Track",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            tracks.forEach { (title, subtitle) ->
                val active = selectedTrack == title
                CodeverseCard(
                    borderColor = if (active) CyberPurple else OutlineNavy,
                    backgroundColor = if (active) Color(0xFF2B2558) else CardNavy,
                    onClick = { selectedTrack = title },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("track_$title")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                            Spacer(Modifier.height(2.dp))
                            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        if (active) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Selected", tint = NeonLavender)
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            CodeverseGradientButton(
                text = "Meet BYTE Mascot",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = { onSaveGoal(selectedMinutes, selectedTrack) },
                modifier = Modifier.fillMaxWidth(),
                testTag = "goal_continue_button"
            )
        }
    }
}

// 6. MEET BYTE MASCOT SCREEN
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MeetByteMascotScreen(
    welcomeLine: String,
    colorTheme: ByteColorTheme,
    accessory: ByteAccessory,
    onSpeak: (String) -> Unit,
    onEnterApp: () -> Unit
) {
    var activeExpression by remember { mutableStateOf(ByteExpression.EXCITED) }

    val expressionQuotes = remember(welcomeLine) {
        mapOf(
            ByteExpression.HAPPY to welcomeLine,
            ByteExpression.EXCITED to "Woohoo! Let's ship your very first full-stack project together!",
            ByteExpression.THINKING to "Hmm... let's trace that JavaScript async promise step by step.",
            ByteExpression.CONFUSED to "Wait a second—missing semicolon or mismatched bracket? Let's debug!",
            ByteExpression.ENCOURAGING to "Every senior developer started at line 1. You've totally got this!",
            ByteExpression.CELEBRATING to "Level up unlocked! +100 XP and spotless test pass!",
            ByteExpression.FOCUSED to "Deep focus mode engaged. Analyzing clean architecture patterns."
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MEET YOUR AI COPILOT",
                style = MaterialTheme.typography.labelMedium,
                color = CyberCyan
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Say Hello to BYTE",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(CyberPurple.copy(alpha = 0.36f), DeepNavy))
                    )
                    .border(2.dp, CyberCyan.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                ByteMascot(
                    expression = activeExpression,
                    colorTheme = colorTheme,
                    accessory = accessory,
                    size = 170.dp
                )
            }

            Spacer(Modifier.height(18.dp))

            val quote = expressionQuotes[activeExpression] ?: welcomeLine
            CodeverseCard(
                borderColor = CyberCyan.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BYTE • ${activeExpression.label}",
                        style = MaterialTheme.typography.labelLarge,
                        color = CyberCyan
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberPurple.copy(alpha = 0.25f))
                            .clickable { onSpeak(quote) }
                            .testTag("meet_byte_speak_button")
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Play BYTE voice",
                            tint = NeonLavender,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Speak", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = quote,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = "Tap an expression to test BYTE's reactions:",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ByteExpression.entries.forEach { expr ->
                    val selected = expr == activeExpression
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) CyberPurple else CardNavy)
                            .border(
                                1.dp,
                                if (selected) CyberCyan else OutlineNavy,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { activeExpression = expr }
                            .testTag("expr_${expr.name.lowercase()}")
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = expr.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(26.dp))

            CodeverseGradientButton(
                text = "Launch Codeverse Dashboard",
                icon = Icons.Filled.RocketLaunch,
                onClick = onEnterApp,
                modifier = Modifier.fillMaxWidth(),
                testTag = "enter_codeverse_button"
            )
        }
    }
}
