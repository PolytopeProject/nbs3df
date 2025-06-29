package codes.reason.nbs3df.plugins

import codes.reason.nbs3df.NBSFile
import codes.reason.nbs3df.Note
import kotlin.math.roundToInt

class ResamplePlugin(private val targetTempo: Double = 20.0) : NBSPlugin {
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
