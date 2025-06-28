package codes.reason.nbs3df.nbs

import codes.reason.nbs3df.util.ByteWriter
import org.khronos.webgl.Uint8Array
import kotlin.math.pow
import kotlin.time.times

// This file name is misleading but this essentially exports NBS to a custom byte format
// designed to be as efficient as possible to read in game.

// DiamondFire has a list limit of 10,000 so we must also chunk these arrays

// This is also responsible for any tempo changes, and normalizing the song to 20tps

// List of all pitches based on the key
val keyPitches = listOf(
    0.500F,
    0.529F,
    0.561F,
    0.594F,
    0.629F,
    0.667F,
    0.707F,
    0.749F,
    0.793F,
    0.840F,
    0.890F,
    0.943F,
    1.000F,
    1.059F,
    1.122F,
    1.189F,
    1.259F,
    1.334F,
    1.414F,
    1.498F,
    1.587F,
    1.681F,
    1.781F,
    1.887F,
    2.000F
)

// Current format
//  - u16 - Delay from previous note
//  - i8  - Instrument index
//  - i8  - Velocity (0 to 100)
//          Velocity of -1 is used to signify the end
//          of the song if -1 is present do not
//          read the rest of the note.
//  - i16 - Pitch
//  - i8  - Panning (-100 to 100)

fun NBSFile.toDFData(): Uint8Array {
    val writer = ByteWriter()
    val notes = this.normalizedNotes

    // Sort the ticks so we can iterate over them
    val sortedTicks = notes.keys.sorted()
    var previousTick = 0
    for (tick in sortedTicks) {
        // Calculate the delay from the previous note.
        var delay = (tick - previousTick).toShort()
        previousTick = tick

        notes[tick]?.forEach { note ->
            val layer = this.layers[note.layer.toInt()]

            writer.writeShort(delay)
            // Write note data.
            writer.writeByte(note.instrument)
            writer.writeByte((note.velocity * layer.volume) / 100)
            writer.writeShort(calculatePitchValue(note.key, note.pitch))
            writer.writeByte((((note.panning + layer.stereo) / 2) - 100) * -1)

            // These notes all happen at the same time, so the
            // delay between them is 0
            delay = 0
        }
    }

    // End of song marker
    writer.writeShort(0) // delay
    writer.writeByte(0)   // instrument
    writer.writeByte(-1)  // key of -1 to kill the song

    // rest of the notes aren't needed

    return writer.toByteArray()
}

fun calculatePitchValue(key: Byte, finePitch: Short): Int {
    var transposedKey = key.toInt()
    while (transposedKey < 33) {
        transposedKey += 12
    }
    while (transposedKey > 57) {
        transposedKey -= 12
    }
    val clampedFinePitch = finePitch.coerceIn(-1200, 1200)

    val semitones = ((transposedKey - 33.0) + (clampedFinePitch / 100.0)).coerceIn(0.0, 24.0)
    val freqRatio = 2.0.pow(semitones / 12.0)

    return ((0.5 + ((freqRatio - 1) / 3) * 1.5) * 1000.0).toInt()
}