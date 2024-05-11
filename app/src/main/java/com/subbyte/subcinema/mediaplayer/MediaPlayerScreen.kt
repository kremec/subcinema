package com.subbyte.subcinema.mediaplayer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
import androidx.compose.ui.graphics.Color
<<<<<<< HEAD
>>>>>>> b1076a5 (View video information)
=======
import androidx.compose.ui.graphics.RectangleShape
>>>>>>> 1ad2f83 (VideoPlayer rework,built-in subtitle support (local subtitle files broken))
import androidx.compose.ui.graphics.asImageBitmap
<<<<<<< HEAD
=======
=======
>>>>>>> edf31f7 (Setup error alerts, SettingsScreen overhaul)
=======
import androidx.compose.ui.graphics.asImageBitmap
>>>>>>> f709910 (Fixed opening smb images, some formats unsupported (.MOV, .tif))
import androidx.compose.ui.input.key.NativeKeyEvent
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> 6e7f076 (Fixed audio issues, added simple video controls)
=======
import androidx.compose.ui.platform.LocalConfiguration
>>>>>>> b1076a5 (View video information)
=======
>>>>>>> 1ad2f83 (VideoPlayer rework,built-in subtitle support (local subtitle files broken))
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.tv.material3.Text
import com.subbyte.subcinema.Screen
import com.subbyte.subcinema.models.Subtitle
import com.subbyte.subcinema.models.SubtitleType
import com.subbyte.subcinema.utils.EntryLocation
import com.subbyte.subcinema.utils.ErrorUtil
import com.subbyte.subcinema.utils.InputUtil
import com.subbyte.subcinema.utils.NavUtil
import com.subbyte.subcinema.utils.StorageUtil
import com.subbyte.subcinema.utils.VlcUtil.externalSubtitlePath
import com.subbyte.subcinema.utils.VlcUtil.initLibVlc
import com.subbyte.subcinema.utils.VlcUtil.initMedia
import com.subbyte.subcinema.utils.VlcUtil.initMediaPlayer
import com.subbyte.subcinema.utils.VlcUtil.initVlcView
import com.subbyte.subcinema.utils.VlcUtil.internalSubtitleTrackId
import com.subbyte.subcinema.utils.VlcUtil.subtitleTracks
import com.subbyte.subcinema.utils.VlcUtil.timeFromMs
import jcifs.smb.SmbFileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer
<<<<<<< HEAD
<<<<<<< HEAD
=======
import org.videolan.libvlc.MediaPlayer.Event
import org.videolan.libvlc.interfaces.IMedia
>>>>>>> 280993d (Added subtitles (currently only choosing the first), better navigation arguments)
=======
import org.videolan.libvlc.MediaPlayer.TrackDescription
import org.videolan.libvlc.interfaces.IMedia.Meta
>>>>>>> 1ad2f83 (VideoPlayer rework,built-in subtitle support (local subtitle files broken))
import org.videolan.libvlc.util.VLCVideoLayout
import java.io.File
import java.net.URLConnection

@Composable
fun MediaPlayerScreen(navController: NavHostController, media: com.subbyte.subcinema.models.Media?) {

    if (media?.mediaPath == null) return
    subtitleTracks.clear()
    internalSubtitleTrackId.intValue = -1
    externalSubtitlePath.value = ""

    fun navigateBack() {
        when (media.mediaLocation) {
            EntryLocation.LOCAL -> {
                navController.navigate("${Screen.MainMenu.route}/${EntryLocation.LOCAL.name}/${NavUtil.serializeArgument(media.mediaDirPath)}")
            }

            EntryLocation.SMB -> {
                navController.navigate("${Screen.MainMenu.route}/${EntryLocation.SMB.name}/${NavUtil.serializeArgument(media.mediaDirPath)}")
            }
        }
    }

    val mimeType = URLConnection.guessContentTypeFromName(media.mediaPath)
    if (mimeType != null) {
        if (mimeType.startsWith("video") || mimeType.startsWith("audio")) {
            val libVlc = initLibVlc(navController.context)
            val vlcView = initVlcView(navController.context)
            val mediaPlayer = initMediaPlayer(libVlc, vlcView)
            initMedia(media, libVlc, mediaPlayer)
            VideoPlayer(libVlc, vlcView, mediaPlayer, media, ::navigateBack)
        }
        else if (mimeType.startsWith("image")) {
            ImagePlayer(media, navController, ::navigateBack)
        }
    }
}

