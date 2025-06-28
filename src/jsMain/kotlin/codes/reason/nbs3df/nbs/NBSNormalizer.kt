package codes.reason.nbs3df.nbs

import kotlin.math.roundToInt

/**
 * Normalizes the notes to 20 TPS
 * @param songTempo tempo of the NBS song
 */
fun Map<Int, List<Note>>.normalize(songTempo: Short): Map<Int, List<Note>> {
    // "The tempo of the song multiplied by 100 (for example, 1225 instead
    // of 12.25). Measured in ticks per second."
    val actualTempo = songTempo.toDouble() / 100.0
    val resampleFactor = 20.0 / actualTempo

    return this.flatMap { (tick, notes) ->
        val newTick = (tick * resampleFactor).roundToInt()
        notes.map { newTick to it }
    }.groupBy({ it.first }, { it.second })
}

val NBSFile.normalizedNotes: Map<Int, List<Note>>
    get() = this.notes.normalize(this.header.tempo)