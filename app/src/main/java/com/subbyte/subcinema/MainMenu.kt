package com.subbyte.subcinema

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusRequester
import androidx.navigation.NavHostController
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
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

    Row (
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val sideMenuWeight = 0.075f
        Column(
            modifier = Modifier
                .weight(sideMenuWeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Spacer(modifier = Modifier.weight(0.1f).alpha(0F))

            for(screen in screenList) {
                Box(
                    modifier = Modifier
                        .weight(0.5f),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        modifier = (
                                if (screen.title == Screen.Home.title) Modifier.focusRequester(homeMenuItemFocusRequester)
                                else if (screen.title == Screen.SmbEntryBrowser.title) Modifier.focusRequester(smbentrybrowserMenuItemFocusRequester)
                                else if (screen.title == Screen.LocalEntryBrowser.title) Modifier.focusRequester(localentrybrowserMenuItemFocusRequester)
                                else Modifier
                                ),
                        onClick = { currentScreen = screen }
                    ) {
                        Icon(
                            imageVector = if (currentScreen == screen) screen.iconSelected else screen.icon,
                            contentDescription = null,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    modifier = Modifier.focusRequester(settingsMenuItemFocusRequester),
                    onClick = { currentScreen = Screen.Settings }
                ) {
                    Icon(
                        imageVector = if (currentScreen == Screen.Settings) Screen.Settings.iconSelected else Screen.Settings.icon,
                        contentDescription = null,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(0.1f))
        }

        Column(
            modifier = Modifier
                .weight(1-sideMenuWeight),
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