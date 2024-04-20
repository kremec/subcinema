package com.subbyte.subcinema.home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.subbyte.subcinema.NavigationItem

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Button(
        onClick = {
            navController.navigate(NavigationItem.EntryBrowser.route)
            Log.d("subcinema", "GOTO ENTRYBROWSER")
        }
    ) {
        Text(text = "Local files")
    }
}