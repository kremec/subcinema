package com.subbyte.subcinema.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    Text(text = "SETTINGS")
}