package com.subbyte.subcinema.models

import com.subbyte.subcinema.utils.EntryLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Media(
    @SerialName("mediapath")
    val mediaPath: String,
    @SerialName("mediadirpath")
    val mediaDirPath: String,
    @SerialName("subtitlepaths")
    val subtitlePaths: List<String>,
    @SerialName("medialocation")
    val mediaLocation: EntryLocation
)
