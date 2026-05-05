package com.fhnav.app.data.model

import com.google.gson.annotations.SerializedName

data class Phrase(
    @SerializedName("id") val id: String = "",
    @SerializedName("category") val category: String = "",
    @SerializedName("text_zh") val textZh: String = "",
    @SerializedName("text_en") val textEn: String = "",
    @SerializedName("audio_url") val audioUrl: String = "",
    @SerializedName("is_preset") val isPreset: Boolean = true
)

enum class PhraseCategory(val zhName: String, val enName: String) {
    GREETING("打招呼", "Greeting"),
    WARNING("警告", "Warning"),
    DIRECTION("方向", "Direction"),
    SPEED("速度", "Speed"),
    EMERGENCY("紧急", "Emergency"),
    CUSTOM("自定义", "Custom")
}
