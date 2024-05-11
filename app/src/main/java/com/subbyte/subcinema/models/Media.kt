package com.subbyte.subcinema.models

import com.subbyte.subcinema.entrybrowser.EntryLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Media(
    @SerialName("mediapath")
    val mediaPath: String,
    @SerialName("subtitlepaths")
    val subtitlePaths: List<String>,
    @SerialName("medialocation")
    val mediaLocation: EntryLocation
)
