package com.subbyte.subcinema

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val iconSelected: ImageVector,
    val title: String,
) {
    data object MainMenu : Screen(
        "MAINMENU",
        Icons.Outlined.Menu,
        Icons.Filled.Menu,
        "Main menu",
    )

    data object Home : Screen(
        "HOMESCREEN",
        Icons.Outlined.Home,
        Icons.Filled.Home,
        "Home",
    )
    data object SmbEntryBrowser: Screen(
        "SMBENTRYBROWSERSCREEN",
        Icons.Outlined.Cloud,
        Icons.Filled.Cloud,
        "NAS library",
    )
    data object LocalEntryBrowser: Screen(
        "LOCALENTRYBROWSERSCREEN",
        Icons.Outlined.VideoLibrary,
        Icons.Filled.VideoLibrary,
        "Local library",
    )
    data object MediaPlayer: Screen(
        "MEDIAPLAYER",
        Icons.Outlined.PlayArrow,
        Icons.Filled.PlayArrow,
        "Media player",
    )

    data object Settings : Screen(
        "SETTINGSSCREEN",
        Icons.Outlined.Settings,
        Icons.Filled.Settings,
        "Settings",
    )
}

val screenList = listOf(
    Screen.Home,
    Screen.SmbEntryBrowser,
    Screen.LocalEntryBrowser,
)