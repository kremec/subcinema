package com.subbyte.subcinema

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
) {
    data object MainMenu : Screen(
        "MAINMENU",
        Icons.Filled.Menu,
        "Main menu",
    )

    data object Home : Screen(
        "HOMESCREEN",
        Icons.Filled.Home,
        "Home",
    )
    data object SmbEntryBrowser: Screen(
        "SMBENTRYBROWSERSCREEN",
        Icons.Filled.Cloud,
        "NAS library",
    )
    data object LocalEntryBrowser: Screen(
        "LOCALENTRYBROWSERSCREEN",
        Icons.Filled.VideoLibrary,
        "Local library",
    )
    data object MediaPlayer: Screen(
        "MEDIAPLAYER",
        Icons.Filled.PlayArrow,
        "Media player",
    )

    data object Settings : Screen(
        "SETTINGSSCREEN",
        Icons.Filled.Settings,
        "Settings",
    )
}

val screenList = listOf(
    Screen.Home,
    Screen.SmbEntryBrowser,
    Screen.LocalEntryBrowser,
)