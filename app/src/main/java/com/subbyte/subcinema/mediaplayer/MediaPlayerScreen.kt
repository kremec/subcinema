package com.subbyte.subcinema.mediaplayer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import java.net.URLDecoder

@Composable
fun MediaPlayerScreen(navController: NavHostController, uri: String?) {

    val context = LocalContext.current
    val exoPlayer = ExoPlayer.Builder(context).build()

    val mediaUri = URLDecoder.decode(uri) ?: ""

    val mediaSource = remember(mediaUri) {
        MediaItem.fromUri(mediaUri)
    }
    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
    }
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
}