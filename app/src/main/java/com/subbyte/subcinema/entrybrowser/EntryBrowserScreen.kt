package com.subbyte.subcinema.entrybrowser

import android.util.Log
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.subbyte.subcinema.models.Entry

@Composable
fun EntryBrowserScreen(navController: NavHostController, entryBrowserViewModel: EntryBrowserViewModel = viewModel()) {
    entryBrowserViewModel.openEntry(entryBrowserViewModel.getRootPath())
    val entriesState by entryBrowserViewModel.entries.collectAsState()

    TvLazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .focusGroup()
    ) {
        items(entriesState) {entry ->
            EntryRow(entry, entryBrowserViewModel)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EntryRow(entry: Entry, entryBrowserViewModel: EntryBrowserViewModel = viewModel()) {
    Row () {
        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) {
                        Log.d("subcinema", "Focused: ${entry.path}")
                    }
                },
            onClick = {
                Log.d("subcinema", "Clicked: ${entry.path}")
                entryBrowserViewModel.openEntry(entry.path)
            },
            shape = RectangleShape,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = entry.name,
                textAlign = TextAlign.Left
            )
        }
    }
}