package com.example.ui.screens.tutor

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.TutorMessage
import com.example.domain.model.TutorQuickAction
import com.example.ui.components.ByteMascot
import com.example.ui.components.CodeverseCard
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CoralError
import com.example.ui.theme.CoralErrorContainer
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepSpaceGradient
import com.example.ui.theme.NeonLavender
import com.example.ui.theme.OutlineNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CodeverseUiState

// 17. AI TUTOR CHAT SCREEN (Real Streaming Firebase AI Logic + Voice Interaction UI)
@Composable
fun AiTutorChatScreen(
    uiState: CodeverseUiState,
    onSendPrompt: (String, TutorQuickAction?) -> Unit,
    onSpeakText: (String) -> Unit,
    onClearChat: () -> Unit,
    onDismissError: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var selectedQuickAction by remember { mutableStateOf<TutorQuickAction?>(null) }
    var voiceNotice by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                inputText = if (inputText.isBlank()) spoken else "$inputText $spoken"
                voiceNotice = null
            }
        }
    }

    LaunchedEffect(uiState.tutorMessages.size) {
        if (uiState.tutorMessages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.tutorMessages.lastIndex)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize()
        ) {
            // Top BYTE AI Tutor Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(CardNavy)
                            .border(1.5.dp, CyberCyan.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        ByteMascot(
                            expression = uiState.tutorByteExpression,
                            colorTheme = uiState.byteColorTheme,
                            accessory = uiState.byteAccessory,
                            size = 44.dp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BYTE AI Tutor",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (uiState.tutorIsStreaming) {
                                "Thinking & streaming..."
                            } else {
                                "Live Gemini • ${uiState.language.nativeName}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberCyan
                        )
                    }
                }
                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier.testTag("tutor_clear_chat_button")
                ) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = "Clear chat", tint = TextSecondary)
                }
            }

            // Quick Action Chips (Explain Code, Find Errors, Give Hint, Line by Line, Practice Quiz)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TutorQuickAction.entries.forEach { action ->
                    val active = selectedQuickAction == action
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (active) CyberPurple else CardNavy)
                            .border(
                                1.dp,
                                if (active) CyberCyan else OutlineNavy,
                                RoundedCornerShape(999.dp)
                            )
                            .clickable {
                                selectedQuickAction = if (active) null else action
                                if (inputText.isBlank()) {
                                    // Pre-fill helpful starter prompt when user taps an action chip directly
                                    inputText = when (action) {
                                        TutorQuickAction.EXPLAIN_CODE -> uiState.jsCode.ifBlank { "const nums = [1,2,3].map(n => n * 2);" }
                                        TutorQuickAction.FIND_ERRORS -> "function greet(name) { const msg = 'Hi '; msg += name; return msg; }"
                                        TutorQuickAction.GIVE_HINT -> "How do I center a card vertically and horizontally with CSS Flexbox?"
                                        TutorQuickAction.LINE_BY_LINE -> "const res = await fetch('/api/users');\nconst data = await res.json();"
                                        TutorQuickAction.PRACTICE_QUESTIONS -> "JavaScript Async/Await and Promises"
                                    }
                                }
                            }
                            .testTag("tutor_action_${action.name.lowercase()}")
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = action.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Transparent error surface (never silent or mocked)
            if (!uiState.tutorError.isNullOrBlank()) {
                CodeverseCard(
                    borderColor = CoralError,
                    backgroundColor = CoralErrorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.tutorError,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Dismiss",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyberCyan,
                            modifier = Modifier
                                .clickable(onClick = onDismissError)
                                .padding(start = 10.dp)
                        )
                    }
                }
            }

            if (!voiceNotice.isNullOrBlank()) {
                Text(
                    text = voiceNotice ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonLavender,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )
            }

            // Messages Stream
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.tutorMessages, key = { it.id }) { msg ->
                    TutorChatBubble(
                        message = msg,
                        onSpeak = { onSpeakText(msg.text) }
                    )
                }
            }

            // Composer with Voice + Send
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask BYTE AI about code...")
                            }
                            speechLauncher.launch(intent)
                        } catch (_: Exception) {
                            voiceNotice = "Speech recognition service is not available on this emulator; type your question below."
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CardNavy)
                        .border(1.dp, OutlineNavy, CircleShape)
                        .testTag("tutor_voice_button")
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = "Voice input", tint = NeonLavender)
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = selectedQuickAction?.let { "${it.label}: paste code or question..." }
                                ?: uiState.strings.askByteTutor,
                            color = TextMuted,
                            maxLines = 1
                        )
                    },
                    maxLines = 4,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardNavy,
                        unfocusedContainerColor = CardNavy,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = OutlineNavy,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tutor_input_field")
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !uiState.tutorIsStreaming) CyberPurple else OutlineNavy)
                        .clickable(enabled = inputText.isNotBlank() && !uiState.tutorIsStreaming) {
                            onSendPrompt(inputText, selectedQuickAction)
                            inputText = ""
                            selectedQuickAction = null
                        }
                        .testTag("tutor_send_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.tutorIsStreaming) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun TutorChatBubble(
    message: TutorMessage,
    onSpeak: () -> Unit
) {
    val isUser = message.isFromUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .background(if (isUser) CyberPurple.copy(alpha = 0.34f) else CardNavy)
                .border(
                    1.dp,
                    if (isUser) CyberPurple else OutlineNavy,
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .padding(14.dp)
        ) {
            if (!isUser) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (message.isStreaming) "BYTE • Streaming..." else "BYTE AI",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan
                    )
                    if (!message.isStreaming && message.text.isNotBlank()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Read BYTE response aloud",
                            tint = NeonLavender,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable(onClick = onSpeak)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
            Text(
                text = message.text.ifBlank { "Analyzing your code..." },
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}
