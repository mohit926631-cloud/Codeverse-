package com.example.data.remote

import android.util.Log
import com.example.core.i18n.AppLanguage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TUTOR_TAG = "AiTutorRepository"

/**
 * Real Gemini AI Tutor powered by Firebase AI Logic (`gemini-3.8-flash`).
 * Uses lazy initialization, realistic 60-second timeouts, streaming responses,
 * and transparent error propagation without mock fallbacks.
 */
class AiTutorRepository {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    You are BYTE, the friendly futuristic robot coding tutor inside CODEVERSE.
                    You teach full-stack web development (HTML, CSS, JavaScript, React, Node.js, SQL, MongoDB, Git, Deployment).
                    Rules:
                    - Keep answers concise, clear, encouraging, and practical for mobile learners.
                    - ALWAYS keep programming keywords, syntax, and code snippets in English.
                    - Match the requested explanation language (English, Hinglish, or Hindi) for prose explanations.
                    - When debugging, point out the exact bug first, explain why it happens, and provide clean corrected code.
                    - Format code cleanly in fenced code blocks.
                    """.trimIndent()
                )
            }
        )
    }

    fun streamTutorResponse(
        userPrompt: String,
        language: AppLanguage,
        conversationContext: List<Pair<Boolean, String>>
    ): Flow<String> = flow {
        val languageDirective = when (language) {
            AppLanguage.ENGLISH -> "Respond in clear English."
            AppLanguage.HINGLISH -> "Respond in friendly Hinglish (Hindi written in Latin script mixed with natural English tech terms). Keep all code in English."
            AppLanguage.HINDI -> "Respond in simple, clear Hindi (Devanagari). Keep all code snippets and programming keywords strictly in English."
        }

        val recentHistory = conversationContext.takeLast(6).map { (isUser, message) ->
            content(role = if (isUser) "user" else "model") {
                text(message)
            }
        }

        val fullPrompt = "$languageDirective\n\n$userPrompt"

        try {
            withTimeout(60_000L) {
                val chat = model.startChat(history = recentHistory)
                val accumulated = StringBuilder()
                chat.sendMessageStream(fullPrompt).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        accumulated.append(piece)
                        emit(accumulated.toString())
                    }
                }
                if (accumulated.isBlank()) {
                    throw IllegalStateException("BYTE received an empty response from Gemini.")
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TUTOR_TAG, "AI Tutor streaming timed out", e)
            throw IllegalStateException("BYTE AI request timed out after 60 seconds. Please retry.", e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TUTOR_TAG, "Firebase AI Logic tutor error", e)
            throw e
        }
    }
}
