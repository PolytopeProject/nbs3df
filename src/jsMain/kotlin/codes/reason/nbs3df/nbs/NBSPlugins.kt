package codes.reason.nbs3df.nbs

import kotlin.math.roundToInt

interface NBSPlugin {
    fun apply(file: NBSFile): NBSFile
}

class NormalizerPlugin(private val targetTempo: Double = 20.0) : NBSPlugin {
    override fun apply(file: NBSFile): NBSFile {
        val actualTempo = file.header.tempo.toDouble() / 100.0
        val resampleFactor = targetTempo / actualTempo

        val notes = buildMap<Int, MutableList<Note>> {
            file.notes.forEach { (tick, notesAtTick) ->
                val newTick = (tick * resampleFactor).roundToInt()
                getOrPut(newTick) { mutableListOf() }.addAll(notesAtTick)
            }
        }

        return file.copy(
            notes = notes,
            header = file.header.copy(tempo = 2000)
        )
    }
}

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

    private fun transposeKey(key: Int): Int =
        ((key - 33 + 12) % 25 + 25) % 25 + 33
}