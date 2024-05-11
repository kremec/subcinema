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
                "smb://${SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbDomain.key, SettingsUtil.EntryBrowser_SmbDomain.defaultValue)}${SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbRoot.key, SettingsUtil.EntryBrowser_SmbRoot.defaultValue)}"
            }
        }
    }

    private val _entries = MutableStateFlow(listOf<Entry>())
    val entries: StateFlow<List<Entry>> = _entries

    private fun openMedia(mediaPath: String, navController: NavHostController) {
        val subtitleEntries = entries.value.filter { entry -> entry.name.endsWith(".srt") }
        val subtitlePaths: List<String> = subtitleEntries.map { it.path }
        val mediaArg = Media(mediaPath, subtitlePaths)
        navController.navigate("${Screen.MediaPlayer.route}/${NavUtil.serializeArgument(mediaArg)}")
    }
    fun openEntry(newPath: String, navController: NavHostController) {
        val result = mutableListOf<Entry>()

        when(type.value) {
            EntryBrowserType.LOCAL -> {
                if (File(newPath).isFile) {
                    openMedia("file://$newPath", navController)
                    return
                }
                else {
                    var index = 1

                    result.add(Entry(0, -1, "..", newPath.replaceAfterLast('/', "").removeSuffix("/")))

                    val directory = File(newPath)
                    val files = directory.listFiles()
                    if (files != null) {
                        for (i in files.indices) {
                            result.add(Entry(index++, i, files[i].name, files[i].path.removeSuffix("/")))
                        }
                    }

                    _entries.value = result.toList()
                }
            }

            EntryBrowserType.SMB -> {
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
                            return@withContext
                        }
                        else  {
                            smbFile.connect()
                            val files = smbFile.listFiles()

                            result.add(Entry(0, -1, "..", newPath.removeSuffix("/").replaceAfterLast('/', "")))
                            var index = 1

                            if (files != null) {
                                for (i in files.indices) {
                                    result.add(Entry(index++, i, files[i].name.removeSuffix("/"), files[i].path))
                                }
                            }

                            _entries.value = result.toList()
                        }
                    }
                }
                if (!success) ErrorUtil.showToast(navController.context, "SMB path cannot be opened")
            }
        }
    }
}