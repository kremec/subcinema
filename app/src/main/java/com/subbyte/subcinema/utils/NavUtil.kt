package com.subbyte.subcinema.utils

import androidx.compose.ui.focus.FocusRequester
import com.subbyte.subcinema.models.Media
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object NavUtil {

    val homeMenuItemFocusRequester = FocusRequester()
    val smbentrybrowserMenuItemFocusRequester = FocusRequester()
    val localentrybrowserMenuItemFocusRequester = FocusRequester()
    val settingsMenuItemFocusRequester = FocusRequester()

    fun serializeArgument(value: Any): String {
        return when (value) {
            is String -> URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
            is Media -> URLEncoder.encode(Json.encodeToString(value), StandardCharsets.UTF_8.toString())
            else -> ""
        }
    }

    fun deserializeString(encodedValue: String): String {
        return URLDecoder.decode(encodedValue, StandardCharsets.UTF_8.toString())
    }
    fun deserializeMedia(encodedValue: String): Media {
        return Json.decodeFromString<Media>(URLDecoder.decode(encodedValue, StandardCharsets.UTF_8.toString()))
    }
}