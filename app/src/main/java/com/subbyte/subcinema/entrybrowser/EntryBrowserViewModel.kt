package com.subbyte.subcinema.entrybrowser

import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.subbyte.subcinema.Screen
import com.subbyte.subcinema.models.Entry
import com.subbyte.subcinema.models.Media
import com.subbyte.subcinema.utils.EntryLocation
import com.subbyte.subcinema.utils.ErrorUtil
import com.subbyte.subcinema.utils.NavUtil
import com.subbyte.subcinema.utils.SettingsUtil
import com.subbyte.subcinema.utils.StorageUtil
import com.subbyte.subcinema.utils.StorageUtil.getEntryDirFromEntryPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class EntryBrowserViewModel() : ViewModel() {

    private val _type = mutableStateOf(EntryLocation.LOCAL)
    private val type: State<EntryLocation> = _type
    fun setType(type: EntryLocation) {
        _type.value = type
    }

    fun getRootPath() : String {
        return when(type.value) {
            EntryLocation.LOCAL -> {
                Environment.getExternalStorageDirectory().toString()
            }

            EntryLocation.SMB -> {
                "smb://${SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbDomain.key, SettingsUtil.EntryBrowser_SmbDomain.defaultValue)}${SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbRoot.key, SettingsUtil.EntryBrowser_SmbRoot.defaultValue)}"
            }
        }
    }

    private val _entries = MutableStateFlow(listOf<Entry>())
    val entries: StateFlow<List<Entry>> = _entries

    private fun sortEntries(entries: MutableList<Entry>) {
        entries.sortWith(compareBy({ it.isFile }, { it.name.lowercase() })) // Folders first, files second -> alphabetical
        for (i in entries.indices) entries[i].index = i
    }


    private fun openMedia(mediaPath: String, navController: NavHostController) {
        val subtitleEntries = entries.value.filter { entry -> entry.name.endsWith(".srt") }
        val subtitlePaths: List<String> = subtitleEntries.map { if (type.value == EntryLocation.LOCAL) "file://${it.path}" else it.path }
        val mediaArg = Media(mediaPath, getEntryDirFromEntryPath(mediaPath, type.value), subtitlePaths, type.value)
        navController.navigate("${Screen.MediaPlayer.route}/${NavUtil.serializeArgument(mediaArg)}")
    }
    fun openEntry(
        newEntry: Entry,
        navController: NavHostController,
        doAfterOpen: (List<Entry>, String) -> Unit,
        previousOpenEntryPath: String
    ) {
        val newPath = newEntry.path
        val result = mutableListOf<Entry>()

        when(type.value) {
            EntryLocation.LOCAL -> {
                if (File(newPath).isFile) {
                    openMedia("file://$newPath", navController)
                    doAfterOpen(result.toList(), previousOpenEntryPath)
                    return
                }
                else {
                    var index = 1
                    result.add(Entry(0, -1, "..", newPath.replaceAfterLast('/', "").removeSuffix("/"), false))

                    val directory = File(newPath)
                    val files = directory.listFiles()

                    if (files != null) {
                        for (i in files.indices) {
                            result.add(Entry(index++, i, files[i].name, files[i].path.removeSuffix("/"), files[i].isFile))
                        }
                    }
                    sortEntries(result)

                    _entries.value = result.toList()
                    doAfterOpen(result.toList(), previousOpenEntryPath)
                }
            }

            EntryLocation.SMB -> {
                var success = true
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val smbFile = StorageUtil.getSmbFile(newPath)
                        if (!smbFile.exists()) {
                            success = false
                            return@withContext
                        }

                        if (smbFile.isFile) {
                            withContext(Dispatchers.Main) {
                                openMedia(newPath, navController)
                            }
                            doAfterOpen(result.toList(), previousOpenEntryPath)
                            return@withContext
                        }
                        else  {
                            var index = 1
                            result.add(Entry(0, -1, "..", newPath.removeSuffix("/").replaceAfterLast('/', ""), false))

                            smbFile.connect()
                            val files = smbFile.listFiles()

                            if (files != null) {
                                for (i in files.indices) {
                                    result.add(Entry(index++, i, files[i].name.removeSuffix("/"), files[i].path, files[i].isFile))
                                }
                            }
                            sortEntries(result)

                            _entries.value = result.toList()
                            withContext(Dispatchers.Main) {
                                doAfterOpen(result.toList(), previousOpenEntryPath)
                            }
                        }
                    }
                }
                if (!success) ErrorUtil.showToast(navController.context, "SMB path cannot be opened")
            }
        }
    }
}