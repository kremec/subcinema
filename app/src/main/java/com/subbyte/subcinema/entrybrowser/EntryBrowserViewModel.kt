package com.subbyte.subcinema.entrybrowser
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import com.subbyte.subcinema.models.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File


class EntryBrowserViewModel : ViewModel() {

    fun getRootLocalPath() : String {
        return Environment.getExternalStorageDirectory().toString() + "/"
    }


    private val _entries = MutableStateFlow(listOf<Entry>())
    val entries: StateFlow<List<Entry>> = _entries
    private val _numOfEntries = MutableStateFlow(0)
    val numOfEntries: StateFlow<Int> = _numOfEntries

    fun openEntry(newPath: String, entriesPerPage: Int) {
        val result = mutableListOf<Entry>()

        if (File(newPath).isFile) {
            Log.d("subcinema", "Open file: $newPath")
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

        _entries.value = result.toList()
    }
}