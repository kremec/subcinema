package com.subbyte.subcinema.utils

import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object InputUtil {
    val _keyDownEvents = MutableSharedFlow<KeyEvent>()
    val keyDownEvents = _keyDownEvents.asSharedFlow()
}