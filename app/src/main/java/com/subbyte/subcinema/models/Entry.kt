package com.subbyte.subcinema.models

data class Entry (
    var index: Int,
    val id: Int,
    val name: String,
    val path: String,
    val isFile: Boolean
)