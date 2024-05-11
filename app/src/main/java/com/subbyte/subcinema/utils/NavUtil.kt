package com.subbyte.subcinema.utils

import com.subbyte.subcinema.models.Media
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object NavUtil {
    fun serializeArgument(value: Any): String {
        return when (value) {
            is Media -> URLEncoder.encode(Json.encodeToString(value), StandardCharsets.UTF_8.toString())
            else -> ""
        }
    }

    fun deserializeMedia(encodedValue: String): Media {
        return Json.decodeFromString<Media>(URLDecoder.decode(encodedValue, StandardCharsets.UTF_8.toString()))
    }
}