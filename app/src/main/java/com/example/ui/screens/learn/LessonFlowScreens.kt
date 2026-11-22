package com.example.ui.screens.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.seed.CurriculumSeed
import com.example.domain.model.ByteExpression
import com.example.ui.components.AnimatedNeonProgressBar
import com.example.ui.components.ByteMascot
import com.example.ui.components.ByteSpeechCard
import com.example.ui.components.CodeverseCard
import com.example.ui.components.CodeverseGradientButton
import com.example.ui.components.CodeverseSecondaryButton
import com.example.ui.components.CodeverseTopBar
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
import com.example.ui.theme.SoftGreenContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmYellow
import com.example.ui.viewmodel.CodeverseUiState

// 10. INTERACTIVE LESSON SCREEN
@Composable
fun InteractiveLessonScreen(
    lessonId: String,
    uiState: CodeverseUiState,
    onBack: () -> Unit,
    onSpeakByte: (String) -> Unit,
    onStartQuiz: (String) -> Unit
) {
    val lesson = remember(lessonId) {
        CurriculumSeed.findLesson(lessonId) ?: CurriculumSeed.allLessons().first()
    }
    var stepIndex by remember(lesson.id) { mutableIntStateOf(0) }
    var selectedChallengeOption by remember(lesson.id, stepIndex) { mutableStateOf<Int?>(null) }

    val step = lesson.steps[stepIndex.coerceIn(0, lesson.steps.lastIndex)]
    val progress = (stepIndex + 1).toFloat() / lesson.steps.size.toFloat()

    val challenge = step.interactiveChallenge
    val isChallengeAnswered = selectedChallengeOption != null
    val isChallengeCorrect = challenge != null && selectedChallengeOption == challenge.correctIndex

    val byteExpr = when {
        challenge == null -> ByteExpression.FOCUSED
        !isChallengeAnswered -> ByteExpression.THINKING
        isChallengeCorrect -> ByteExpression.CELEBRATING
        else -> ByteExpression.ENCOURAGING
    }
    val byteMessage = when {
        challenge != null && isChallengeAnswered && isChallengeCorrect -> challenge.feedbackCorrect
        challenge != null && isChallengeAnswered && !isChallengeCorrect -> challenge.feedbackWrong
        else -> step.byteTip
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
                .widthIn(max = 680.dp)
                .fillMaxSize()
        ) {
            CodeverseTopBar(
                title = lesson.title,
                subtitle = "Step ${stepIndex + 1} of ${lesson.steps.size}",
                onBack = onBack
            )

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                AnimatedNeonProgressBar(progress = progress, heightDp = 8)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CodeverseCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = step.explanation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }

                SyntaxCodeBlock(
                    code = step.codeSnippet,
                    languageLabel = step.codeLanguage
                )

                if (challenge != null) {
                    CodeverseCard(
                        borderColor = CyberPurple.copy(alpha = 0.65f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "QUICK CHECK",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyberCyan
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = challenge.prompt,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(12.dp))
                        challenge.options.forEachIndexed { idx, opt ->
                            val picked = selectedChallengeOption == idx
                            val border = when {
                                picked && idx == challenge.correctIndex -> SoftGreen
                                picked && idx != challenge.correctIndex -> CoralError
                                else -> OutlineNavy
                            }
                            val bg = when {
                                picked && idx == challenge.correctIndex -> SoftGreenContainer
                                picked && idx != challenge.correctIndex -> CoralErrorContainer
                                else -> DeepNavy
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bg)
                                    .border(1.dp, border, RoundedCornerShape(12.dp))
                                    .clickable { selectedChallengeOption = idx }
                                    .testTag("lesson_challenge_opt_$idx")
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(opt, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                if (picked) {
                                    Icon(
                                        imageVector = if (idx == challenge.correctIndex) Icons.Filled.CheckCircle else Icons.Filled.ErrorOutline,
                                        contentDescription = null,
                                        tint = if (idx == challenge.correctIndex) SoftGreen else CoralError
                                    )
                                }
                            }
                        }
                    }
                }

                ByteSpeechCard(
                    message = byteMessage,
                    expression = byteExpr,
                    colorTheme = uiState.byteColorTheme,
                    accessory = uiState.byteAccessory,
                    onSpeakClick = { onSpeakByte(byteMessage) }
                )
            }

            Box(modifier = Modifier.padding(16.dp)) {
                val isLastStep = stepIndex >= lesson.steps.lastIndex
                CodeverseGradientButton(
                    text = if (isLastStep) "Start Quiz & Earn +${lesson.xpReward} XP" else "Next Concept",
                    icon = if (isLastStep) Icons.Filled.Quiz else Icons.AutoMirrored.Filled.ArrowForward,
                    onClick = {
                        if (!isLastStep) {
                            stepIndex += 1
                        } else {
                            onStartQuiz(lesson.id)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "lesson_next_button"
                )
            }
        }
    }
}

