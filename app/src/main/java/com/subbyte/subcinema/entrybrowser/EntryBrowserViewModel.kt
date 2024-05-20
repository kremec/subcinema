package com.subbyte.subcinema.entrybrowser

import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.subbyte.subcinema.Screen
import com.subbyte.subcinema.models.Entry
import com.subbyte.subcinema.utils.EntryLocation
import com.subbyte.subcinema.utils.ErrorUtil
import com.subbyte.subcinema.utils.NavUtil
import com.subbyte.subcinema.utils.SettingsUtil
import com.subbyte.subcinema.utils.StorageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLConnection


class EntryBrowserViewModel : ViewModel() {
    val SMBOPENERROR = "SMB path cannot be opened"


    private val _type = mutableStateOf(EntryLocation.LOCAL)
    private val type: State<EntryLocation> = _type
    fun setType(type: EntryLocation) {
        _type.value = type
    }

    private val _entries = MutableStateFlow(listOf<Entry>())
    val entries: StateFlow<List<Entry>> = _entries
    private val subtitlePaths = mutableListOf<String>()

    fun getRootPath() : String {
        return when(type.value) {
            EntryLocation.LOCAL -> {
                Environment.getExternalStorageDirectory().toString()
            }
            EntryLocation.SMB -> {
                val smbDomain = SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbDomain.key, SettingsUtil.EntryBrowser_SmbDomain.defaultValue)
                val smbRoot = SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbRoot.key, SettingsUtil.EntryBrowser_SmbRoot.defaultValue)
                "smb://$smbDomain$smbRoot"
            }
        }
    }

    private fun isSupportedFileType(fileType: String) : Boolean {
        return fileType.startsWith("video") || fileType.startsWith("audio") || fileType.startsWith("image")
    }
    private fun sortEntries(entries: MutableList<Entry>) {
        entries.sortWith(compareBy({ it.isFile }, { it.name.lowercase() })) // Folders first, files second -> alphabetical
        for (i in entries.indices) entries[i].index = i+1
    }
    private fun openFile(entry: Entry, navController: NavHostController) {
        entry.subtitlePaths = subtitlePaths.toList()
        navController.navigate("${Screen.MediaPlayer.route}/${NavUtil.serializeArgument(entry)}")
    }
    fun openEntry(
        entry: Entry,
        navController: NavHostController,
        doAfterOpen: (List<Entry>, String) -> Unit,
        previousOpenEntryPath: String
    ) {
        val result = mutableListOf<Entry>()

        when(type.value) {
            EntryLocation.LOCAL -> {
                if (entry.isFile) {
                    entry.path = "file://${entry.path}"
                    openFile(entry, navController)
                    doAfterOpen(result.toList(), previousOpenEntryPath)
                    return
                }
                else {
                    subtitlePaths.clear()
                    val directory = File(entry.path)
                    val files = directory.listFiles()

                    if (files != null) {
                        for (i in files.indices) {
                            val fileType = URLConnection.guessContentTypeFromName(files[i].path.removeSuffix("/")) ?: ""
                            if (files[i].isDirectory || isSupportedFileType(fileType))
                                result.add(
                                    Entry(
                                        name = files[i].name,
                                        isFile = files[i].isFile,
                                        path = files[i].path.removeSuffix("/"),
                                        location = EntryLocation.LOCAL,
                                        type = if (files[i].isFile) URLConnection.guessContentTypeFromName(files[i].path.removeSuffix("/")) else ""
                                    )
                                )
                            else if (files[i].name.endsWith(".srt"))
                                subtitlePaths.add("file://${files[i].path}")
                        }
                    }
                    sortEntries(result)
                    result.add(0,
                        Entry(
                            index = 0,
                            name = "..",
                            path = entry.path.replaceAfterLast('/', "").removeSuffix("/"),
                            location = EntryLocation.LOCAL
                        )
                    )

                    _entries.value = result.toList()
                    doAfterOpen(result.toList(), previousOpenEntryPath)
                }
            }

            EntryLocation.SMB -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val smbFile = StorageUtil.getSmbFile(entry.path)

                        if (!smbFile.exists()) {
                            withContext(Dispatchers.Main) {
                                ErrorUtil.showToast(navController.context, SMBOPENERROR)
                            }
                            return@withContext
                        }
                        if (smbFile.isFile) {
                            withContext(Dispatchers.Main) {
                                openFile(entry, navController)
                            }
                            doAfterOpen(result.toList(), previousOpenEntryPath)
                            return@withContext
                        }
                        else  {
                            subtitlePaths.clear()
                            try {
                                smbFile.connect()
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    ErrorUtil.showToast(navController.context, SMBOPENERROR)
                                }
                                return@withContext
                            }

                            val files = smbFile.listFiles()

                            if (files != null) {
                                for (i in files.indices) {
                                    val fileType = URLConnection.guessContentTypeFromName(files[i].path.removeSuffix("/")) ?: ""
                                    if (files[i].isDirectory || isSupportedFileType(fileType))
                                        result.add(
                                            Entry(
                                                name = files[i].name.removeSuffix("/"),
                                                isFile = files[i].isFile,
                                                path = files[i].path,
                                                location = EntryLocation.SMB,
                                                type = if (files[i].isFile) URLConnection.guessContentTypeFromName(files[i].path.removeSuffix("/")) else ""
                                            )
                                        )
                                    else if (files[i].name.endsWith(".srt"))
                                        subtitlePaths.add(files[i].path)
                                }
                            }
                            sortEntries(result)
                            result.add(0,
                                Entry(
                                    index = 0,
                                    name = "..",
                                    path = entry.path.removeSuffix("/").replaceAfterLast('/', ""),
                                    location = EntryLocation.SMB
                                )
                            )

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