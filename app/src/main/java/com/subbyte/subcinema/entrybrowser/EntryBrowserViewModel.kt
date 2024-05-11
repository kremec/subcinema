package com.subbyte.subcinema.entrybrowser
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.subbyte.subcinema.Screen
import com.subbyte.subcinema.models.Entry
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.context.CIFSContextWrapper
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Properties


class EntryBrowserViewModel() : ViewModel() {

    private val _type = mutableStateOf(EntryBrowserType.LOCAL)
    val type: State<EntryBrowserType> = _type
    fun setType(type: EntryBrowserType) {
        _type.value = type
    }

    fun getRootPath() : String {
        return when(type.value) {
            EntryBrowserType.LOCAL -> {
                Environment.getExternalStorageDirectory().toString()
            }

            EntryBrowserType.SMB -> {
                "/"
            }
        }
    }

    private val _entries = MutableStateFlow(listOf<Entry>())
    val entries: StateFlow<List<Entry>> = _entries

    fun openEntry(newPath: String, navController: NavHostController) {
        val result = mutableListOf<Entry>()
        Log.d("subcinema", "New path: $newPath")

        when(type.value) {
            EntryBrowserType.LOCAL -> {
                if (File(newPath).isFile) {
                    val mediaUrl = URLEncoder.encode(newPath, StandardCharsets.UTF_8.toString())
                    navController.navigate("${Screen.MediaPlayer.route}/$mediaUrl")
                    return
                }

                var index = 1

                result.add(Entry(0, -1, "..", newPath.replaceAfterLast('/', "").removeSuffix("/")))

                val directory = File(newPath)
                val files = directory.listFiles()
                if (files != null) {
                    for (i in files.indices) {
                        result.add(Entry(index++, i, files[i].name, files[i].path))
                    }
                }

                _entries.value = result.toList()
            }
            EntryBrowserType.SMB -> {
                var files: Array<out SmbFile>?
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        jcifs.Config.registerSmbURLHandler()
                        val config = PropertyConfiguration(
                            Properties().apply {
                                setProperty("jcifs.smb.client.enableSMB2", "true")
                            }
                        )
                        val smbAuth = NtlmPasswordAuthenticator("**DOMAIN**", "**USERNAME**", "**PASSWORD**")
                        val smbFile = SmbFile("smb://${smbAuth.username}:${smbAuth.password}@${smbAuth.userDomain}/", CIFSContextWrapper(
                            BaseContext(config)
                        ))
                        if (!smbFile.exists()) {
                            Log.d("subcinema", "SMB FILE DOESN'T EXIST")
                        }
                        smbFile.connect()
                        files = smbFile.listFiles()

                        result.add(Entry(0, -1, "..", newPath.replaceAfterLast('/', "").removeSuffix("/")))
                        var index = 1

                        if (files != null) {
                            for (i in files!!.indices) {
                                Log.d("subcinema", "SMB File: ${files!![i].name}")
                                result.add(Entry(index++, i, files!![i].name, files!![i].path))
                            }
                        }

                        _entries.value = result.toList()
                    }
                }
            }
        }
    }
}