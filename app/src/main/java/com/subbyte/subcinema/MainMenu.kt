package com.subbyte.subcinema

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Icon
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Text
import com.subbyte.subcinema.entrybrowser.EntryBrowserScreen
import com.subbyte.subcinema.home.HomeScreen
import com.subbyte.subcinema.mediaplayer.MediaPlayerScreen
import com.subbyte.subcinema.settings.SettingsScreen
import com.subbyte.subcinema.utils.EntryLocation
import com.subbyte.subcinema.utils.NavUtil.homeMenuItemFocusRequester
import com.subbyte.subcinema.utils.NavUtil.localentrybrowserMenuItemFocusRequester
import com.subbyte.subcinema.utils.NavUtil.settingsMenuItemFocusRequester
import com.subbyte.subcinema.utils.NavUtil.smbentrybrowserMenuItemFocusRequester

@Composable
fun MainMenu(navController: NavHostController, pathLocation: EntryLocation?, openPath: String?) {

    val defaultSelection: Screen =
        if (openPath == null) Screen.Home
        else if (pathLocation == EntryLocation.LOCAL) Screen.LocalEntryBrowser
        else Screen.SmbEntryBrowser
    var currentScreen by remember { mutableStateOf(defaultSelection) }

    NavigationDrawer(
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TvLazyColumn(
                    modifier = Modifier
                        .selectableGroup(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
                ) {
                    items(screenList, key = { it.route }) { screen ->
                        NavigationDrawerItem(
                            modifier = (
                                if (screen.title == Screen.Home.title) Modifier.focusRequester(homeMenuItemFocusRequester)
                                else if (screen.title == Screen.SmbEntryBrowser.title) Modifier.focusRequester(smbentrybrowserMenuItemFocusRequester)
                                else if (screen.title == Screen.LocalEntryBrowser.title) Modifier.focusRequester(localentrybrowserMenuItemFocusRequester)
                                else Modifier
                            ),
                            selected = currentScreen == screen,
                            onClick = { currentScreen = screen },
                            leadingContent = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = null,
                                )
                            }
                        ) {
                            Text(text = screen.title)
                        }
                    }
                }

                NavigationDrawerItem(
                    modifier = Modifier.focusRequester(settingsMenuItemFocusRequester),
                    selected = currentScreen == Screen.Settings,
                    onClick = { currentScreen = Screen.Settings },
                    leadingContent = {
                        Icon(
                            imageVector = Screen.Settings.icon,
                            contentDescription = null,
                        )
                    }
                ) {
                    Text(text = Screen.Settings.title)
                }
            }
        }
    ) {

        /* switching screens based on currentScreen changes */
        when (currentScreen) {
            Screen.Home -> {
                HomeScreen(navController)
            }
            Screen.LocalEntryBrowser -> {
                EntryBrowserScreen(
                    navController,
                    EntryLocation.LOCAL,
                    localentrybrowserMenuItemFocusRequester,
                    if (pathLocation == EntryLocation.LOCAL) openPath else null
                )
            }
            Screen.SmbEntryBrowser -> {
                EntryBrowserScreen(
                    navController,
                    EntryLocation.SMB,
                    smbentrybrowserMenuItemFocusRequester,
                    if (pathLocation == EntryLocation.SMB) openPath else null
                )
            }
            Screen.MediaPlayer -> {
                MediaPlayerScreen(navController, null)
            }

            Screen.Settings -> {
                SettingsScreen(navController, settingsMenuItemFocusRequester)
            }

            Screen.MainMenu -> {}
        }

    }

    LaunchedEffect(Unit) {
        if (openPath == null) homeMenuItemFocusRequester.requestFocus()
        else if (pathLocation == EntryLocation.LOCAL) {
            localentrybrowserMenuItemFocusRequester.requestFocus()
        }
        else {
            smbentrybrowserMenuItemFocusRequester.requestFocus()
        }
    }
}