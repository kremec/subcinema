package com.subbyte.subcinema.mediaplayer

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import java.io.File
import java.net.URLConnection
import java.net.URLDecoder


@Composable
fun MediaPlayerScreen(navController: NavHostController, path: String?) {

    val mediaPath = URLDecoder.decode(path) ?: ""
    val mimeType = URLConnection.guessContentTypeFromName(mediaPath)

    if (mimeType != null) {
        if (mimeType.startsWith("video") || mimeType.startsWith("audio")) {
            VideoPlayer(mediaPath)
        }
        else if (mimeType.startsWith("image")) {
            ImagePlayer(mediaPath)
        }
    }
}

@Composable
fun VideoPlayer(videoPath: String) {
    val context = LocalContext.current
    val libVLC = LibVLC(context, ArrayList<String>().apply {
        add("--file-caching=1500")
        add("--network-caching=150")
        add("--live-caching=150")
        add("--drop-late-frames")
        add("--skip-frames")
        add("--clock-jitter=0")
        add("--vout=android-display")
        //add("--rtsp-tcp")
        add("-vvv")
    })
    val vlcView = VLCVideoLayout(context).apply {
        keepScreenOn = true
        fitsSystemWindows = false
        visibility = View.VISIBLE
    }
    val mediaPlayer = MediaPlayer(libVLC).apply {
        attachViews(vlcView, null, false, false)
    }
    Media(libVLC, videoPath).apply {
        setHWDecoderEnabled(true, false)
        mediaPlayer.media = this
    }.release()

    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = {
            vlcView
        }
    )

    LaunchedEffect(Unit) {
        mediaPlayer.play()
    }
    DisposableEffect(Unit) {
        onDispose {
            libVLC.release()
        }
    }
}

@Composable
fun ImagePlayer(imagePath: String) {
    val imgFile = File(imagePath)
    val bitmapFile = BitmapFactory.decodeFile(imgFile.absolutePath)
    val bitmapDrawable = BitmapDrawable(LocalContext.current.resources, bitmapFile)
    val bitmap = bitmapDrawable.bitmap.asImageBitmap()

    Image(
        bitmap = bitmap,
        contentDescription = null,
        modifier = Modifier.fillMaxSize()
    )
}