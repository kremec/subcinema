package com.subbyte.subcinema.entrybrowser

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.mutableStateOf
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
import androidx.tv.material3.Text
import com.subbyte.subcinema.Screen
import com.subbyte.subcinema.models.Entry
import com.subbyte.subcinema.utils.EntryLocation
import com.subbyte.subcinema.utils.SettingsUtil
import com.subbyte.subcinema.utils.StorageUtil

@Composable
fun EntryBrowserScreen(
    navController: NavHostController,
    type: EntryLocation,
    menuItemFocusRequester: FocusRequester?,
    openEntryPath: String?
) {

    val entryBrowserViewModel: EntryBrowserViewModel = viewModel()
    entryBrowserViewModel.setType(type)

    val rootPath = entryBrowserViewModel.getRootPath()
    val entriesPerPage = SettingsUtil.getData(SettingsUtil.EntryBrowser_EntriesPerPage.key, SettingsUtil.EntryBrowser_EntriesPerPage.defaultValue) as Int

    val entriesState by entryBrowserViewModel.entries.collectAsState()

    val focusedEntryIndex = remember { mutableIntStateOf(0) }
    val currentPage = focusedEntryIndex.intValue / entriesPerPage
    val startIndex = currentPage * entriesPerPage
    val endIndex = (startIndex + entriesPerPage).coerceAtMost(entriesState.size)
    val pageEntries =
        if (entriesState.isNotEmpty())
            entriesState.subList(startIndex, endIndex)
        else
            emptyList()

    val focusRequester = remember { FocusRequester() }

    val previousOpenEntryPath = remember { mutableStateOf<String?>("") }
    fun setIndexToPreviousDir(entries: List<Entry>, previousEntryPath: String) {
        val openEntry = entries.find {
            it.path == (previousOpenEntryPath.value?.removePrefix("file://") ?: "")
        }
        focusedEntryIndex.intValue = openEntry?.index ?: 0
        previousOpenEntryPath.value = previousEntryPath
    }
    fun openEntry(entry: Entry) {
        if (entry.path.length < rootPath.length) {
            navController.navigate("${Screen.MainMenu.route}/ / ")
        }
        else {
            entryBrowserViewModel.openEntry(
                entry,
                navController,
                ::setIndexToPreviousDir,
                entry.path
            )
        }
    }

    fun moveDown() {
        focusedEntryIndex.intValue++
        if (focusedEntryIndex.intValue == entriesState.size) focusedEntryIndex.intValue = 0
    }
    fun moveUp() {
        focusedEntryIndex.intValue--
        if (focusedEntryIndex.intValue == -1) focusedEntryIndex.intValue = entriesState.size - 1
    }
    fun select() {
        openEntry(entriesState[focusedEntryIndex.intValue])
    }
    fun goBack() {
        openEntry(entriesState[0])
    }

    TvLazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent {
                if (it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                    when (it.nativeKeyEvent.keyCode) {
                        NativeKeyEvent.KEYCODE_DPAD_DOWN -> moveDown()
                        NativeKeyEvent.KEYCODE_DPAD_UP -> moveUp()
                        NativeKeyEvent.KEYCODE_DPAD_CENTER -> select()
                        NativeKeyEvent.KEYCODE_DPAD_LEFT -> menuItemFocusRequester?.requestFocus()
                        NativeKeyEvent.KEYCODE_BACK -> goBack()

                        NativeKeyEvent.KEYCODE_ENTER -> select()
                        NativeKeyEvent.KEYCODE_ESCAPE -> goBack()
                    }
                }
                true
            },
        userScrollEnabled = false
    ) {
        items(pageEntries) { entry ->
            EntryRow(entry, focusedEntryIndex)
        }
    }
    BackHandler {
        goBack()
    }

    fun setIndexToOpenEntry(entries: List<Entry>, previousEntryPath: String) {
        val openEntry = entries.find { it.path == previousEntryPath.removePrefix("file://") }
        focusedEntryIndex.intValue = openEntry?.index ?: 0
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        if (openEntryPath != null) {
            entryBrowserViewModel.openEntry(
                Entry(
                    path = StorageUtil.getEntryDirFromEntryPath(openEntryPath, type),
                    location = type
                ),
                navController,
                ::setIndexToOpenEntry,
                openEntryPath
            )
        }
        else
            entryBrowserViewModel.openEntry(
                Entry(
                    path = rootPath,
                    location = type
                ),
                navController,
                ::setIndexToOpenEntry,
                ""
            )
    }
}

@Composable
fun EntryRow(
    entry: Entry,
    focusedEntryIndex: MutableIntState,
) {
    Row {
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
