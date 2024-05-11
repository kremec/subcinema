package com.subbyte.subcinema.mediaplayer

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
<<<<<<< HEAD
=======
import androidx.compose.ui.input.key.NativeKeyEvent
>>>>>>> 6e7f076 (Fixed audio issues, added simple video controls)
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.subbyte.subcinema.utils.InputUtil
import com.subbyte.subcinema.utils.StorageUtil
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import java.io.File
import java.net.URLConnection
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Composable
fun MediaPlayerScreen(navController: NavHostController, path: String?) {

    val mediaPath = URLDecoder.decode(path, StandardCharsets.UTF_8.toString()) ?: ""
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
<<<<<<< HEAD
        //add("--rtsp-tcp")
        add("-vvv")
=======
        add("--aout=android")
        add("-vv")
        add("--access=smb")
        add("--smb-domain=${StorageUtil.getData(StorageUtil.EntryBrowser_SmbDomain, StorageUtil.DEFAULT_EntryBrowser_SmbDomain)}")
        add("--smb-user=${StorageUtil.getData(StorageUtil.EntryBrowser_SmbUsername, StorageUtil.DEFAULT_EntryBrowser_SmbUsername)}")
        add("--smb-pwd=${StorageUtil.getData(StorageUtil.EntryBrowser_SmbPassword, StorageUtil.DEFAULT_EntryBrowser_SmbPassword)}")
>>>>>>> 6e7f076 (Fixed audio issues, added simple video controls)
    })
    val vlcView = VLCVideoLayout(context).apply {
        keepScreenOn = true
        fitsSystemWindows = false
        visibility = View.VISIBLE
    }
    val mediaPlayer = MediaPlayer(libVLC).apply {
<<<<<<< HEAD
        attachViews(vlcView, null, false, false)
=======
        attachViews(vlcView, null, true, false)
        setEventListener {
            handleVlcEvents(it)
        }
>>>>>>> 6e7f076 (Fixed audio issues, added simple video controls)
    }
    Media(libVLC, Uri.parse(videoPath)).apply {
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
<<<<<<< HEAD
=======

        InputUtil.keyDownEvents.collect {
            if (it.action == NativeKeyEvent.ACTION_DOWN) {
                when (it.keyCode) {
                    NativeKeyEvent.KEYCODE_SPACE -> {
                        if (mediaPlayer.isPlaying) mediaPlayer.pause() else mediaPlayer.play()
                    }
                    NativeKeyEvent.KEYCODE_DPAD_RIGHT -> mediaPlayer.position += 0.01f
                    NativeKeyEvent.KEYCODE_DPAD_LEFT -> mediaPlayer.position -= 0.01f
                }
            }
        }
>>>>>>> 6e7f076 (Fixed audio issues, added simple video controls)
    }
    DisposableEffect(Unit) {
        onDispose {
            libVLC.release()
        }
    }
}
<<<<<<< HEAD
=======
fun handleVlcEvents(event: Event) {
    when (event.type) {
        Event.Playing -> {

        }
        Event.Paused -> {

        }
    }
}
>>>>>>> 6e7f076 (Fixed audio issues, added simple video controls)

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