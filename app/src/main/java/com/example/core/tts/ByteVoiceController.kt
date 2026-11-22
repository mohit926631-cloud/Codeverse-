package com.example.core.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Voice narration controller for the BYTE mascot using Android's native TextToSpeech engine.
 * Provides safe lifecycle management and reactive speaking state for lip-sync / wave visuals.
 */
class ByteVoiceController(context: Context) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            isReady = (status == TextToSpeech.SUCCESS)
            if (isReady) {
                tts?.setPitch(1.12f)
                tts?.setSpeechRate(1.04f)
            }
        }
    }

    fun speak(text: String, languageCode: String = "en") {
        if (!isReady || text.isBlank()) return
        val locale = when (languageCode) {
            "hi" -> Locale("hi", "IN")
            "hinglish" -> Locale("en", "IN")
            else -> Locale.US
        }
        tts?.language = locale
        _isSpeaking.value = true
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "byte_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _isSpeaking.value = false
    }
}
