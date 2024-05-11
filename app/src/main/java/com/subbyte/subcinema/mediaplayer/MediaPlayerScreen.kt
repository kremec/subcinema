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
<<<<<<< HEAD
=======
import org.videolan.libvlc.MediaPlayer.Event
import org.videolan.libvlc.interfaces.IMedia
>>>>>>> 280993d (Added subtitles (currently only choosing the first), better navigation arguments)
import org.videolan.libvlc.util.VLCVideoLayout
import java.io.File
import java.net.URLConnection


@Composable
fun MediaPlayerScreen(navController: NavHostController, media: com.subbyte.subcinema.models.Media?) {

    if (media?.mediaPath == null) return

    val mimeType = URLConnection.guessContentTypeFromName(media.mediaPath)

    if (mimeType != null) {
        if (mimeType.startsWith("video") || mimeType.startsWith("audio")) {
            VideoPlayer(media.mediaPath, media.subtitlePaths)
        }
        else if (mimeType.startsWith("image")) {
            ImagePlayer(media.mediaPath)
        }
    }
}

@Composable
fun VideoPlayer(videoPath: String, subtitlePaths: List<String>?) {
    val context = LocalContext.current

    val libVLC = LibVLC(context, ArrayList<String>().apply {
        add("--file-caching=6000")
        add("--network-caching=6000")
        add("--live-caching=6000")
        add("--disc-caching=6000")
        add("--sout-mux-caching=2000")
        add("--drop-late-frames")
        add("--skip-frames")
        add("--clock-jitter=0")

        add("--vout=android-display")
<<<<<<< HEAD
<<<<<<< HEAD
        //add("--rtsp-tcp")
        add("-vvv")
=======
        add("--aout=android")
=======
        add("--aout=audiotrack") // Previously "android", "opensles" -> test difference
        add("--subsdec-encoding=Windows-1250") // Other encodings: https://github.com/videolan/vlc/blob/master/modules/codec/subsdec.c
>>>>>>> 280993d (Added subtitles (currently only choosing the first), better navigation arguments)
        add("-vv")
        add("--http-reconnect")
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
        // setHWDecoderEnabled(true, false) // Test differences with/without
        mediaPlayer.media = this
    }.release()
    if (subtitlePaths?.isNotEmpty() == true) {
        mediaPlayer.addSlave(
            IMedia.Slave.Type.Subtitle,
            Uri.parse(subtitlePaths[0]),
            true
        )
    }


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