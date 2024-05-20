package com.subbyte.subcinema.models

import com.subbyte.subcinema.utils.EntryLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    @SerialName("index")
    var index: Int = -1,
    @SerialName("name")
    var name: String = "",
    @SerialName("isfile")
    val isFile: Boolean = false,

    @SerialName("path")
    var path: String = "",
    @SerialName("subtitlepaths")
    var subtitlePaths: List<String> = emptyList(),
    @SerialName("location")
    val location: EntryLocation,
    @SerialName("type")
    val type: String = ""
)
