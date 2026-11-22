package com.example.ui.screens.profile

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import com.example.data.seed.CurriculumSeed
import com.example.domain.model.ByteAccessory
import com.example.domain.model.ByteColorTheme
import com.example.domain.model.ByteExpression
import com.example.ui.components.AnimatedNeonProgressBar
import com.example.ui.components.ByteMascot
import com.example.ui.components.CodeverseCard
import com.example.ui.components.CodeverseTopBar
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatBadgePill
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepSpaceGradient
import com.example.ui.theme.GoldGradient
import com.example.ui.theme.NeonLavender
import com.example.ui.theme.OutlineNavy
import com.example.ui.theme.SoftGreen
import com.example.ui.theme.SoftGreenContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmYellow
import com.example.ui.viewmodel.CodeverseUiState

// 18. USER PROFILE SCREEN
@Composable
fun UserProfileScreen(
    uiState: CodeverseUiState,
    onUpdateDisplayName: (String) -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenMascotStudio: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPremium: () -> Unit,
    onOpenGallery: () -> Unit
) {
    var showEditNameDialog by remember { mutableStateOf(false) }
    var draftName by remember(uiState.displayName) { mutableStateOf(uiState.displayName) }

    val xpIntoLevel = uiState.xp % 250
    val levelProgress = xpIntoLevel / 250f

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
            // Profile Hero Card
            item {
                CodeverseCard(
                    borderColor = CyberPurple.copy(alpha = 0.65f),
                    backgroundColor = Color(0xFF23254B)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .clip(CircleShape)
                                .background(DeepNavy)
                                .border(2.dp, CyberCyan, CircleShape)
                                .clickable(onClick = onOpenMascotStudio),
                            contentAlignment = Alignment.Center
                        ) {
                            ByteMascot(
                                expression = ByteExpression.HAPPY,
                                colorTheme = uiState.byteColorTheme,
                                accessory = uiState.byteAccessory,
                                size = 74.dp
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.displayName,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { showEditNameDialog = true },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit Name", tint = NeonLavender, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = uiState.email.ifBlank { uiState.learningTrack },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = if (uiState.proMember) "CODEVERSE PRO • LEVEL ${uiState.level}" else "LEVEL ${uiState.level} • ${uiState.learningTrack}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (uiState.proMember) WarmYellow else CyberCyan
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("XP to Level ${uiState.level + 1}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text("$xpIntoLevel / 250 XP", style = MaterialTheme.typography.labelSmall, color = NeonLavender)
                    }
                    Spacer(Modifier.height(6.dp))
                    AnimatedNeonProgressBar(progress = levelProgress, heightDp = 8)
                }
            }

            // Stats Strip
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileStatBox("Streak", "${uiState.streakDays}d", Icons.Filled.LocalFireDepartment, WarmYellow, Modifier.weight(1f))
                    ProfileStatBox("Total XP", "${uiState.xp}", Icons.Filled.Bolt, NeonLavender, Modifier.weight(1f))
                    ProfileStatBox("Gems", "${uiState.gems}", Icons.Filled.Diamond, CyberCyan, Modifier.weight(1f))
                }
            }

            // Pro Promo Banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(GoldGradient)
                        .clickable(onClick = onOpenPremium)
                        .testTag("profile_premium_card")
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = DeepNavy, modifier = Modifier.size(30.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (uiState.proMember) "CODEVERSE Pro Active" else "Upgrade to CODEVERSE Pro",
                                style = MaterialTheme.typography.titleMedium,
                                color = DeepNavy,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Unlimited practice hearts, Pro tracks & custom BYTE themes",
                                style = MaterialTheme.typography.bodySmall,
                                color = DeepNavy.copy(alpha = 0.85f)
                            )
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = DeepNavy)
                }
            }

            item { SectionHeader(title = "Hub & Progression") }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileNavRow(
                        title = uiState.strings.leaderboardTitle,
                        subtitle = "Compete in Cyber, Neon & Quantum Leagues",
                        icon = Icons.Filled.Leaderboard,
                        accent = CyberCyan,
                        onClick = onOpenLeaderboard,
                        testTag = "profile_leaderboard_row"
                    )
                    ProfileNavRow(
                        title = uiState.strings.achievementsTitle,
                        subtitle = "${uiState.unlockedAchievementIds.size} of ${CurriculumSeed.achievements.size} Badges Unlocked",
                        icon = Icons.Filled.EmojiEvents,
                        accent = WarmYellow,
                        onClick = onOpenAchievements,
                        testTag = "profile_achievements_row"
                    )
                    ProfileNavRow(
                        title = uiState.strings.mascotStudioTitle,
                        subtitle = "Customize BYTE's armor colors & accessories",
                        icon = Icons.Filled.Brush,
                        accent = NeonLavender,
                        onClick = onOpenMascotStudio,
                        testTag = "profile_mascot_studio_row"
                    )
                    ProfileNavRow(
                        title = uiState.strings.projectGalleryTitle,
                        subtitle = "${uiState.savedProjects.size} saved HTML/CSS/JS builds",
                        icon = Icons.Filled.FolderOpen,
                        accent = SoftGreen,
                        onClick = onOpenGallery,
                        testTag = "profile_gallery_row"
                    )
                    ProfileNavRow(
                        title = uiState.strings.settingsTitle,
                        subtitle = "Language (${uiState.language.nativeName}), Goals, Voice & Account",
                        icon = Icons.Filled.Settings,
                        accent = TextSecondary,
                        onClick = onOpenSettings,
                        testTag = "profile_settings_row"
                    )
                }
            }

            item { Spacer(Modifier.height(14.dp)) }
        }
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            containerColor = CardNavy,
            title = { Text("Update Display Name", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    label = { Text("Coder Handle") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateDisplayName(draftName)
                    showEditNameDialog = false
                }) { Text("Save", color = CyberCyan) }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun ProfileStatBox(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    CodeverseCard(borderColor = tint.copy(alpha = 0.45f), modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
private fun ProfileNavRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    testTag: String
) {
    CodeverseCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextMuted)
        }
    }
}

