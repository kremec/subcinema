package com.subbyte.subcinema

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val description: String
) {
    data object MainMenu : Screen(
        "MAINMENU",
        Icons.Outlined.Menu,
        "Main menu",
        ""
    )

    data object Home : Screen(
        "HOMESCREEN",
        Icons.Outlined.Home,
        "Home",
        ""
    )
    data object SmbEntryBrowser: Screen(
        "SMBENTRYBROWSERSCREEN",
        Icons.Outlined.PlayArrow,
        "NAS library",
        "NAS files browser"
    )
    data object LocalEntryBrowser: Screen(
        "LOCALENTRYBROWSERSCREEN",
        Icons.Outlined.PlayArrow,
        "Local library",
        "Local files browser"
    )
    data object MediaPlayer: Screen(
        "MEDIAPLAYER",
        Icons.Outlined.PlayArrow,
        "Media player",
        "Media player"
    )

    data object Settings : Screen(
        "SETTINGSSCREEN",
        Icons.Outlined.Settings,
        "Settings",
        ""
    )
}

val screenList = listOf(
    Screen.Home,
    Screen.SmbEntryBrowser,
    Screen.LocalEntryBrowser,
)