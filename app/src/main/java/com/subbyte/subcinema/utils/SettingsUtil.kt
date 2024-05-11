package com.subbyte.subcinema.utils

import android.content.Context
import android.content.SharedPreferences
import com.subbyte.subcinema.models.Setting

object SettingsUtil {
    lateinit var sharedPreferences: SharedPreferences
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("Preferences_Settings", Context.MODE_PRIVATE)
    }

    val EntryBrowser_EntriesPerPage = Setting(
        0,
        "EntryBrowser_EntriesPerPage",
        "Entries per page",
        1
    )

    val EntryBrowser_SmbDomain = Setting(
        1,
        "EntryBrowser_SmbDomain",
        "SMB domain",
        ""
    )
    val EntryBrowser_SmbRoot = Setting(
        2,
        "EntryBrowser_SmbRoot",
        "SMB root",
        ""
    )
    val EntryBrowser_SmbUsername = Setting(
        3,
        "EntryBrowser_SmbUsername",
        "SMB username",
        ""
    )
    val EntryBrowser_SmbPassword = Setting(
        4,
        "EntryBrowser_SmbPassword",
        "SMB password",
        ""
    )


    inline fun <reified T> saveData(setting: Setting, value: T) {
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> editor.putString(setting.key, value)
            is Int -> editor.putInt(setting.key, value)
            is Long -> editor.putLong(setting.key, value)
            is Float -> editor.putFloat(setting.key, value)
            is Boolean -> editor.putBoolean(setting.key, value)
            // Add more cases as needed for other types
            else -> throw IllegalArgumentException("Unsupported value type")
        }
        editor.apply()
    }

    inline fun <reified T> getData(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            // Add more cases as needed for other types
            else -> throw IllegalArgumentException("Unsupported value type")
        }
    }
}
