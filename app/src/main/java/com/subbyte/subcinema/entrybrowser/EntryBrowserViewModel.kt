package com.subbyte.subcinema.entrybrowser
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.subbyte.subcinema.Screen
import com.subbyte.subcinema.models.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class EntryBrowserViewModel() : ViewModel() {

    private val _type = mutableStateOf(EntryBrowserType.LOCAL)
    val type: State<EntryBrowserType> = _type
    fun setType(type: EntryBrowserType) {
        _type.value = type
    }

    fun getRootPath() : String {
        return when(type.value) {
            EntryBrowserType.LOCAL -> {
                Environment.getExternalStorageDirectory().toString() + "/"
            }

            EntryBrowserType.SMB -> {
                ""
            }
        }
    }

    private val _entries = MutableStateFlow(listOf<Entry>())
    val entries: StateFlow<List<Entry>> = _entries
    private val _numOfEntries = MutableStateFlow(0)
    val numOfEntries: StateFlow<Int> = _numOfEntries

    fun openEntry(newPath: String, entriesPerPage: Int, navController: NavHostController) {
        val result = mutableListOf<Entry>()

        when(type.value) {
            EntryBrowserType.LOCAL -> {
                if (File(newPath).isFile) {
                    val mediaUrl = URLEncoder.encode(newPath, StandardCharsets.UTF_8.toString())
                    navController.navigate("${Screen.MediaPlayer.route}/$mediaUrl")
                    return
                }

                var index = 1

                result.add(Entry(0, -1, "..", newPath.replaceAfterLast('/', "").removeSuffix("/")))
                Log.d("subcinema", "New path: $newPath")

                val directory = File(newPath)
                val files = directory.listFiles()
                if (files != null) {
                    for (i in files.indices) {
                        result.add(Entry(index++, i, files[i].name, files[i].path))
                    }
                }
                _numOfEntries.value = result.size

                while (result.size % entriesPerPage != 0) {
                    result.add(Entry(-1, -1, "", ""))
                }
            }
            EntryBrowserType.SMB -> {

            }
        }

        _entries.value = result.toList()
    }
}