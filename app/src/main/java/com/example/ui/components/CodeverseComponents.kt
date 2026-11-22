package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CodeTextStyle
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NeonLavender
import com.example.ui.theme.OutlineNavy
import com.example.ui.theme.PrimaryAccentGradient
import com.example.ui.theme.SpaceVoid
import com.example.ui.theme.SyntaxAttr
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxNumber
import com.example.ui.theme.SyntaxString
import com.example.ui.theme.SyntaxTag
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CodeverseCard(
    modifier: Modifier = Modifier,
    borderColor: Color = OutlineNavy,
    backgroundColor: Color = CardNavy,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun CodeverseGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    brush: Brush = PrimaryAccentGradient,
    testTag: String = "gradient_button"
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .heightIn(min = 52.dp)
            .clip(shape)
            .background(if (enabled) brush else SolidColor(OutlineNavy.copy(alpha = 0.5f)))
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CodeverseSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "secondary_button"
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineNavy),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CardNavy,
            contentColor = TextPrimary
        ),
        modifier = modifier
            .heightIn(min = 50.dp)
            .testTag(testTag)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonLavender,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun StatBadgePill(
    icon: ImageVector,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(999.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(DeepNavy)
            .border(1.dp, tint.copy(alpha = 0.45f), shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AnimatedNeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    heightDp: Int = 8,
    brush: Brush = PrimaryAccentGradient
) {
    val clamped by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(500),
        label = "progress_anim"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(CircleShape)
            .background(DeepNavy)
            .border(0.5.dp, OutlineNavy, CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clamped)
                .height(heightDp.dp)
                .clip(CircleShape)
                .background(brush)
        )
    }
}

@Composable
fun CodeverseTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CardNavy)
                    .border(1.dp, OutlineNavy, CircleShape)
                    .testTag("top_bar_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = TextPrimary
                )
            }
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            trailing()
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        if (actionLabel != null && onActionClick != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelMedium,
                color = CyberCyan,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

/**
 * Syntax-highlighted code container using JetBrains Mono.
 * Programming syntax always remains in English regardless of selected UI language.
 */
@Composable
fun SyntaxCodeBlock(
    code: String,
    languageLabel: String,
    modifier: Modifier = Modifier
) {
    val highlighted = remember(code) { highlightCodeSyntax(code) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceVoid)
            .border(1.dp, OutlineNavy, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepNavy)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5C7C)))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFCA57)))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF38E59D)))
            }
            Text(
                text = languageLabel.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan
            )
        }
        Text(
            text = highlighted,
            style = CodeTextStyle,
            color = TextPrimary,
            modifier = Modifier.padding(14.dp)
        )
    }
}

fun highlightCodeSyntax(code: String): AnnotatedString {
    val keywords = setOf(
        "const", "let", "var", "function", "async", "await", "return", "if", "else",
        "try", "catch", "throw", "new", "import", "from", "export", "SELECT", "FROM",
        "WHERE", "LEFT", "JOIN", "ON", "GROUP", "BY", "AS", "COUNT", "CREATE", "INDEX",
        "display", "grid", "flex", "background", "color", "padding", "margin", "git"
    )
    return buildAnnotatedString {
        append(code)
        // Comments
        Regex("//[^\\n]*|/\\*[\\s\\S]*?\\*/").findAll(code).forEach { m ->
            addStyle(SpanStyle(color = SyntaxComment), m.range.first, m.range.last + 1)
        }
        // Strings
        Regex("\"(?:\\\\.|[^\"])*\"|'(?:\\\\.|[^'])*'|`(?:\\\\.|[^`])*`").findAll(code).forEach { m ->
            addStyle(SpanStyle(color = SyntaxString), m.range.first, m.range.last + 1)
        }
        // HTML tags
        Regex("</?[a-zA-Z0-9\\-]+").findAll(code).forEach { m ->
            addStyle(SpanStyle(color = SyntaxTag, fontWeight = FontWeight.SemiBold), m.range.first, m.range.last + 1)
        }
        // Numbers
        Regex("\\b\\d+(\\.\\d+)?(px|rem|fr|%)?\\b").findAll(code).forEach { m ->
            addStyle(SpanStyle(color = SyntaxNumber), m.range.first, m.range.last + 1)
        }
        // Keywords
        Regex("\\b[a-zA-Z_]+\\b").findAll(code).forEach { m ->
            if (m.value in keywords) {
                addStyle(SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.SemiBold), m.range.first, m.range.last + 1)
            }
        }
        // Attributes / CSS properties
        Regex("\\b(class|className|id|src|alt|href|justify-content|align-items|gap|border-radius)\\b").findAll(code).forEach { m ->
            addStyle(SpanStyle(color = SyntaxAttr), m.range.first, m.range.last + 1)
        }
    }
}
