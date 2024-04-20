package com.subbyte.subcinema.entrybrowser
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import com.subbyte.subcinema.models.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File


class EntryBrowserViewModel : ViewModel() {

    fun getRootPath() : String {
        return Environment.getExternalStorageDirectory().toString() + "/"
    }


    private val _entries = MutableStateFlow(listOf<Entry>())
    val entries: StateFlow<List<Entry>> = _entries

    fun openEntry(newPath: String) {
        val result = mutableListOf<Entry>()

        if (File(newPath).isFile) {
            Log.d("subcinema", "Open file: $newPath")
            return
        }

        result.add(Entry(-1, "..", newPath.replaceAfterLast('/', "").removeSuffix("/")))
        Log.d("subcinema", "New path: $newPath")
        val directory = File(newPath)
        val files = directory.listFiles()
        if (files != null) {
            for (i in files.indices) {
                result.add(Entry(i, files[i].name, files[i].path))
            }
        }

        _entries.value = result.toList()
    }
}