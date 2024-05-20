package com.subbyte.subcinema.utils

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.util.VLCVideoLayout

object VlcUtil {
    fun timeFromMs(ms: Long): String {
        val hours = (ms / 3600000)
        val minutes = ((ms - hours*3600000) / 60000)
        val seconds = ((ms - hours*3600000 - minutes*60000) / 1000)
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }
    val subtitleTracks = mutableListOf<MediaPlayer.TrackDescription>()
    val internalSubtitleTrackId = mutableIntStateOf(-1)
    val externalSubtitlePath = mutableStateOf("")

    fun initLibVlc(context: Context): LibVLC {
        return LibVLC(context, ArrayList<String>().apply {
            add("--file-caching=6000")
            add("--network-caching=6000")
            add("--live-caching=6000")
            add("--disc-caching=6000")
            add("--sout-mux-caching=2000")
            add("--drop-late-frames")
            add("--skip-frames")
            add("--clock-jitter=0")

            add("--vout=android-display")
            add("--aout=android") // Previously "audiotrack", "opensles" -> test difference
            add("--subsdec-encoding=Windows-1250") // Other encodings: https://github.com/videolan/vlc/blob/master/modules/codec/subsdec.c
            add("-vv")
            add("--http-reconnect")
            add("--access=smb")
            add("--smb-domain=${SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbDomain.key, SettingsUtil.EntryBrowser_SmbDomain.defaultValue)}")
            add("--smb-user=${SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbUsername.key, SettingsUtil.EntryBrowser_SmbDomain.defaultValue)}")
            add("--smb-pwd=${SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbPassword.key, SettingsUtil.EntryBrowser_SmbPassword.defaultValue)}")
        })
    }
    fun initVlcView(context: Context): VLCVideoLayout {
        return VLCVideoLayout(context).apply {
            keepScreenOn = true
            fitsSystemWindows = false
            visibility = View.VISIBLE
        }
    }
    fun initMediaPlayer(
        libVlc: LibVLC,
        vlcView: VLCVideoLayout,
        setMediaProgress: (Float) -> Unit,
        hideLoadingCircle: () -> Unit
    ): MediaPlayer {
        return MediaPlayer(libVlc).apply {
            attachViews(vlcView, null, true, false)
            setEventListener { event ->
                when (event.type) {
                    MediaPlayer.Event.PositionChanged -> {
                        setMediaProgress(position)
                    }
                    MediaPlayer.Event.Playing -> {
                        setSubtitles(this)
                        hideLoadingCircle()
                    }
                }
            }
        }
    }
    fun initMedia(
        videoEntry: com.subbyte.subcinema.models.Entry,
        libVlc: LibVLC,
        mediaPlayer: MediaPlayer
    ) {
        Media(libVlc, Uri.parse(videoEntry.path)).apply {
            parse()
            setEventListener {
                when (it.type) {
                    IMedia.Event.ParsedChanged -> {
                        if (mediaPlayer.spuTracks != null) {
                            for (track in mediaPlayer.spuTracks)
                                subtitleTracks.add(track)
                        }
                    }
                }
            }
            // setHWDecoderEnabled(true, false) // Test differences with/without
            mediaPlayer.media = this
        }.release()
    }

    fun setSubtitles(mediaPlayer: MediaPlayer) {
        if (externalSubtitlePath.value != "") {
            internalSubtitleTrackId.intValue = -1
            mediaPlayer.media?.clearSlaves()
            mediaPlayer.addSlave(
                IMedia.Slave.Type.Subtitle,
                Uri.parse(externalSubtitlePath.value),
                true
            )
        }
        else {
            mediaPlayer.setSpuTrack(internalSubtitleTrackId.intValue)
        }
    }
}