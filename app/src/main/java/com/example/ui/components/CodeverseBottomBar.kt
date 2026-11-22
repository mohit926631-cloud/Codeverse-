package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.i18n.LocalizedStrings
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.OutlineNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class PrimaryTab(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    LEARN("home", Icons.Filled.School, Icons.Outlined.School, "tab_learn"),
    CODE("playground", Icons.Filled.Code, Icons.Outlined.Code, "tab_code"),
    PRACTICE("practice", Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports, "tab_practice"),
    AI_TUTOR("ai_tutor", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "tab_ai_tutor"),
    PROFILE("profile", Icons.Filled.Person, Icons.Outlined.Person, "tab_profile");

    fun label(strings: LocalizedStrings): String = when (this) {
        LEARN -> strings.navLearn
        CODE -> strings.navCode
        PRACTICE -> strings.navPractice
        AI_TUTOR -> strings.navTutor
        PROFILE -> strings.navProfile
    }
}

/**
 * Custom futuristic 5-destination bottom navigation bar with explicit navigationBarsPadding()
 * so gesture bars and 3-button Android nav never overlap active pills or labels.
 */
@Composable
fun CodeverseBottomBar(
    currentRoute: String?,
    strings: LocalizedStrings,
    onSelectTab: (PrimaryTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DeepNavy.copy(alpha = 0.96f))
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardNavy)
                .border(1.dp, OutlineNavy, RoundedCornerShape(24.dp))
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrimaryTab.entries.forEach { tab ->
                val selected = currentRoute == tab.route
                val contentColor by animateColorAsState(
                    targetValue = if (selected) TextPrimary else TextMuted,
                    label = "nav_color"
                )
                val pillModifier = if (selected) {
                    Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    CyberPurple.copy(alpha = 0.34f),
                                    CyberCyan.copy(alpha = 0.24f)
                                )
                            )
                        )
                        .border(1.dp, CyberCyan.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                } else {
                    Modifier.clip(RoundedCornerShape(18.dp))
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .then(pillModifier)
                        .clickable { onSelectTab(tab) }
                        .testTag(tab.testTag)
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label(strings),
                        tint = if (selected) CyberCyan else contentColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = tab.label(strings),
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Adaptive side NavigationRail for tablet / foldable wide layouts.
 */
@Composable
fun CodeverseNavigationRail(
    currentRoute: String?,
    strings: LocalizedStrings,
    onSelectTab: (PrimaryTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = CardNavy,
        contentColor = TextPrimary
    ) {
        Spacer(Modifier.height(16.dp))
        PrimaryTab.entries.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label(strings)
                    )
                },
                label = {
                    Text(
                        text = tab.label(strings),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = CyberCyan,
                    selectedTextColor = TextPrimary,
                    indicatorColor = CyberPurple.copy(alpha = 0.35f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("${tab.testTag}_rail")
            )
        }
    }
}
