package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.model.ByteAccessory
import com.example.domain.model.ByteColorTheme
import com.example.domain.model.ByteExpression
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NeonLavender
import com.example.ui.theme.OutlineNavy
import com.example.ui.theme.SoftGreen
import com.example.ui.theme.WarmYellow

/**
 * Programmatic, animated futuristic robot mascot BYTE.
 * Renders rounded white/lavender proportions, purple joints, cyan glowing eyes,
 * all 7 dynamic expressions, floating idle physics, celebration bounce, and accessories.
 */
@Composable
fun ByteMascot(
    expression: ByteExpression = ByteExpression.HAPPY,
    colorTheme: ByteColorTheme = ByteColorTheme.LAVENDER_PRIME,
    accessory: ByteAccessory = ByteAccessory.NONE,
    size: Dp = 128.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "byte_motion")

    val hoverOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (expression == ByteExpression.EXCITED || expression == ByteExpression.CELEBRATING) 550 else 1500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "byte_hover"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "byte_pulse"
    )

    val headTilt by infiniteTransition.animateFloat(
        initialValue = when (expression) {
            ByteExpression.THINKING -> -5f
            ByteExpression.CONFUSED -> 7f
            ByteExpression.CELEBRATING -> -6f
            else -> -1.5f
        },
        targetValue = when (expression) {
            ByteExpression.THINKING -> 2f
            ByteExpression.CONFUSED -> 11f
            ByteExpression.CELEBRATING -> 6f
            else -> 1.5f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "byte_tilt"
    )

    Canvas(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = "BYTE robot mascot feeling ${expression.label}" }
    ) {
        val w = this.size.width
        val h = this.size.height
        val floatY = hoverOffset * (h / 128f)

        // Subtle cyan/purple ambient floor shadow
        drawOval(
            color = colorTheme.eyeGlow.copy(alpha = 0.22f * pulseAlpha),
            topLeft = Offset(w * 0.25f, h * 0.87f),
            size = Size(w * 0.50f, h * 0.08f)
        )

        // Optional Quantum Cape behind body
        if (accessory == ByteAccessory.PIXEL_CAPE) {
            val capePath = Path().apply {
                moveTo(w * 0.27f, h * 0.58f + floatY)
                lineTo(w * 0.14f, h * 0.84f + floatY)
                lineTo(w * 0.86f, h * 0.84f + floatY)
                lineTo(w * 0.73f, h * 0.58f + floatY)
                close()
            }
            drawPath(
                path = capePath,
                brush = Brush.verticalGradient(
                    listOf(CyberPurple.copy(alpha = 0.9f), CyberCyan.copy(alpha = 0.45f))
                )
            )
        }

        // Rounded futuristic torso
        val bodyLeft = w * 0.27f
        val bodyTop = h * 0.57f + floatY
        val bodyW = w * 0.46f
        val bodyH = h * 0.27f
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(colorTheme.bodyPrimary, colorTheme.bodySecondary)),
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyW, bodyH),
            cornerRadius = CornerRadius(w * 0.16f, w * 0.16f)
        )
        drawRoundRect(
            color = colorTheme.accent.copy(alpha = 0.45f),
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyW, bodyH),
            cornerRadius = CornerRadius(w * 0.16f, w * 0.16f),
            style = Stroke(width = w * 0.018f)
        )

        // Chest energy core
        val coreCenter = Offset(w * 0.50f, bodyTop + bodyH * 0.52f)
        drawCircle(
            color = colorTheme.accent.copy(alpha = 0.28f),
            radius = w * 0.095f,
            center = coreCenter
        )
        drawCircle(
            color = colorTheme.eyeGlow.copy(alpha = pulseAlpha),
            radius = w * 0.055f,
            center = coreCenter
        )

        // Cute floating pill arms (raised when celebrating or excited)
        val armLift = if (expression == ByteExpression.CELEBRATING || expression == ByteExpression.EXCITED) {
            -h * 0.08f
        } else 0f

        drawRoundRect(
            color = colorTheme.bodySecondary,
            topLeft = Offset(w * 0.14f, h * 0.60f + floatY + armLift),
            size = Size(w * 0.10f, h * 0.17f),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
        )
        drawRoundRect(
            color = colorTheme.bodySecondary,
            topLeft = Offset(w * 0.76f, h * 0.60f + floatY + armLift),
            size = Size(w * 0.10f, h * 0.17f),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
        )

        // Head group with subtle animated tilt
        rotate(degrees = headTilt, pivot = Offset(w * 0.5f, h * 0.42f + floatY)) {
            // Antenna stem & glowing orb
            drawLine(
                color = colorTheme.accent,
                start = Offset(w * 0.50f, h * 0.20f + floatY),
                end = Offset(w * 0.50f, h * 0.11f + floatY),
                strokeWidth = w * 0.03f,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = colorTheme.eyeGlow.copy(alpha = 0.35f * pulseAlpha),
                radius = w * 0.068f,
                center = Offset(w * 0.50f, h * 0.10f + floatY)
            )
            drawCircle(
                color = colorTheme.eyeGlow,
                radius = w * 0.038f,
                center = Offset(w * 0.50f, h * 0.10f + floatY)
            )

            // Ear pods (or Synth Headset)
            val earColor = if (accessory == ByteAccessory.DEV_HEADSET) CyberCyan else colorTheme.accent
            drawRoundRect(
                color = earColor,
                topLeft = Offset(w * 0.14f, h * 0.31f + floatY),
                size = Size(w * 0.08f, h * 0.16f),
                cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
            )
            drawRoundRect(
                color = earColor,
                topLeft = Offset(w * 0.78f, h * 0.31f + floatY),
                size = Size(w * 0.08f, h * 0.16f),
                cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
            )

            if (accessory == ByteAccessory.DEV_HEADSET) {
                drawArc(
                    color = CyberPurple,
                    startAngle = 190f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(w * 0.16f, h * 0.14f + floatY),
                    size = Size(w * 0.68f, h * 0.36f),
                    style = Stroke(width = w * 0.042f, cap = StrokeCap.Round)
                )
            }

            // Main rounded head shell
            val headLeft = w * 0.19f
            val headTop = h * 0.20f + floatY
            val headW = w * 0.62f
            val headH = h * 0.37f
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(colorTheme.bodyPrimary, colorTheme.bodySecondary)),
                topLeft = Offset(headLeft, headTop),
                size = Size(headW, headH),
                cornerRadius = CornerRadius(w * 0.18f, w * 0.18f)
            )
            drawRoundRect(
                color = colorTheme.accent.copy(alpha = 0.55f),
                topLeft = Offset(headLeft, headTop),
                size = Size(headW, headH),
                cornerRadius = CornerRadius(w * 0.18f, w * 0.18f),
                style = Stroke(width = w * 0.02f)
            )

            // Face visor screen (Deep Navy glass)
            val visorLeft = w * 0.25f
            val visorTop = h * 0.26f + floatY
            val visorW = w * 0.50f
            val visorH = h * 0.25f
            drawRoundRect(
                color = DeepNavy,
                topLeft = Offset(visorLeft, visorTop),
                size = Size(visorW, visorH),
                cornerRadius = CornerRadius(w * 0.11f, w * 0.11f)
            )

            // Dynamic glowing eyes + mouth based on ByteExpression
            val leftEyeX = w * 0.39f
            val rightEyeX = w * 0.61f
            val eyeY = h * 0.365f + floatY
            val eyeStroke = Stroke(width = w * 0.032f, cap = StrokeCap.Round)
            val eyeColor = when (expression) {
                ByteExpression.CELEBRATING -> SoftGreen
                ByteExpression.CONFUSED -> WarmYellow
                else -> colorTheme.eyeGlow
            }

            when (expression) {
                ByteExpression.HAPPY, ByteExpression.ENCOURAGING -> {
                    // Glowing rounded eyes with soft halo
                    drawCircle(eyeColor.copy(alpha = 0.25f), w * 0.075f, Offset(leftEyeX, eyeY))
                    drawCircle(eyeColor.copy(alpha = 0.25f), w * 0.075f, Offset(rightEyeX, eyeY))
                    drawCircle(eyeColor, w * 0.044f, Offset(leftEyeX, eyeY))
                    drawCircle(eyeColor, w * 0.044f, Offset(rightEyeX, eyeY))
                    // Smile arc
                    drawArc(
                        color = eyeColor,
                        startAngle = 15f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(w * 0.43f, h * 0.405f + floatY),
                        size = Size(w * 0.14f, h * 0.065f),
                        style = Stroke(width = w * 0.024f, cap = StrokeCap.Round)
                    )
                }

                ByteExpression.EXCITED, ByteExpression.CELEBRATING -> {
                    // Joyful curved ^_^ eyes
                    drawArc(
                        color = eyeColor,
                        startAngle = 195f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(leftEyeX - w * 0.055f, eyeY - h * 0.03f),
                        size = Size(w * 0.11f, h * 0.07f),
                        style = eyeStroke
                    )
                    drawArc(
                        color = eyeColor,
                        startAngle = 195f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(rightEyeX - w * 0.055f, eyeY - h * 0.03f),
                        size = Size(w * 0.11f, h * 0.07f),
                        style = eyeStroke
                    )
                    // Big happy grin
                    drawArc(
                        color = eyeColor,
                        startAngle = 5f,
                        sweepAngle = 170f,
                        useCenter = false,
                        topLeft = Offset(w * 0.41f, h * 0.40f + floatY),
                        size = Size(w * 0.18f, h * 0.08f),
                        style = Stroke(width = w * 0.028f, cap = StrokeCap.Round)
                    )
                }

                ByteExpression.THINKING -> {
                    // Left eye normal, right eye slightly narrowed looking upward
                    drawCircle(eyeColor, w * 0.042f, Offset(leftEyeX, eyeY - h * 0.01f))
                    drawLine(
                        color = eyeColor,
                        start = Offset(rightEyeX - w * 0.045f, eyeY),
                        end = Offset(rightEyeX + w * 0.045f, eyeY - h * 0.015f),
                        strokeWidth = w * 0.03f,
                        cap = StrokeCap.Round
                    )
                    // Thought dots
                    drawCircle(NeonLavender, w * 0.018f, Offset(w * 0.46f, h * 0.445f + floatY))
                    drawCircle(NeonLavender, w * 0.018f, Offset(w * 0.50f, h * 0.445f + floatY))
                    drawCircle(NeonLavender, w * 0.018f, Offset(w * 0.54f, h * 0.445f + floatY))
                }

                ByteExpression.CONFUSED -> {
                    // Asymmetric big/small eyes + wavy mouth
                    drawCircle(eyeColor, w * 0.052f, Offset(leftEyeX, eyeY))
                    drawCircle(eyeColor, w * 0.028f, Offset(rightEyeX, eyeY))
                    drawLine(
                        color = eyeColor,
                        start = Offset(w * 0.44f, h * 0.445f + floatY),
                        end = Offset(w * 0.56f, h * 0.435f + floatY),
                        strokeWidth = w * 0.024f,
                        cap = StrokeCap.Round
                    )
                }

                ByteExpression.FOCUSED -> {
                    // Sharp glowing coding visor bars
                    drawRoundRect(
                        color = eyeColor,
                        topLeft = Offset(leftEyeX - w * 0.055f, eyeY - h * 0.018f),
                        size = Size(w * 0.11f, h * 0.036f),
                        cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
                    )
                    drawRoundRect(
                        color = eyeColor,
                        topLeft = Offset(rightEyeX - w * 0.055f, eyeY - h * 0.018f),
                        size = Size(w * 0.11f, h * 0.036f),
                        cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
                    )
                    drawLine(
                        color = eyeColor.copy(alpha = 0.85f),
                        start = Offset(w * 0.45f, h * 0.445f + floatY),
                        end = Offset(w * 0.55f, h * 0.445f + floatY),
                        strokeWidth = w * 0.024f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Accessory: Holo Visor overlay
            if (accessory == ByteAccessory.NEON_VISOR) {
                drawRoundRect(
                    color = CyberCyan.copy(alpha = 0.24f),
                    topLeft = Offset(w * 0.22f, h * 0.29f + floatY),
                    size = Size(w * 0.56f, h * 0.14f),
                    cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                )
                drawRoundRect(
                    color = CyberCyan,
                    topLeft = Offset(w * 0.22f, h * 0.29f + floatY),
                    size = Size(w * 0.56f, h * 0.14f),
                    cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
                    style = Stroke(width = w * 0.018f)
                )
            }

            // Accessory: Binary Crown
            if (accessory == ByteAccessory.CODE_CROWN) {
                val crownPath = Path().apply {
                    moveTo(w * 0.34f, h * 0.21f + floatY)
                    lineTo(w * 0.37f, h * 0.11f + floatY)
                    lineTo(w * 0.44f, h * 0.16f + floatY)
                    lineTo(w * 0.50f, h * 0.08f + floatY)
                    lineTo(w * 0.56f, h * 0.16f + floatY)
                    lineTo(w * 0.63f, h * 0.11f + floatY)
                    lineTo(w * 0.66f, h * 0.21f + floatY)
                    close()
                }
                drawPath(path = crownPath, color = WarmYellow)
            }
        }

        // Celebration floating sparkles
        if (expression == ByteExpression.CELEBRATING || expression == ByteExpression.EXCITED) {
            drawCircle(WarmYellow.copy(alpha = pulseAlpha), w * 0.026f, Offset(w * 0.15f, h * 0.20f))
            drawCircle(CyberCyan.copy(alpha = pulseAlpha), w * 0.022f, Offset(w * 0.86f, h * 0.24f))
            drawCircle(SoftGreen.copy(alpha = pulseAlpha), w * 0.02f, Offset(w * 0.82f, h * 0.72f))
        }
    }
}

/**
 * Reusable BYTE companion banner with speech bubble and voice narration button.
 */
@Composable
fun ByteSpeechCard(
    message: String,
    expression: ByteExpression,
    colorTheme: ByteColorTheme,
    accessory: ByteAccessory,
    onSpeakClick: (() -> Unit)? = null,
    onMascotClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CardNavy)
            .border(1.dp, OutlineNavy, RoundedCornerShape(22.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(DeepNavy)
                .border(1.5.dp, CyberCyan.copy(alpha = 0.45f), CircleShape)
                .then(
                    if (onMascotClick != null) Modifier.clickable(onClick = onMascotClick)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            ByteMascot(
                expression = expression,
                colorTheme = colorTheme,
                accessory = accessory,
                size = 66.dp
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "BYTE",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberCyan
                )
                Text(
                    text = "• ${expression.label}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonLavender
                )
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (onSpeakClick != null) {
            IconButton(
                onClick = onSpeakClick,
                modifier = Modifier.testTag("byte_speak_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Hear BYTE voice narration",
                    tint = NeonLavender
                )
            }
        }
    }
}
