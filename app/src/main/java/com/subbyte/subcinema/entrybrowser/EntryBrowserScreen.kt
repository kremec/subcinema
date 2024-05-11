package com.subbyte.subcinema.entrybrowser

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.subbyte.subcinema.Screen
import com.subbyte.subcinema.models.Entry
import com.subbyte.subcinema.utils.StorageUtil

enum class EntryBrowserType {
    LOCAL,
    SMB
}

@Composable
fun EntryBrowserScreen(navController: NavHostController, type: EntryBrowserType) {

    val entryBrowserViewModel: EntryBrowserViewModel = viewModel()
    entryBrowserViewModel.setType(type)

    val rootPath = entryBrowserViewModel.getRootPath()
    val entriesPerPage = StorageUtil.getData(StorageUtil.EntryBrowser_EntriesPerPage, 1)

    val entriesState by entryBrowserViewModel.entries.collectAsState()

    val focusedEntryIndex = remember { mutableIntStateOf(0) }
    val currentPage = remember { mutableIntStateOf(0) }
    val startIndex = currentPage.intValue * entriesPerPage
    val endIndex = (startIndex + entriesPerPage).coerceAtMost(entriesState.size)
    val pageEntries = entriesState.subList(startIndex, endIndex)

    fun openEntry(entry: Entry) {
        if (entry.path.length < rootPath.length) {
            navController.navigate(Screen.MainMenu.route)
        }
        else {
            entryBrowserViewModel.openEntry(entry.path, navController)
        }
    }

    val focusRequester = remember { FocusRequester() }

    TvLazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent {
                if (it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                    when (it.nativeKeyEvent.keyCode) {
                        NativeKeyEvent.KEYCODE_DPAD_DOWN -> {
                            focusedEntryIndex.intValue++
                            if (focusedEntryIndex.intValue == entriesState.size) focusedEntryIndex.intValue =
                                0
                        }
                        NativeKeyEvent.KEYCODE_DPAD_UP -> {
                            focusedEntryIndex.intValue--
                            if (focusedEntryIndex.intValue == -1) focusedEntryIndex.intValue =
                                entriesState.size - 1
                        }
                        NativeKeyEvent.KEYCODE_SPACE -> {
                            openEntry(entriesState[focusedEntryIndex.intValue])
                            focusedEntryIndex.intValue = 0
                            currentPage.intValue = 0
                        }
                    }
                    currentPage.intValue = focusedEntryIndex.intValue / entriesPerPage
                }
                true
            },
        userScrollEnabled = false
    ) {
        items(pageEntries) { entry ->
<<<<<<< HEAD
            EntryRow(
<<<<<<< HEAD
                entry, (if (entry.index == 0) Modifier.focusRequester(firstEntryFocusRequester) else if (entry.index == numOfEntries-1) Modifier.focusRequester(lastEntryFocusRequester) else Modifier)
            ) { openEntry(entry) }
        }
    }
    LaunchedEffect(Unit) {
        firstEntryFocusRequester.requestFocus()
    }
=======
                entry, focusedEntryIndex
            ) { openEntry(entry) }
        }
    }
>>>>>>> 38c6efa (EntryBrowserScreen refactor)
=======
            EntryRow(entry, focusedEntryIndex)
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        entryBrowserViewModel.openEntry(rootPath, navController)
    }
>>>>>>> afa3f14 (EntryBrowserScreen refactor #2, working SMB)
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EntryRow(
    entry: Entry,
    focusedEntryIndex: MutableIntState,
) {
    Row () {
        TextButton(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RectangleShape,
            onClick = {},
            border = if (entry.index == focusedEntryIndex.intValue) BorderStroke(1.dp, Color.White) else null
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = entry.name,
                textAlign = TextAlign.Left
            )
        }
    }
}
