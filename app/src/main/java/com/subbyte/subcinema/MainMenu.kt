package com.subbyte.subcinema

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.navigation.NavHostController
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import androidx.tv.material3.MaterialTheme
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
fun MainMenu(navController: NavHostController, pathLocation: EntryLocation?, openEntryPath: String?) {

    val defaultScreen: Screen = Screen.SmbEntryBrowser
    val defaultSelection: Screen =
        if (openEntryPath == null) defaultScreen
        else if (pathLocation == EntryLocation.LOCAL) Screen.LocalEntryBrowser
        else if (pathLocation == EntryLocation.SMB) Screen.SmbEntryBrowser
        else defaultScreen
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
                    val modifier = when (screen.title) {
                        Screen.Home.title -> Modifier.focusRequester(homeMenuItemFocusRequester)
                        Screen.SmbEntryBrowser.title -> Modifier.focusRequester(smbentrybrowserMenuItemFocusRequester)
                        Screen.LocalEntryBrowser.title -> Modifier.focusRequester(localentrybrowserMenuItemFocusRequester)
                        else -> Modifier
                    }
                    var isFocused by remember { mutableStateOf(false) }
                    IconButton(
                        modifier = modifier.onFocusChanged {
                            isFocused = it.isFocused
                        },
                        onClick = { currentScreen = screen },
                        scale =
                            if (currentScreen == screen)
                                IconButtonDefaults.scale(scale = 1.1f)
                            else
                                IconButtonDefaults.scale(),
                        colors =
                            if (currentScreen == screen)
                                IconButtonDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.onSurface,
                                    contentColor = MaterialTheme.colorScheme.inverseOnSurface
                                )
                            else
                                IconButtonDefaults.colors()
                    ) {
                        Icon(
                            imageVector = if (isFocused) screen.iconSelected else screen.icon,
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
                var isFocused by remember { mutableStateOf(false) }
                IconButton(
                    modifier = Modifier
                        .focusRequester(settingsMenuItemFocusRequester)
                        .onFocusChanged {
                            isFocused = it.isFocused
                        },
                    onClick = { currentScreen = Screen.Settings },
                    scale =
                        if (currentScreen == Screen.Settings)
                            IconButtonDefaults.scale(scale = 1.1f)
                        else
                            IconButtonDefaults.scale(),
                    colors =
                        if (currentScreen == Screen.Settings)
                            IconButtonDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.onSurface,
                                contentColor = MaterialTheme.colorScheme.inverseOnSurface
                            )
                        else
                            IconButtonDefaults.colors()
                ) {
                    Icon(
                        imageVector = if (isFocused) Screen.Settings.iconSelected else Screen.Settings.icon,
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
                        if (pathLocation == EntryLocation.LOCAL) openEntryPath else null
                    )
                }
                Screen.SmbEntryBrowser -> {
                    EntryBrowserScreen(
                        navController,
                        EntryLocation.SMB,
                        smbentrybrowserMenuItemFocusRequester,
                        if (pathLocation == EntryLocation.SMB) openEntryPath else null
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
}