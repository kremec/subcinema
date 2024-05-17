package com.subbyte.subcinema

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.tv.material3.Surface
import com.subbyte.subcinema.entrybrowser.EntryBrowserScreen
import com.subbyte.subcinema.home.HomeScreen
import com.subbyte.subcinema.mediaplayer.MediaPlayerScreen
import com.subbyte.subcinema.settings.SettingsScreen
import com.subbyte.subcinema.ui.theme.SubcinemaTheme
import com.subbyte.subcinema.utils.EntryLocation
import com.subbyte.subcinema.utils.InputUtil
import com.subbyte.subcinema.utils.NavUtil
import com.subbyte.subcinema.utils.SettingsUtil
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        System.loadLibrary("vlc")

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.hide(WindowInsetsCompat.Type.statusBars())
        wic.hide(WindowInsetsCompat.Type.navigationBars())
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE // or SCREEN_ORIENTATION_LANDSCAPE

        SettingsUtil.init(this)

        setContent {
            SubcinemaTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .focusable(true),
                    shape = RectangleShape
                ) {
                    AppNavHost(navController = rememberNavController())
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        event?.let {
            lifecycleScope.launch {
                InputUtil._keyDownEvents.emit(it)
            }
        }

        return super.onKeyDown(keyCode, event)
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = "${Screen.MainMenu.route}/ / ",
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(
            route = "${Screen.MainMenu.route}/{pathlocation}/{openentrypath}",
            arguments = listOf(
                navArgument("pathlocation") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("openentrypath") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val pathLocation = it.arguments?.getString("pathlocation", null)
            val location =
                if (pathLocation.isNullOrBlank()) null
                else if (pathLocation == EntryLocation.LOCAL.name) EntryLocation.LOCAL
                else EntryLocation.SMB
            val openEntryPath = it.arguments?.getString("openentrypath", null)
            MainMenu(navController, location, if (openEntryPath.isNullOrBlank()) null else NavUtil.deserializeString(openEntryPath))
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.LocalEntryBrowser.route) {
            EntryBrowserScreen(
                navController,
                EntryLocation.LOCAL,
                null,
                null
            )
        }
        composable(Screen.SmbEntryBrowser.route) {
            EntryBrowserScreen(
                navController,
                EntryLocation.SMB,
                null,
                null
            )
        }
        composable(
            route="${Screen.MediaPlayer.route}/{media}",
            arguments = listOf(
                navArgument("media") {
                    type = NavType.StringType
                }
            )
        ) {
            val jsonStringMedia = it.arguments?.getString("media", null)
            MediaPlayerScreen(
                navController,
                NavUtil.deserializeMedia(jsonStringMedia ?: "")
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController, null)
        }
    }
}
