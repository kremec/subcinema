package com.subbyte.subcinema.mediaplayer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
<<<<<<< HEAD
<<<<<<< HEAD
import androidx.compose.ui.graphics.asImageBitmap
<<<<<<< HEAD
=======
=======
>>>>>>> edf31f7 (Setup error alerts, SettingsScreen overhaul)
=======
import androidx.compose.ui.graphics.asImageBitmap
>>>>>>> f709910 (Fixed opening smb images, some formats unsupported (.MOV, .tif))
import androidx.compose.ui.input.key.NativeKeyEvent
>>>>>>> 6e7f076 (Fixed audio issues, added simple video controls)
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.subbyte.subcinema.Screen
import com.subbyte.subcinema.entrybrowser.EntryLocation
import com.subbyte.subcinema.utils.ErrorUtil
import com.subbyte.subcinema.utils.InputUtil
import com.subbyte.subcinema.utils.SettingsUtil
import com.subbyte.subcinema.utils.StorageUtil
import jcifs.smb.SmbFileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            VideoPlayer(media, navController)
        }
        else if (mimeType.startsWith("image")) {
            ImagePlayer(media, navController)
        }
    }
}

@Composable
fun VideoPlayer(videoMedia: com.subbyte.subcinema.models.Media, navController: NavHostController) {
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
<<<<<<< HEAD
        add("--smb-domain=${StorageUtil.getData(StorageUtil.EntryBrowser_SmbDomain, StorageUtil.DEFAULT_EntryBrowser_SmbDomain)}")
        add("--smb-user=${StorageUtil.getData(StorageUtil.EntryBrowser_SmbUsername, StorageUtil.DEFAULT_EntryBrowser_SmbUsername)}")
        add("--smb-pwd=${StorageUtil.getData(StorageUtil.EntryBrowser_SmbPassword, StorageUtil.DEFAULT_EntryBrowser_SmbPassword)}")
>>>>>>> 6e7f076 (Fixed audio issues, added simple video controls)
=======
        add("--smb-domain=${SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbDomain.key, SettingsUtil.EntryBrowser_SmbDomain.defaultValue)}")
        add("--smb-user=${SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbUsername.key, SettingsUtil.EntryBrowser_SmbDomain.defaultValue)}")
        add("--smb-pwd=${SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbPassword.key, SettingsUtil.EntryBrowser_SmbPassword.defaultValue)}")
>>>>>>> edf31f7 (Setup error alerts, SettingsScreen overhaul)
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
    Media(libVLC, Uri.parse(videoMedia.mediaPath)).apply {
        // setHWDecoderEnabled(true, false) // Test differences with/without
        mediaPlayer.media = this
    }.release()
    if (videoMedia.subtitlePaths.isNotEmpty() == true) {
        mediaPlayer.addSlave(
            IMedia.Slave.Type.Subtitle,
            Uri.parse(videoMedia.subtitlePaths[0]),
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

    fun togglePlay() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause() else mediaPlayer.play()
    }
    fun exit() {
        mediaPlayer.stop()
        libVLC.release()
        navController.navigate(Screen.MainMenu.route)
        navController.navigate(Screen.MainMenu.route) // This is not an typo, without it it doesnt fully exit this screen
    }

    LaunchedEffect(Unit) {
        mediaPlayer.play()
<<<<<<< HEAD
=======

        InputUtil.keyDownEvents.collect {
            if (it.action == NativeKeyEvent.ACTION_DOWN) {
                when (it.keyCode) {
                    NativeKeyEvent.KEYCODE_DPAD_CENTER -> togglePlay()
                    NativeKeyEvent.KEYCODE_DPAD_RIGHT -> mediaPlayer.position += 0.01f
                    NativeKeyEvent.KEYCODE_DPAD_LEFT -> mediaPlayer.position -= 0.01f
                    NativeKeyEvent.KEYCODE_BACK -> exit()

                    NativeKeyEvent.KEYCODE_SPACE -> togglePlay()
                    NativeKeyEvent.KEYCODE_ESCAPE -> exit()
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
fun ImagePlayer(imageMedia: com.subbyte.subcinema.models.Media, navController: NavHostController) {

    val bitmap = remember { mutableStateOf<Bitmap>(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) }

    when(imageMedia.mediaLocation) {
        EntryLocation.LOCAL -> {
            val imgFile = File(imageMedia.mediaPath.removePrefix("file://"))
            val bitmapFile = BitmapFactory.decodeFile(imgFile.absolutePath)
            bitmap.value = BitmapDrawable(LocalContext.current.resources, bitmapFile).bitmap
        }

        EntryLocation.SMB -> {
            val scope = rememberCoroutineScope()
            LaunchedEffect(Unit) {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        val smbImgFile = StorageUtil.getSmbFile(imageMedia.mediaPath)
                        if (!smbImgFile.exists()) {
                            ErrorUtil.showToast(navController.context, "Image cannot be opened")
                            return@withContext
                        }
                        val inputStream = SmbFileInputStream(smbImgFile)
                        val bytes = inputStream.readBytes()
                        withContext(Dispatchers.Main) {
                            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            if (bmp == null) {
                                ErrorUtil.showToast(navController.context, "Image cannot be opened")
                                return@withContext
                            }
                            bitmap.value = bmp
                        }
                    }
                }
            }
        }
    }

    Image(
        bitmap = bitmap.value.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier.fillMaxSize()
    )


    fun exit() {
        navController.navigate(Screen.MainMenu.route)
    }
    LaunchedEffect(Unit) {
        InputUtil.keyDownEvents.collect {
            if (it.action == NativeKeyEvent.ACTION_DOWN) {
                when (it.keyCode) {
                    NativeKeyEvent.KEYCODE_BACK -> exit()

                    NativeKeyEvent.KEYCODE_ESCAPE -> exit()
                }
            }
        }
    }
}