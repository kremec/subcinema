package com.subbyte.subcinema

import MainViewModel
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
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
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.subbyte.subcinema.models.Entry
import com.subbyte.subcinema.ui.theme.SubcinemaTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SubcinemaTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .focusable(true),
                    shape = RectangleShape
                ) {
                    EntryNavigation()
                }
            }
        }
    }
}

@Composable
fun EntryNavigation(mainViewModel: MainViewModel = viewModel()) {
    mainViewModel.openEntry(mainViewModel.getRootPath())
    val entriesState by mainViewModel.entries.collectAsState()

    TvLazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .focusGroup()
    ) {
        items(entriesState) {entry ->
            EntryRow(entry, mainViewModel)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EntryRow(entry: Entry, mainViewModel: MainViewModel = viewModel()) {
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
                mainViewModel.openEntry(entry.path)
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
