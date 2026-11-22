package com.example.ui.screens.learn

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.seed.CurriculumSeed
import com.example.domain.model.ByteExpression
import com.example.domain.model.LearningWorld
import com.example.ui.components.AnimatedNeonProgressBar
import com.example.ui.components.ByteSpeechCard
import com.example.ui.components.CodeverseCard
import com.example.ui.components.CodeverseGradientButton
import com.example.ui.components.CodeverseTopBar
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatBadgePill
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepSpaceGradient
import com.example.ui.theme.HeroCardGradient
import com.example.ui.theme.NeonLavender
import com.example.ui.theme.OutlineNavy
import com.example.ui.theme.SoftGreen
import com.example.ui.theme.SoftGreenContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmYellow
import com.example.ui.viewmodel.CodeverseUiState

// 7. HOME DASHBOARD SCREEN
@Composable
fun HomeDashboardScreen(
    uiState: CodeverseUiState,
    onSpeakByte: (String) -> Unit,
    onOpenRoadmap: () -> Unit,
    onOpenWorld: (String) -> Unit,
    onStartLesson: (String) -> Unit,
    onOpenPlayground: () -> Unit,
    onOpenBossBattle: (String) -> Unit,
    onOpenAiTutor: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenMascotStudio: () -> Unit
) {
    val allLessons = remember { CurriculumSeed.allLessons() }
    val nextLesson = remember(uiState.completedLessonIds) {
        allLessons.firstOrNull { it.id !in uiState.completedLessonIds } ?: allLessons.first()
    }
    val nextWorld = remember(nextLesson) {
        CurriculumSeed.findWorld(nextLesson.worldId) ?: CurriculumSeed.worlds.first()
    }
    val overallProgress = if (allLessons.isNotEmpty()) {
        uiState.completedLessonIds.size.toFloat() / allLessons.size.toFloat()
    } else 0f

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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Greeting & Live Gamification Stats
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CODEVERSE • LVL ${uiState.level}",
                                style = MaterialTheme.typography.labelMedium,
                                color = CyberCyan
                            )
                            Text(
                                text = "Hey, ${uiState.displayName.substringBefore(" ")}!",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatBadgePill(
                                icon = Icons.Filled.LocalFireDepartment,
                                value = "${uiState.streakDays}",
                                tint = WarmYellow
                            )
                            StatBadgePill(
                                icon = Icons.Filled.Diamond,
                                value = "${uiState.gems}",
                                tint = CyberCyan,
                                onClick = onOpenMascotStudio
                            )
                            StatBadgePill(
                                icon = Icons.Filled.Bolt,
                                value = "${uiState.xp}",
                                tint = NeonLavender
                            )
                        }
                    }

                    ByteSpeechCard(
                        message = uiState.strings.byteWelcomeLine,
                        expression = ByteExpression.HAPPY,
                        colorTheme = uiState.byteColorTheme,
                        accessory = uiState.byteAccessory,
                        onSpeakClick = { onSpeakByte(uiState.strings.byteWelcomeLine) },
                        onMascotClick = onOpenMascotStudio
                    )
                }
            }

            // Hero Continue Learning Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(HeroCardGradient)
                        .border(1.5.dp, CyberPurple.copy(alpha = 0.65f), RoundedCornerShape(24.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WORLD ${nextWorld.number} • ${nextWorld.badgeTag}",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyberCyan
                        )
                        Text(
                            text = "${(overallProgress * 100).toInt()}% Complete",
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonLavender
                        )
                    }
                    Text(
                        text = nextLesson.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = nextLesson.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    AnimatedNeonProgressBar(progress = overallProgress, heightDp = 9)
                    Spacer(Modifier.height(4.dp))
                    CodeverseGradientButton(
                        text = "${uiState.strings.continueLearning} (+${nextLesson.xpReward} XP)",
                        icon = Icons.Filled.PlayArrow,
                        onClick = { onStartLesson(nextLesson.id) },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "home_continue_lesson_button"
                    )
                }
            }

            // Quick Actions Grid (2x2)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeader(title = "Command Center")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickCommandTile(
                            title = "Playground",
                            subtitle = "HTML/CSS/JS",
                            icon = Icons.Filled.Code,
                            accent = CyberCyan,
                            onClick = onOpenPlayground,
                            modifier = Modifier.weight(1f).testTag("home_quick_playground")
                        )
                        QuickCommandTile(
                            title = uiState.strings.bossBattle,
                            subtitle = "Win +150 XP",
                            icon = Icons.Filled.Shield,
                            accent = WarmYellow,
                            onClick = { onOpenBossBattle(nextWorld.id) },
                            modifier = Modifier.weight(1f).testTag("home_quick_boss")
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickCommandTile(
                            title = "BYTE AI Tutor",
                            subtitle = "Debug & Explain",
                            icon = Icons.Filled.AutoAwesome,
                            accent = NeonLavender,
                            onClick = onOpenAiTutor,
                            modifier = Modifier.weight(1f).testTag("home_quick_tutor")
                        )
                        QuickCommandTile(
                            title = "Gallery",
                            subtitle = "${uiState.savedProjects.size} Projects",
                            icon = Icons.Filled.FolderOpen,
                            accent = SoftGreen,
                            onClick = onOpenGallery,
                            modifier = Modifier.weight(1f).testTag("home_quick_gallery")
                        )
                    }
                }
            }

            // Daily Missions
            item {
                SectionHeader(title = uiState.strings.dailyMissions)
            }
            items(CurriculumSeed.dailyMissions, key = { it.id }) { mission ->
                val done = mission.id in uiState.completedMissionIds
                CodeverseCard(
                    borderColor = if (done) SoftGreen.copy(alpha = 0.6f) else OutlineNavy,
                    backgroundColor = if (done) SoftGreenContainer.copy(alpha = 0.45f) else CardNavy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mission.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary
                            )
                            Text(
                                text = mission.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        if (done) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = "Completed",
                                tint = SoftGreen
                            )
                        } else {
                            Text(
                                text = "+${mission.xpReward} XP",
                                style = MaterialTheme.typography.labelMedium,
                                color = WarmYellow
                            )
                        }
                    }
                }
            }

            // 7-World Roadmap Preview
            item {
                SectionHeader(
                    title = uiState.strings.worldsRoadmap,
                    actionLabel = "View All 7",
                    onActionClick = onOpenRoadmap
                )
            }
            items(CurriculumSeed.worlds.take(3), key = { it.id }) { world ->
                WorldSummaryCard(
                    world = world,
                    completedLessons = uiState.completedLessonIds,
                    bossDefeated = world.id in uiState.defeatedBossWorldIds,
                    onClick = { onOpenWorld(world.id) }
                )
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun QuickCommandTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CodeverseCard(
        borderColor = accent.copy(alpha = 0.45f),
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

// 8. LEARNING ROADMAP SCREEN (Full 7 Worlds)
@Composable
fun LearningRoadmapScreen(
    uiState: CodeverseUiState,
    onBack: () -> Unit,
    onOpenWorld: (String) -> Unit,
    onOpenBossBattle: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
        ) {
            CodeverseTopBar(
                title = "Full-Stack Roadmap",
                subtitle = "7 Worlds • From HTML to Production Capstone",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(CurriculumSeed.worlds, key = { it.id }) { world ->
                    WorldSummaryCard(
                        world = world,
                        completedLessons = uiState.completedLessonIds,
                        bossDefeated = world.id in uiState.defeatedBossWorldIds,
                        onClick = { onOpenWorld(world.id) },
                        onBossClick = { onOpenBossBattle(world.id) }
                    )
                }
                item { Spacer(Modifier.height(18.dp)) }
            }
        }
    }
}