@Composable
fun VideoPlayer(
    libVlc: LibVLC,
    vlcView: VLCVideoLayout,
    mediaPlayer: MediaPlayer,
    videoMedia: com.subbyte.subcinema.models.Media,
    navigateBack: () -> Unit
) {

    var mediaProgress by remember { mutableFloatStateOf(0F) }
    var mediaLength by remember { mutableLongStateOf(0) }

<<<<<<< HEAD
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
        setEventListener { event ->
            when (event.type) {
                Event.PositionChanged -> {
                    mediaProgress = this.position
                }
            }
        }
>>>>>>> 6e7f076 (Fixed audio issues, added simple video controls)
    }
    Media(libVLC, Uri.parse(videoMedia.mediaPath)).apply {
        // setHWDecoderEnabled(true, false) // Test differences with/without
        mediaPlayer.media = this
    }.release()
    if (videoMedia.subtitlePaths.isNotEmpty()) {
        mediaPlayer.addSlave(
            IMedia.Slave.Type.Subtitle,
            Uri.parse(videoMedia.subtitlePaths[0]),
            true
        )
    }

=======
>>>>>>> 1ad2f83 (VideoPlayer rework,built-in subtitle support (local subtitle files broken))
    var showInfo by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    fun toggleInfo() {
        mediaLength = mediaPlayer.length
        showInfo = !showInfo
    }
    fun toggleMenu() {
        showMenu = !showMenu
        if (showMenu)
            mediaPlayer.pause()
    }
    fun togglePlay() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause() else mediaPlayer.play()
    }
    fun updateMediaPlayerTime(ms: Long) {
        mediaProgress = (mediaPlayer.time.toFloat() + ms.toFloat()) / mediaPlayer.length.toFloat()
        mediaPlayer.time += ms
    }
    fun exit() {
        mediaPlayer.stop()
        libVlc.release()
        navigateBack()
        navigateBack() // This is not an typo, without it it doesn't fully exit this screen
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = {
                vlcView
            }
        )

        if (showInfo) {
            VideoInfo(
                mediaPlayer,
                videoMedia,
                mediaLength,
                mediaProgress
            )
        }
        if (showMenu) {
            VideoMenu(
                subtitleTracks.toList(),
                videoMedia.subtitlePaths,
                ::toggleMenu,
                ::exit
            )
        }
    }

    LaunchedEffect(Unit) {
        mediaPlayer.play()
<<<<<<< HEAD
=======

        InputUtil.keyDownEvents.collect {
            if (it.action == NativeKeyEvent.ACTION_DOWN && !showMenu) {
                when (it.keyCode) {
                    NativeKeyEvent.KEYCODE_DPAD_CENTER -> toggleInfo()
                    NativeKeyEvent.KEYCODE_MENU -> toggleMenu()
                    NativeKeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> togglePlay()
                    NativeKeyEvent.KEYCODE_DPAD_RIGHT -> updateMediaPlayerTime(30000)
                    NativeKeyEvent.KEYCODE_DPAD_LEFT -> updateMediaPlayerTime(-30000)
                    NativeKeyEvent.KEYCODE_DPAD_UP -> updateMediaPlayerTime(180000)
                    NativeKeyEvent.KEYCODE_DPAD_DOWN -> updateMediaPlayerTime(-180000)
                    NativeKeyEvent.KEYCODE_BACK -> exit()

                    NativeKeyEvent.KEYCODE_I -> toggleInfo()
                    NativeKeyEvent.KEYCODE_M -> toggleMenu()
                    NativeKeyEvent.KEYCODE_SPACE -> togglePlay()
                    NativeKeyEvent.KEYCODE_ESCAPE -> exit()
                }
            }
        }
>>>>>>> 6e7f076 (Fixed audio issues, added simple video controls)
    }
    DisposableEffect(Unit) {
        onDispose {
            libVlc.release()
        }
    }
}
<<<<<<< HEAD
<<<<<<< HEAD
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
=======
>>>>>>> b1076a5 (View video information)
=======
@Composable
fun VideoInfo(
    mediaPlayer: MediaPlayer,
    videoMedia: com.subbyte.subcinema.models.Media,
    mediaLength: Long,
    mediaProgress: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val mediaTitle = mediaPlayer.media?.getMeta(Meta.Title) ?: ""
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.25f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = mediaTitle,
                    color = Color.White,
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val elapsedMs = (mediaLength * mediaProgress).toLong()
                Text(
                    text = "${timeFromMs(elapsedMs)} / ${timeFromMs(mediaLength)}",
                    color = Color.White,
                )
                LinearProgressIndicator(
                    progress = { mediaProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = videoMedia.mediaPath.substringAfterLast("/"),
                    color = Color.White,
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.25f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TEST2",
                    color = Color.White,
                )
            }
        }
    }
}
@Composable
fun VideoMenu(
    internalSubtitleTracks: List<TrackDescription>,
    externalSubtitlePaths: List<String>,
    toggleMenu: () -> Unit,
    exit: () -> Unit
) {
    val subtitleList = mutableListOf<Subtitle>()
    for (internalSubtitle in internalSubtitleTracks) {
        subtitleList.add(Subtitle(SubtitleType.INTERNAL, internalSubtitle.id, internalSubtitle.name, ""))
    }
    for (externalSubtitle in externalSubtitlePaths) {
        subtitleList.add(Subtitle(SubtitleType.EXTERNAL, -1, "", externalSubtitle))
    }
>>>>>>> 1ad2f83 (VideoPlayer rework,built-in subtitle support (local subtitle files broken))

    var selectedSubtitle by remember { mutableIntStateOf(if (subtitleList.isNotEmpty()) 0 else -1) }

    fun saveSubtitle() {
        val subtitle = subtitleList[selectedSubtitle]
        externalSubtitlePath.value = subtitle.externalPath
        internalSubtitleTrackId.intValue = subtitle.internalId
    }

    if (selectedSubtitle != -1) saveSubtitle()

    for(i in subtitleList.indices) {
        val subtitle = subtitleList[i]
        when (subtitle.type) {
            SubtitleType.INTERNAL -> {
                if (internalSubtitleTrackId.intValue == subtitle.internalId) {
                    selectedSubtitle = i
                    break
                }
            }
            SubtitleType.EXTERNAL -> {
                if (externalSubtitlePath.value == subtitle.externalPath) {
                    selectedSubtitle = i
                    break
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
    ) {
        TextButton(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RectangleShape,
            onClick = {},
            border = BorderStroke(1.dp, Color.White)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text =
                    if (selectedSubtitle != -1) {
                        val subtitle = subtitleList[selectedSubtitle]
                        when (subtitle.type) {
                            SubtitleType.INTERNAL -> subtitle.internalName
                            SubtitleType.EXTERNAL -> subtitle.externalPath.substringAfterLast("/")
                        }
                    }
                    else "",
                textAlign = TextAlign.Left
            )
        }
    }

    LaunchedEffect(Unit) {
        InputUtil.keyDownEvents.collect {
            if (it.action == NativeKeyEvent.ACTION_DOWN) {
                when (it.keyCode) {
                    NativeKeyEvent.KEYCODE_MENU -> toggleMenu()
                    NativeKeyEvent.KEYCODE_BACK -> exit()

                    NativeKeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (selectedSubtitle != -1) {
                            selectedSubtitle++
                            if (selectedSubtitle == subtitleList.size) selectedSubtitle = 0
                            println(selectedSubtitle)
                        }
                    }

                    NativeKeyEvent.KEYCODE_DPAD_LEFT -> {
                        if (selectedSubtitle != -1) {
                            selectedSubtitle--
                            if (selectedSubtitle == -1) selectedSubtitle = subtitleList.size-1
                            println(selectedSubtitle)
                        }
                    }

                    NativeKeyEvent.KEYCODE_M -> toggleMenu()
                    NativeKeyEvent.KEYCODE_ESCAPE -> exit()
                }
            }
        }
    }
}

@Composable
fun ImagePlayer(
    imageMedia: com.subbyte.subcinema.models.Media,
    navController: NavHostController,
    navigateBack: () -> Unit
) {

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
        navigateBack()
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