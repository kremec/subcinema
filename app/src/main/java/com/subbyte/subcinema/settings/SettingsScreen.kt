package com.subbyte.subcinema.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.subbyte.subcinema.utils.StorageUtil

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "SETTINGS",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )
        EntryBrowser_EntriesPerPage_Field()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EntryBrowser_EntriesPerPage_Field() {
    var entriesPerPage = StorageUtil.getData(StorageUtil.EntryBrowser_EntriesPerPage, 1)

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val border : MutableState<BorderStroke?> = remember{ mutableStateOf(null) }

    Text(
        text = "Entries per page",
        style = MaterialTheme.typography.labelLarge
    )
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    border.value = BorderStroke(1.dp, Color.White)
                } else {
                    border.value = null
                }
            }
            .onKeyEvent {
                if (it.nativeKeyEvent.action == NativeKeyEvent.ACTION_UP) {
                    when (it.nativeKeyEvent.keyCode) {
                        NativeKeyEvent.KEYCODE_DPAD_LEFT -> {
                            entriesPerPage--
                            StorageUtil.saveData(StorageUtil.EntryBrowser_EntriesPerPage, entriesPerPage)
                        }
                        NativeKeyEvent.KEYCODE_DPAD_RIGHT -> {
                            entriesPerPage++
                            StorageUtil.saveData(StorageUtil.EntryBrowser_EntriesPerPage, entriesPerPage)
                        }

                        NativeKeyEvent.KEYCODE_SPACE -> {
                            focusManager.moveFocus(FocusDirection.Left)
                        }
                    }
                }
                true
            },
        onClick = {},
        shape = RectangleShape,
        border = border.value
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = entriesPerPage.toString(),
            textAlign = TextAlign.Left
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}