package com.subbyte.subcinema.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Text(text = "HOME")
}