@Composable
private fun WorldSummaryCard(
    world: LearningWorld,
    completedLessons: Set<String>,
    bossDefeated: Boolean,
    onClick: () -> Unit,
    onBossClick: (() -> Unit)? = null
) {
    val worldLessons = world.modules.flatMap { it.lessons }
    val doneCount = worldLessons.count { it.id in completedLessons }
    val progress = if (worldLessons.isNotEmpty()) doneCount.toFloat() / worldLessons.size else 0f
    val accent = Color(world.accentHex)

    CodeverseCard(
        borderColor = accent.copy(alpha = 0.55f),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("world_card_${world.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.18f))
                        .border(1.5.dp, accent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${world.number}",
                        style = MaterialTheme.typography.titleMedium,
                        color = accent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "WORLD ${world.number} • ${world.badgeTag}",
                        style = MaterialTheme.typography.labelSmall,
                        color = accent
                    )
                    Text(
                        text = world.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open ${world.title}",
                tint = TextSecondary
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = world.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Spacer(Modifier.height(10.dp))
        AnimatedNeonProgressBar(
            progress = progress,
            brush = Brush.horizontalGradient(listOf(accent, CyberPurple))
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$doneCount / ${worldLessons.size} Lessons Completed",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            if (onBossClick != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (bossDefeated) SoftGreenContainer else WarmYellow.copy(alpha = 0.16f))
                        .clickable(onClick = onBossClick)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (bossDefeated) Icons.Filled.CheckCircle else Icons.Filled.Shield,
                        contentDescription = null,
                        tint = if (bossDefeated) SoftGreen else WarmYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (bossDefeated) "Boss Cleared" else "Boss: ${world.bossName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (bossDefeated) SoftGreen else WarmYellow
                    )
                }
            }
        }
    }
}

