package com.example.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.seed.CurriculumSeed
import com.example.domain.model.ByteExpression
import com.example.domain.model.QuizQuestion
import com.example.ui.components.AnimatedNeonProgressBar
import com.example.ui.components.ByteMascot
import com.example.ui.components.ByteSpeechCard
import com.example.ui.components.CodeverseCard
import com.example.ui.components.CodeverseGradientButton
import com.example.ui.components.CodeverseSecondaryButton
import com.example.ui.components.CodeverseTopBar
import com.example.ui.components.SectionHeader
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

private data class MiniPracticeGame(
    val id: String,
    val title: String,
    val subtitle: String,
    val rewardText: String,
    val icon: ImageVector,
    val accent: Color,
    val question: QuizQuestion
)

// 15. PRACTICE GAME SELECTION SCREEN
@Composable
fun PracticeGameSelectionScreen(
    uiState: CodeverseUiState,
    onLaunchBossBattle: (String) -> Unit,
    onPracticeGameWon: (xp: Int, gems: Int) -> Unit
) {
    val miniGames = remember {
        listOf(
            MiniPracticeGame(
                id = "bug_hunter",
                title = "Bug Hunter",
                subtitle = "Spot and patch a sneaky JavaScript runtime bug fast.",
                rewardText = "+60 XP • +15 Gems",
                icon = Icons.Filled.BugReport,
                accent = CoralError,
                question = QuizQuestion(
                    id = "mg_bug",
                    prompt = "Find the bug in this function meant to sum an array:",
                    codeContext = "function sum(arr) {\n  const total = 0;\n  for (const n of arr) total += n;\n  return total;\n}",
                    options = listOf(
                        "total is declared with const instead of let",
                        "for..of cannot iterate arrays",
                        "return must be inside the loop",
                        "arr needs quotes around it"
                    ),
                    correctIndex = 0,
                    explanation = "Reassigning a const variable throws TypeError! Change const total = 0 to let total = 0."
                )
            ),
            MiniPracticeGame(
                id = "output_oracle",
                title = "Output Predictor",
                subtitle = "Trace JS & CSS execution in your head before running.",
                rewardText = "+70 XP • +20 Gems",
                icon = Icons.Filled.Psychology,
                accent = CyberCyan,
                question = QuizQuestion(
                    id = "mg_out",
                    prompt = "What does this JavaScript expression output?",
                    codeContext = "const list = [1, 2, 3];\nconst res = [...list, 4].length;\nconsole.log(res);",
                    options = listOf("4", "3", "[1, 2, 3, 4]", "undefined"),
                    correctIndex = 0,
                    explanation = "The spread operator copies [1, 2, 3] and appends 4, giving length 4."
                )
            ),
            MiniPracticeGame(
                id = "speed_syntax",
                title = "Speed Syntax Sprint",
                subtitle = "Lock in clean HTML, SQL, and React patterns.",
                rewardText = "+55 XP • +15 Gems",
                icon = Icons.Filled.Speed,
                accent = SoftGreen,
                question = QuizQuestion(
                    id = "mg_spd",
                    prompt = "Which HTTP method is idempotent and typically replaces an existing REST resource?",
                    codeContext = "fetch(\"/api/profile/42\", { method: \"___\" });",
                    options = listOf("PUT", "POST", "CONNECT", "PRINT"),
                    correctIndex = 0,
                    explanation = "PUT replaces the target resource idempotently."
                )
            )
        )
    }

    var activeGame by remember { mutableStateOf<MiniPracticeGame?>(null) }
    var selectedOpt by remember { mutableStateOf<Int?>(null) }
    var submitted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("PRACTICE ARENA", style = MaterialTheme.typography.labelMedium, color = CyberCyan)
                    Text(
                        text = "Play, Debug & Conquer",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sharpen coding reflexes with quick mini-games or challenge a World Boss.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            item { SectionHeader(title = "Interactive Coding Mini-Games") }

            items(miniGames, key = { it.id }) { game ->
                CodeverseCard(
                    borderColor = game.accent.copy(alpha = 0.55f),
                    onClick = {
                        activeGame = game
                        selectedOpt = null
                        submitted = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("practice_game_${game.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(game.accent.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(game.icon, contentDescription = null, tint = game.accent)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(game.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(game.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Spacer(Modifier.height(4.dp))
                            Text(game.rewardText, style = MaterialTheme.typography.labelSmall, color = WarmYellow)
                        }
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play ${game.title}", tint = CyberCyan)
                    }
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader(title = "7-World Boss Battles")
            }

            items(CurriculumSeed.worlds, key = { it.id }) { world ->
                val defeated = world.id in uiState.defeatedBossWorldIds
                CodeverseCard(
                    borderColor = if (defeated) SoftGreen.copy(alpha = 0.6f) else WarmYellow.copy(alpha = 0.5f),
                    onClick = { onLaunchBossBattle(world.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("practice_boss_${world.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WORLD ${world.number} BOSS",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (defeated) SoftGreen else WarmYellow
                            )
                            Text(
                                text = world.bossName,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = world.bossTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = if (defeated) Icons.Filled.CheckCircle else Icons.Filled.Shield,
                            contentDescription = "Challenge ${world.bossName}",
                            tint = if (defeated) SoftGreen else WarmYellow
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(14.dp)) }
        }
    }

    val currentGame = activeGame
    if (currentGame != null) {
        val q = currentGame.question
        val isCorrect = submitted && selectedOpt == q.correctIndex
        AlertDialog(
            onDismissRequest = { activeGame = null },
            containerColor = CardNavy,
            title = {
                Text(
                    text = currentGame.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(q.prompt, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    if (!q.codeContext.isNullOrBlank()) {
                        SyntaxCodeBlock(code = q.codeContext, languageLabel = "CHALLENGE")
                    }
                    q.options.forEachIndexed { idx, opt ->
                        val picked = selectedOpt == idx
                        val border = when {
                            submitted && idx == q.correctIndex -> SoftGreen
                            submitted && picked && idx != q.correctIndex -> CoralError
                            picked -> CyberCyan
                            else -> OutlineNavy
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DeepNavy)
                                .border(1.5.dp, border, RoundedCornerShape(12.dp))
                                .clickable(enabled = !submitted) { selectedOpt = idx }
                                .padding(12.dp)
                        ) {
                            Text(opt, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }
                    if (submitted) {
                        Text(
                            text = if (isCorrect) "Correct! ${q.explanation}" else "Not quite! ${q.explanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCorrect) SoftGreen else WarmYellow
                        )
                    }
                }
            },
            confirmButton = {
                if (!submitted) {
                    TextButton(
                        enabled = selectedOpt != null,
                        onClick = {
                            submitted = true
                            if (selectedOpt == q.correctIndex) {
                                onPracticeGameWon(60, 15)
                            }
                        }
                    ) {
                        Text("Submit", color = CyberCyan)
                    }
                } else {
                    TextButton(onClick = { activeGame = null }) {
                        Text("Done", color = SoftGreen)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { activeGame = null }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }
}

// 16. BOSS BATTLE SCREEN
@Composable
fun BossBattleScreen(
    worldId: String,
    uiState: CodeverseUiState,
    onBack: () -> Unit,
    onSpeakByte: (String) -> Unit,
    onVictory: (worldId: String, xp: Int, gems: Int) -> Unit
) {
    val world = remember(worldId) {
        CurriculumSeed.findWorld(worldId) ?: CurriculumSeed.worlds.first()
    }
    val questions = world.bossQuestions
    var qIndex by remember(world.id) { mutableIntStateOf(0) }
    var bossHp by remember(world.id) { mutableIntStateOf(questions.size) }
    var playerHearts by remember(world.id) { mutableIntStateOf(3) }
    var selectedOpt by remember(world.id, qIndex) { mutableStateOf<Int?>(null) }
    var answered by remember(world.id, qIndex) { mutableStateOf(false) }
    var battleFinished by remember(world.id) { mutableStateOf(false) }
    var playerWon by remember(world.id) { mutableStateOf(false) }

    val question = questions[qIndex.coerceIn(0, questions.lastIndex)]
    val isRight = answered && selectedOpt == question.correctIndex

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
                title = "Boss Arena: ${world.bossName}",
                subtitle = world.bossTitle,
                onBack = onBack
            )

            if (battleFinished) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ByteMascot(
                        expression = if (playerWon) ByteExpression.CELEBRATING else ByteExpression.ENCOURAGING,
                        colorTheme = uiState.byteColorTheme,
                        accessory = uiState.byteAccessory,
                        size = 160.dp
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = if (playerWon) "${world.bossName} DEFEATED!" else "Shields Depleted!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (playerWon) SoftGreen else CoralError,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (playerWon) {
                            "Incredible full-stack mastery! You claimed +150 XP and +40 Gems."
                        } else {
                            "BYTE recharged your shields. Review the explanation and jump right back in!"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    if (playerWon) {
                        CodeverseGradientButton(
                            text = "Return to Arena",
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "boss_victory_done_button"
                        )
                    } else {
                        CodeverseGradientButton(
                            text = "Retry Boss Battle",
                            onClick = {
                                qIndex = 0
                                bossHp = questions.size
                                playerHearts = 3
                                selectedOpt = null
                                answered = false
                                battleFinished = false
                                playerWon = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "boss_retry_button"
                        )
                        Spacer(Modifier.height(10.dp))
                        CodeverseSecondaryButton(
                            text = "Back to Practice",
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Boss HP & Player Hearts HUD
                    CodeverseCard(
                        borderColor = CoralError.copy(alpha = 0.6f),
                        backgroundColor = Color(0xFF2B1D3D)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Shield, contentDescription = null, tint = CoralError)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${world.bossName} HP",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(3) { idx ->
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = "Heart",
                                        tint = if (idx < playerHearts) CoralError else OutlineNavy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        AnimatedNeonProgressBar(
                            progress = bossHp.toFloat() / questions.size.toFloat(),
                            heightDp = 10,
                            brush = Brush.horizontalGradient(listOf(CoralError, WarmYellow))
                        )
                    }

                    Text(
                        text = question.prompt,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    if (!question.codeContext.isNullOrBlank()) {
                        SyntaxCodeBlock(code = question.codeContext, languageLabel = "BOSS ATTACK")
                    }

                    question.options.forEachIndexed { idx, opt ->
                        val picked = selectedOpt == idx
                        val border = when {
                            answered && idx == question.correctIndex -> SoftGreen
                            answered && picked && idx != question.correctIndex -> CoralError
                            picked -> CyberCyan
                            else -> OutlineNavy
                        }
                        val bg = when {
                            answered && idx == question.correctIndex -> SoftGreenContainer
                            answered && picked && idx != question.correctIndex -> CoralErrorContainer
                            else -> CardNavy
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(bg)
                                .border(1.5.dp, border, RoundedCornerShape(14.dp))
                                .clickable(enabled = !answered) { selectedOpt = idx }
                                .testTag("boss_option_$idx")
                                .padding(14.dp)
                        ) {
                            Text(opt, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        }
                    }

                    val tip = when {
                        !answered -> "Strike back by choosing the right fix!"
                        isRight -> "Direct hit! ${question.explanation}"
                        else -> "Boss hit your shield! ${question.explanation}"
                    }
                    ByteSpeechCard(
                        message = tip,
                        expression = when {
                            !answered -> ByteExpression.FOCUSED
                            isRight -> ByteExpression.EXCITED
                            else -> ByteExpression.ENCOURAGING
                        },
                        colorTheme = uiState.byteColorTheme,
                        accessory = uiState.byteAccessory,
                        onSpeakClick = { onSpeakByte(tip) }
                    )
                }

                Box(modifier = Modifier.padding(16.dp)) {
                    if (!answered) {
                        CodeverseGradientButton(
                            text = "Launch Counter-Attack",
                            icon = Icons.Filled.Bolt,
                            enabled = selectedOpt != null,
                            onClick = {
                                answered = true
                                if (selectedOpt == question.correctIndex) {
                                    bossHp = (bossHp - 1).coerceAtLeast(0)
                                } else {
                                    playerHearts = (playerHearts - 1).coerceAtLeast(0)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "boss_attack_button"
                        )
                    } else {
                        CodeverseGradientButton(
                            text = if (bossHp == 0 || playerHearts == 0 || qIndex >= questions.lastIndex) "See Battle Result" else "Next Round",
                            onClick = {
                                if (bossHp == 0) {
                                    playerWon = true
                                    battleFinished = true
                                    onVictory(world.id, 150, 40)
                                } else if (playerHearts == 0) {
                                    playerWon = false
                                    battleFinished = true
                                } else if (qIndex >= questions.lastIndex) {
                                    playerWon = bossHp < questions.size
                                    battleFinished = true
                                    if (playerWon) onVictory(world.id, 150, 40)
                                } else {
                                    qIndex += 1
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "boss_next_button"
                        )
                    }
                }
            }
        }
    }
}
