package com.subbyte.subcinema.utils

import android.content.Context
import android.content.SharedPreferences

object StorageUtil {
    lateinit var sharedPreferences: SharedPreferences
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("Preferences_Settings", Context.MODE_PRIVATE)
    }


    val EntryBrowser_EntriesPerPage = "EntryBrowser_EntriesPerPage"

    inline fun <reified T> saveData(key: String, value: T) {
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is Boolean -> editor.putBoolean(key, value)
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
