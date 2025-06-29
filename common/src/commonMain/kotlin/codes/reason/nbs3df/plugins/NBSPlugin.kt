package codes.reason.nbs3df.plugins

import codes.reason.nbs3df.NBSFile

interface NBSPlugin {
    fun apply(file: NBSFile): NBSFile
}

fun List<NBSPlugin>.apply(file: NBSFile): NBSFile =
    fold(file) { acc, plugin -> plugin.apply(acc) }