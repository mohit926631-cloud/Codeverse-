package com.example.ui.screens.profile

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.domain.model.ByteExpression
import com.example.ui.components.ByteMascot
import com.example.ui.components.CodeverseCard
import com.example.ui.components.CodeverseGradientButton
import com.example.ui.components.CodeverseSecondaryButton
import com.example.ui.components.CodeverseTopBar
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CoralError
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepSpaceGradient
import com.example.ui.theme.GoldGradient
import com.example.ui.theme.NeonLavender
import com.example.ui.theme.OutlineNavy
import com.example.ui.theme.SoftGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmYellow
import com.example.ui.viewmodel.CodeverseUiState

// 22. SETTINGS SCREEN
@Composable
fun SettingsScreen(
    uiState: CodeverseUiState,
    onBack: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onToggleVoiceNarration: (Boolean) -> Unit,
    onToggleSoundEffects: (Boolean) -> Unit,
    onToggleDailyReminder: (Boolean) -> Unit,
    onSignOut: () -> Unit
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
                title = uiState.strings.settingsTitle,
                subtitle = "Language, Voice, Theme & Account",
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SectionHeader(title = "Instruction Language (Code stays English)")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val active = uiState.language == lang
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) CyberPurple.copy(alpha = 0.34f) else CardNavy)
                                .border(
                                    1.dp,
                                    if (active) CyberCyan else OutlineNavy,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectLanguage(lang) }
                                .testTag("settings_lang_${lang.code}")
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.Language,
                                contentDescription = null,
                                tint = if (active) CyberCyan else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = lang.nativeName,
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }

                SectionHeader(title = "Experience & Accessibility")

                SettingsSwitchRow(
                    title = "Futuristic Dark Theme",
                    subtitle = "Midnight Navy default workspace",
                    icon = Icons.Filled.DarkMode,
                    checked = uiState.darkThemeEnabled,
                    onCheckedChange = onToggleDarkTheme,
                    testTag = "settings_dark_theme_switch"
                )
                SettingsSwitchRow(
                    title = "BYTE Voice Narration (TTS)",
                    subtitle = "Hear BYTE read tips and AI Tutor replies aloud",
                    icon = Icons.Filled.RecordVoiceOver,
                    checked = uiState.voiceNarrationEnabled,
                    onCheckedChange = onToggleVoiceNarration,
                    testTag = "settings_voice_switch"
                )
                SettingsSwitchRow(
                    title = "Interactive Sound Cues",
                    subtitle = "Feedback cues for quizzes and Boss Battles",
                    icon = Icons.Filled.VolumeUp,
                    checked = uiState.soundEffectsEnabled,
                    onCheckedChange = onToggleSoundEffects,
                    testTag = "settings_sound_switch"
                )
                SettingsSwitchRow(
                    title = "Daily Streak Reminder",
                    subtitle = "Goal: ${uiState.dailyGoalMinutes} min/day • ${uiState.learningTrack}",
                    icon = Icons.Filled.NotificationsActive,
                    checked = uiState.dailyReminderEnabled,
                    onCheckedChange = onToggleDailyReminder,
                    testTag = "settings_reminder_switch"
                )

                Spacer(Modifier.height(8.dp))
                SectionHeader(title = "Account & Cloud Sync")

                CodeverseCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = uiState.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Text(
                        text = uiState.email.ifBlank { "Google Authenticated Account" },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(12.dp))
                    CodeverseSecondaryButton(
                        text = "Sign Out",
                        icon = Icons.AutoMirrored.Filled.Logout,
                        onClick = onSignOut,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "settings_sign_out_button"
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    CodeverseCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(icon, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = CyberPurple
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}

// 23. PREMIUM SUBSCRIPTION SCREEN
@Composable
fun PremiumSubscriptionScreen(
    uiState: CodeverseUiState,
    onBack: () -> Unit,
    onToggleProPreview: (Boolean) -> Unit
) {
    var annualSelected by remember { mutableStateOf(true) }

    val perks = listOf(
        "Unlimited Boss Battle shields & instant retries",
        "Full-Stack Capstone Architecture review templates",
        "Exclusive BYTE Studio armor themes & holo accessories",
        "Priority BYTE AI Tutor code walkthroughs in Hindi, Hinglish & English",
        "Offline Room access for all saved Coding Playground projects"
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
                .widthIn(max = 680.dp)
                .fillMaxSize()
        ) {
            CodeverseTopBar(
                title = "CODEVERSE Pro",
                subtitle = "Accelerate your Full-Stack journey",
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ByteMascot(
                    expression = ByteExpression.CELEBRATING,
                    colorTheme = uiState.byteColorTheme,
                    accessory = uiState.byteAccessory,
                    size = 134.dp
                )

                Text(
                    text = "Unlock Full-Stack Superpowers",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                // Plan Toggle Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PlanTierCard(
                        title = "Annual Pro",
                        price = "$4.99 / mo",
                        badge = "SAVE 50%",
                        selected = annualSelected,
                        onClick = { annualSelected = true },
                        modifier = Modifier.weight(1f).testTag("plan_annual")
                    )
                    PlanTierCard(
                        title = "Monthly Pro",
                        price = "$9.99 / mo",
                        badge = "FLEXIBLE",
                        selected = !annualSelected,
                        onClick = { annualSelected = false },
                        modifier = Modifier.weight(1f).testTag("plan_monthly")
                    )
                }

                CodeverseCard(
                    borderColor = WarmYellow.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Included with CODEVERSE Pro",
                        style = MaterialTheme.typography.titleMedium,
                        color = WarmYellow,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    perks.forEach { perk ->
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SoftGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(perk, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        }
                    }
                }

                CodeverseGradientButton(
                    text = if (uiState.proMember) "Switch Back to Free Plan" else "Activate Pro Preview",
                    icon = Icons.Filled.WorkspacePremium,
                    brush = if (uiState.proMember) GoldGradient else GoldGradient,
                    onClick = { onToggleProPreview(!uiState.proMember) },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "premium_activate_button"
                )

                Text(
                    text = "Play Billing preview mode: no real charge is processed in this development build.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PlanTierCard(
    title: String,
    price: String,
    badge: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CodeverseCard(
        borderColor = if (selected) WarmYellow else OutlineNavy,
        backgroundColor = if (selected) Color(0xFF332D49) else CardNavy,
        onClick = onClick,
        modifier = modifier
    ) {
        Text(badge, style = MaterialTheme.typography.labelSmall, color = WarmYellow)
        Spacer(Modifier.height(4.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(price, style = MaterialTheme.typography.titleMedium, color = CyberCyan, fontWeight = FontWeight.Bold)
    }
}
