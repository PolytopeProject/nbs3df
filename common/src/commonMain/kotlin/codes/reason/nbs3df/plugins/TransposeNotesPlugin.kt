package codes.reason.nbs3df.plugins

import codes.reason.nbs3df.NBSFile

object TransposeNotesPlugin : NBSPlugin {
    override fun apply(file: NBSFile): NBSFile {
        val transposedNotes = file.notes.mapValues { (_, noteList) ->
            noteList.map { note ->
                val transposedKey = transposeKey(note.key.toInt())
                note.copy(key = transposedKey.toByte())
            }
        }
        return file.copy(notes = transposedNotes)
    }

    private fun transposeKey(key: Int): Int {
        var transposedKey = key
        while (transposedKey < 33) {
            transposedKey += 12
        }
        while (transposedKey > 57) {
            transposedKey -= 12
        }
        return transposedKey
    }
}
