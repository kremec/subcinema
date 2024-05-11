package com.subbyte.subcinema.models

data class Subtitle(
    val type: SubtitleType,
    val internalId: Int,
    val internalName: String,
    val externalPath: String
)

enum class SubtitleType {
    INTERNAL,
    EXTERNAL
}
