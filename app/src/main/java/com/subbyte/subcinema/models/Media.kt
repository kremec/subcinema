package com.subbyte.subcinema.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Media(
    @SerialName("mediapath")
    val mediaPath: String? = null,
    @SerialName("subtitlepaths")
    val subtitlePaths: List<String>? = null
)
