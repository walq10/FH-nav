package com.fhnav.app.domain.model

data class VoiceCommand(
    val audioResId: Int? = null,
    val ttsText: String? = null,
    val priority: VoicePriority = VoicePriority.NORMAL
) {
    init {
        require(audioResId != null || ttsText != null) {
            "VoiceCommand must have either audioResId or ttsText"
        }
    }
}

enum class VoicePriority {
    LOW, NORMAL, HIGH, URGENT
}
