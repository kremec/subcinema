package com.subbyte.subcinema.utils

import android.content.Context
import android.widget.Toast

object ErrorUtil {
    fun showToast(context: Context, message: String) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}