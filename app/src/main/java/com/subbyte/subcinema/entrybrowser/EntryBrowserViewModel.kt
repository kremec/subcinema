package com.subbyte.subcinema.entrybrowser

import android.content.Context
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
        for (i in entries.indices) entries[i].index = i+1
    }


    private fun openMedia(mediaPath: String, navController: NavHostController) {
        val subtitleEntries = entries.value.filter { entry -> entry.name.endsWith(".srt") }
        val subtitlePaths: List<String> = subtitleEntries.map { if (type.value == EntryLocation.LOCAL) "file://${it.path}" else it.path }
        val mediaArg = Media(mediaPath, getEntryDirFromEntryPath(mediaPath, type.value), subtitlePaths, type.value)
        navController.navigate("${Screen.MediaPlayer.route}/${NavUtil.serializeArgument(mediaArg)}")
    }
    fun showError(context: Context) {
        ErrorUtil.showToast(context, "SMB path cannot be opened")
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
                    val directory = File(newPath)
                    val files = directory.listFiles()

                    if (files != null) {
                        for (i in files.indices) {
                            result.add(Entry(-1, i, files[i].name, files[i].path.removeSuffix("/"), files[i].isFile))
                        }
                    }
                    sortEntries(result)
                    result.add(0, Entry(0, -1, "..", newPath.replaceAfterLast('/', "").removeSuffix("/"), false))

                    _entries.value = result.toList()
                    doAfterOpen(result.toList(), previousOpenEntryPath)
                }
            }

            EntryLocation.SMB -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val smbFile = StorageUtil.getSmbFile(newPath)

                        if (!smbFile.exists()) {
                            withContext(Dispatchers.Main) {
                                showError(navController.context)
                            }
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
                            try {
                                smbFile.connect()
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    showError(navController.context)
                                }
                                return@withContext
                            }

                            val files = smbFile.listFiles()

                            if (files != null) {
                                for (i in files.indices) {
                                    result.add(Entry(-1, i, files[i].name.removeSuffix("/"), files[i].path, files[i].isFile))
                                }
                            }
                            sortEntries(result)
                            result.add(0, Entry(0, -1, "..", newPath.removeSuffix("/").replaceAfterLast('/', ""), false))

                            _entries.value = result.toList()
                            withContext(Dispatchers.Main) {
                                doAfterOpen(result.toList(), previousOpenEntryPath)
                            }
                        }
                    }
                }
            }
        }
    }
}