// 19. LEADERBOARD SCREEN
@Composable
fun LeaderboardScreen(
    uiState: CodeverseUiState,
    onBack: () -> Unit
) {
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
                title = uiState.strings.leaderboardTitle,
                subtitle = "Live Weekly XP Rankings",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(uiState.leaderboard, key = { _, m -> m.userId }) { index, member ->
                    val rank = index + 1
                    val rankColor = when (rank) {
                        1 -> WarmYellow
                        2 -> Color(0xFFD5DDF5)
                        3 -> Color(0xFFFF9F68)
                        else -> TextSecondary
                    }
                    CodeverseCard(
                        borderColor = if (member.isCurrentUser) CyberCyan else OutlineNavy,
                        backgroundColor = if (member.isCurrentUser) Color(0xFF282D5C) else CardNavy,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(rankColor.copy(alpha = 0.16f))
                                    .border(1.dp, rankColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("#$rank", style = MaterialTheme.typography.labelLarge, color = rankColor)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (member.isCurrentUser) "${member.displayName} (You)" else member.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Lvl ${member.level} • ${member.league} • ${member.streakDays}d streak",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = "${member.xp} XP",
                                style = MaterialTheme.typography.titleSmall,
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// 20. ACHIEVEMENTS SCREEN
@Composable
fun AchievementsScreen(
    uiState: CodeverseUiState,
    onBack: () -> Unit
) {
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
                title = uiState.strings.achievementsTitle,
                subtitle = "${uiState.unlockedAchievementIds.size} of ${CurriculumSeed.achievements.size} Unlocked",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(CurriculumSeed.achievements, key = { it.id }) { ach ->
                    val unlocked = ach.id in uiState.unlockedAchievementIds
                    CodeverseCard(
                        borderColor = if (unlocked) SoftGreen.copy(alpha = 0.65f) else OutlineNavy,
                        backgroundColor = if (unlocked) SoftGreenContainer.copy(alpha = 0.42f) else CardNavy,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (unlocked) SoftGreen.copy(alpha = 0.2f) else DeepNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (unlocked) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = if (unlocked) WarmYellow else TextMuted
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${ach.category.uppercase()} • +${ach.xpBonus} XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (unlocked) SoftGreen else TextMuted
                                )
                                Text(ach.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                Text(ach.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            if (unlocked) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "Unlocked", tint = SoftGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 21. MASCOT CUSTOMIZATION STUDIO SCREEN
@Composable
fun MascotCustomizationStudioScreen(
    uiState: CodeverseUiState,
    onBack: () -> Unit,
    onSelectTheme: (ByteColorTheme) -> Unit,
    onSelectAccessory: (ByteAccessory) -> Unit
) {
    var previewExpr by remember { mutableStateOf(ByteExpression.EXCITED) }

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
                title = "BYTE Customization Studio",
                subtitle = "Personalize your futuristic coding companion",
                onBack = onBack,
                trailing = {
                    StatBadgePill(
                        icon = Icons.Filled.Diamond,
                        value = "${uiState.gems}",
                        tint = CyberCyan
                    )
                }
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Live Interactive Preview Stage
                item {
                    CodeverseCard(
                        borderColor = CyberCyan.copy(alpha = 0.55f),
                        backgroundColor = Color(0xFF1F2244)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(listOf(uiState.byteColorTheme.accent.copy(alpha = 0.35f), DeepNavy))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                ByteMascot(
                                    expression = previewExpr,
                                    colorTheme = uiState.byteColorTheme,
                                    accessory = uiState.byteAccessory,
                                    size = 152.dp
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "${uiState.byteColorTheme.title} • ${uiState.byteAccessory.title}",
                                style = MaterialTheme.typography.titleSmall,
                                color = CyberCyan
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(ByteExpression.HAPPY, ByteExpression.EXCITED, ByteExpression.FOCUSED, ByteExpression.CELEBRATING).forEach { expr ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (previewExpr == expr) CyberPurple else DeepNavy)
                                            .clickable { previewExpr = expr }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(expr.label, style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                item { SectionHeader(title = "Chassis Color Themes") }

                items(ByteColorTheme.entries, key = { it.id }) { theme ->
                    val equipped = uiState.byteColorTheme == theme
                    val owned = theme.id in uiState.ownedThemeIds || theme.gemCost == 0
                    CodeverseCard(
                        borderColor = if (equipped) CyberCyan else OutlineNavy,
                        onClick = { onSelectTheme(theme) },
                        modifier = Modifier.fillMaxWidth().testTag("studio_theme_${theme.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(theme.bodySecondary, theme.accent)))
                                        .border(1.5.dp, theme.eyeGlow, CircleShape)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(theme.title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                            }
                            Text(
                                text = when {
                                    equipped -> "Equipped"
                                    owned -> "Equip"
                                    else -> "${theme.gemCost} Gems"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = if (equipped) SoftGreen else CyberCyan
                            )
                        }
                    }
                }

                item { SectionHeader(title = "Wearable Accessories") }

                items(ByteAccessory.entries, key = { it.id }) { acc ->
                    val equipped = uiState.byteAccessory == acc
                    val owned = acc.id in uiState.ownedAccessoryIds || acc.gemCost == 0
                    CodeverseCard(
                        borderColor = if (equipped) NeonLavender else OutlineNavy,
                        onClick = { onSelectAccessory(acc) },
                        modifier = Modifier.fillMaxWidth().testTag("studio_acc_${acc.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(acc.title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text(acc.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Text(
                                text = when {
                                    equipped -> "Equipped"
                                    owned -> "Equip"
                                    else -> "${acc.gemCost} Gems"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = if (equipped) SoftGreen else WarmYellow
                            )
                        }
                    }
                }
            }
        }
    }
}