// 9. COURSE DETAILS SCREEN
@Composable
fun CourseDetailsScreen(
    worldId: String,
    uiState: CodeverseUiState,
    onBack: () -> Unit,
    onStartLesson: (String) -> Unit,
    onStartBossBattle: (String) -> Unit
) {
    val world = remember(worldId) {
        CurriculumSeed.findWorld(worldId) ?: CurriculumSeed.worlds.first()
    }
    val accent = Color(world.accentHex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
        ) {
            CodeverseTopBar(
                title = "World ${world.number}: ${world.title}",
                subtitle = world.badgeTag,
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    CodeverseCard(
                        borderColor = accent.copy(alpha = 0.65f),
                        backgroundColor = Color(0xFF23264B)
                    ) {
                        Text(
                            text = world.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = world.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatBadgePill(
                                icon = Icons.Filled.Star,
                                value = "${world.modules.size} Modules",
                                tint = accent
                            )
                            StatBadgePill(
                                icon = Icons.Filled.Shield,
                                value = world.bossName,
                                tint = WarmYellow
                            )
                        }
                    }
                }

                items(world.modules, key = { it.id }) { module ->
                    CodeverseCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = module.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Timer,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${module.estimatedMinutes}m",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberCyan
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = module.summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(12.dp))

                        module.lessons.forEach { lesson ->
                            val isDone = lesson.id in uiState.completedLessonIds
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DeepNavy)
                                    .border(
                                        1.dp,
                                        if (isDone) SoftGreen.copy(alpha = 0.6f) else OutlineNavy,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { onStartLesson(lesson.id) }
                                    .testTag("lesson_row_${lesson.id}")
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lesson.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "+${lesson.xpReward} XP • +${lesson.gemReward} Gems",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonLavender
                                    )
                                }
                                Icon(
                                    imageVector = if (isDone) Icons.Filled.CheckCircle else Icons.Filled.PlayArrow,
                                    contentDescription = "Start lesson",
                                    tint = if (isDone) SoftGreen else CyberCyan
                                )
                            }
                        }
                    }
                }

                item {
                    CodeverseGradientButton(
                        text = "Challenge World Boss: ${world.bossName}",
                        icon = Icons.Filled.Shield,
                        onClick = { onStartBossBattle(world.id) },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "course_boss_battle_button"
                    )
                    Spacer(Modifier.height(18.dp))
                }
            }
        }
    }
}