// 11. EXERCISE AND QUIZ SCREEN
@Composable
fun ExerciseQuizScreen(
    lessonId: String,
    uiState: CodeverseUiState,
    onBack: () -> Unit,
    onSpeakByte: (String) -> Unit,
    onCompleteQuiz: (correctCount: Int, totalCount: Int) -> Unit
) {
    val lesson = remember(lessonId) {
        CurriculumSeed.findLesson(lessonId) ?: CurriculumSeed.allLessons().first()
    }
    val questions = lesson.quizQuestions
    var currentIndex by remember(lesson.id) { mutableIntStateOf(0) }
    var selectedIndex by remember(lesson.id, currentIndex) { mutableStateOf<Int?>(null) }
    var checked by remember(lesson.id, currentIndex) { mutableStateOf(false) }
    var correctCount by remember(lesson.id) { mutableIntStateOf(0) }

    val question = questions[currentIndex.coerceIn(0, questions.lastIndex)]
    val isCorrect = checked && selectedIndex == question.correctIndex

    val byteExpression = when {
        !checked -> ByteExpression.THINKING
        isCorrect -> ByteExpression.CELEBRATING
        else -> ByteExpression.ENCOURAGING
    }
    val byteFeedback = when {
        !checked -> "Pick the best answer below. Read the code carefully!"
        isCorrect -> "${uiState.strings.byteCorrectLine} ${question.explanation}"
        else -> "${uiState.strings.byteWrongLine} ${question.explanation}"
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
                .widthIn(max = 680.dp)
                .fillMaxSize()
        ) {
            CodeverseTopBar(
                title = "Quiz Challenge",
                subtitle = "Question ${currentIndex + 1} of ${questions.size}",
                onBack = onBack
            )

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                AnimatedNeonProgressBar(
                    progress = (currentIndex + 1).toFloat() / questions.size.toFloat(),
                    heightDp = 8
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = question.prompt,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                if (!question.codeContext.isNullOrBlank()) {
                    SyntaxCodeBlock(
                        code = question.codeContext,
                        languageLabel = "CODE"
                    )
                }

                question.options.forEachIndexed { idx, option ->
                    val isPicked = selectedIndex == idx
                    val border = when {
                        checked && idx == question.correctIndex -> SoftGreen
                        checked && isPicked && idx != question.correctIndex -> CoralError
                        isPicked -> CyberCyan
                        else -> OutlineNavy
                    }
                    val bg = when {
                        checked && idx == question.correctIndex -> SoftGreenContainer
                        checked && isPicked && idx != question.correctIndex -> CoralErrorContainer
                        isPicked -> Color(0xFF282D59)
                        else -> CardNavy
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(bg)
                            .border(1.5.dp, border, RoundedCornerShape(16.dp))
                            .clickable(enabled = !checked) { selectedIndex = idx }
                            .testTag("quiz_option_$idx")
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (checked && idx == question.correctIndex) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Correct", tint = SoftGreen)
                        } else if (checked && isPicked) {
                            Icon(Icons.Filled.ErrorOutline, contentDescription = "Incorrect", tint = CoralError)
                        }
                    }
                }

                ByteSpeechCard(
                    message = byteFeedback,
                    expression = byteExpression,
                    colorTheme = uiState.byteColorTheme,
                    accessory = uiState.byteAccessory,
                    onSpeakClick = { onSpeakByte(byteFeedback) }
                )
            }

            Box(modifier = Modifier.padding(16.dp)) {
                if (!checked) {
                    CodeverseGradientButton(
                        text = "Check Answer",
                        enabled = selectedIndex != null,
                        onClick = {
                            checked = true
                            if (selectedIndex == question.correctIndex) {
                                correctCount += 1
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "quiz_check_button"
                    )
                } else {
                    val isLast = currentIndex >= questions.lastIndex
                    CodeverseGradientButton(
                        text = if (isLast) "Claim Rewards" else "Next Question",
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = {
                            if (isLast) {
                                onCompleteQuiz(correctCount, questions.size)
                            } else {
                                currentIndex += 1
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "quiz_continue_button"
                    )
                }
            }
        }
    }
}

// 12. LESSON RESULTS SCREEN
@Composable
fun LessonResultsScreen(
    uiState: CodeverseUiState,
    onSpeakByte: (String) -> Unit,
    onContinueRoadmap: () -> Unit,
    onOpenPlayground: () -> Unit
) {
    val result = uiState.lastLessonResult

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
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(SoftGreen.copy(alpha = 0.28f), DeepNavy))
                    )
                    .border(2.dp, SoftGreen.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                ByteMascot(
                    expression = ByteExpression.CELEBRATING,
                    colorTheme = uiState.byteColorTheme,
                    accessory = uiState.byteAccessory,
                    size = 156.dp
                )
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = "LESSON COMPLETE!",
                style = MaterialTheme.typography.labelLarge,
                color = SoftGreen
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = result?.lessonTitle ?: "Coding Mastery",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = uiState.strings.byteCompleteLine,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResultMetricCard(
                    label = "XP Earned",
                    value = "+${result?.xpEarned ?: 60}",
                    accent = NeonLavender,
                    icon = Icons.Filled.Bolt,
                    modifier = Modifier.weight(1f)
                )
                ResultMetricCard(
                    label = "Gems",
                    value = "+${result?.gemsEarned ?: 20}",
                    accent = CyberCyan,
                    icon = Icons.Filled.Diamond,
                    modifier = Modifier.weight(1f)
                )
                ResultMetricCard(
                    label = "Accuracy",
                    value = "${result?.accuracyPercent ?: 100}%",
                    accent = SoftGreen,
                    icon = Icons.Filled.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(26.dp))

            CodeverseGradientButton(
                text = "Continue Roadmap",
                icon = Icons.Filled.RocketLaunch,
                onClick = onContinueRoadmap,
                modifier = Modifier.fillMaxWidth(),
                testTag = "results_continue_button"
            )
            Spacer(Modifier.height(10.dp))
            CodeverseSecondaryButton(
                text = "Try This in Coding Playground",
                icon = Icons.Filled.Code,
                onClick = {
                    onSpeakByte("Let's build something live in the playground!")
                    onOpenPlayground()
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "results_playground_button"
            )
        }
    }
}

@Composable
private fun ResultMetricCard(
    label: String,
    value: String,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    CodeverseCard(
        borderColor = accent.copy(alpha = 0.5f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}
