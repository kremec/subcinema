package com.subbyte.subcinema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.subbyte.subcinema.entrybrowser.EntryBrowserScreen
import com.subbyte.subcinema.entrybrowser.EntryBrowserType
import com.subbyte.subcinema.home.HomeScreen
import com.subbyte.subcinema.mediaplayer.MediaPlayerScreen
import com.subbyte.subcinema.settings.SettingsScreen
import com.subbyte.subcinema.ui.theme.SubcinemaTheme
import com.subbyte.subcinema.utils.StorageUtil


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.hide(WindowInsetsCompat.Type.statusBars())
        wic.hide(WindowInsetsCompat.Type.navigationBars())

        StorageUtil.init(this)

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
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.MainMenu.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Screen.MainMenu.route) {
            MainMenu(navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.LocalEntryBrowser.route) {
            EntryBrowserScreen(navController, EntryBrowserType.LOCAL)
        }
        composable(Screen.SmbEntryBrowser.route) {
            EntryBrowserScreen(navController, EntryBrowserType.SMB)
        }
        composable(
            route="${Screen.MediaPlayer.route}/{mediaurl}",
            arguments = listOf(
                navArgument("mediaurl") {
                    type = NavType.StringType
                }
            )
        ) {
            MediaPlayerScreen(navController, it.arguments?.getString("mediaurl", null))
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
    }
}
