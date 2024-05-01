package com.subbyte.subcinema.entrybrowser

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.subbyte.subcinema.Screen
import com.subbyte.subcinema.models.Entry
import com.subbyte.subcinema.utils.StorageUtil
import kotlinx.coroutines.launch
import java.lang.Integer.min

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

    entryBrowserViewModel.openEntry(rootPath, entriesPerPage, navController)
    val entriesState by entryBrowserViewModel.entries.collectAsState()
    val numOfEntries by entryBrowserViewModel.numOfEntries.collectAsState()

    val focusManager = LocalFocusManager.current
    val firstEntryFocusRequester = remember { FocusRequester() }
    val lastEntryFocusRequester = remember { FocusRequester() }
    val focusedEntryIndex = remember { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()
    val lazyListState = rememberTvLazyListState()

    fun openEntry(entry: Entry) {

        if (entry.path.length < rootPath.length) {
            navController.navigate(Screen.Home.route)
        }
        else {
            entryBrowserViewModel.openEntry(entry.path, entriesPerPage, navController)
            scope.launch {
                lazyListState.scrollToItem(0)
                firstEntryFocusRequester.requestFocus()
            }
            focusedEntryIndex.intValue = 0
        }
    }

    TvLazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .focusGroup()
            .onKeyEvent {
                if (it.nativeKeyEvent.action == NativeKeyEvent.ACTION_UP) {
                    when (it.nativeKeyEvent.keyCode) {
                        NativeKeyEvent.KEYCODE_DPAD_DOWN -> {
                            focusedEntryIndex.intValue++
                            val lastVisibleEntryIndex =
                                min(lazyListState.firstVisibleItemIndex + entriesPerPage, numOfEntries - 1)

                            if (focusedEntryIndex.intValue >= lastVisibleEntryIndex) {
                                if (focusedEntryIndex.intValue == numOfEntries) {

                                    scope.launch {
                                        lazyListState.scrollToItem(0)
                                        firstEntryFocusRequester.requestFocus()
                                    }
                                    focusedEntryIndex.intValue = 0
                                } else {
                                    scope.launch {
                                        lazyListState.scrollToItem((focusedEntryIndex.intValue % numOfEntries - 1) + 1)
                                    }
                                }
                            }
                        }
                        NativeKeyEvent.KEYCODE_DPAD_UP -> {
                            focusedEntryIndex.intValue--

                            if (focusedEntryIndex.intValue <= lazyListState.firstVisibleItemIndex) {
                                if (focusedEntryIndex.intValue < 0) {
                                    scope.launch {
                                        lazyListState.scrollToItem((numOfEntries / entriesPerPage) * entriesPerPage)
                                        lastEntryFocusRequester.requestFocus()
                                    }
                                    focusedEntryIndex.intValue = numOfEntries - 1
                                } else {
                                    scope.launch {
                                        lazyListState.scrollToItem((focusedEntryIndex.intValue / entriesPerPage) * entriesPerPage)
                                    }
                                }
                            }
                        }
                        NativeKeyEvent.KEYCODE_SPACE -> {
                            focusManager.moveFocus(FocusDirection.Left)
                        }
                    }
                }
                false
            },
        state = lazyListState,
        userScrollEnabled = false
    ) {
        items(entriesState) { entry ->
            EntryRow(
                entry, (if (entry.index == 0) Modifier.focusRequester(firstEntryFocusRequester) else if (entry.index == numOfEntries-1) Modifier.focusRequester(lastEntryFocusRequester) else Modifier)
            ) { openEntry(entry) }
        }
    }
    LaunchedEffect(Unit) {
        firstEntryFocusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EntryRow(
    entry: Entry,
    modifier: Modifier,
    openEntry: () -> Unit,
) {
    Row () {
        val border : MutableState<BorderStroke?> = remember{ mutableStateOf(null) }
        TextButton(
            modifier = modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) {
                        border.value = BorderStroke(1.dp, Color.White)
                    } else border.value = null
                },
            onClick = {
                openEntry()
            },
            shape = RectangleShape,
            border = border.value
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = entry.name,
                textAlign = TextAlign.Left
            )
        }
    }
}
