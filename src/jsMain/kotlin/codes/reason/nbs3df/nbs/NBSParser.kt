package codes.reason.nbs3df.nbs

import codes.reason.nbs3df.util.asByteReader
import org.w3c.files.File
import kotlin.collections.List

suspend fun File.parseAsNBS(): NBSFile {
    val reader = this.asByteReader()

    // In NBS v0 the length of the song is the first short
    // in NBS v1 or higher this is 0 and the version is present
    // after wards.
    var version = 0.toByte()
    val legacyLength = reader.readShort()
    if (legacyLength.toInt() == 0) {
        version = reader.readByte()
    }

    println("Version $version NBS file provided.")

    // Parse the header of the file.
    val header = NBSHeader(
        version = version,
        vanillaInstruments = if (version > 0) reader.readByte() else 9,
        length = if (version >= 3) reader.readShort() else legacyLength,
        layerCount = reader.readShort(),
        meta = Metadata(
            name = reader.readString(),
            author = reader.readString(),
            originalAuthor = reader.readString(),
            description = reader.readString()
        ),
        tempo = reader.readShort(),
        autoSaveConfig = AutoSaveConfig(
            enabled = reader.readBoolean(),
            duration = reader.readByte()
        ),
        timeSignature = reader.readByte(),
        stats = Statistics(
            minutesSpent = reader.readInt(),
            leftClicks = reader.readInt(),
            rightClicks = reader.readInt(),
            noteBlocksAdded = reader.readInt(),
            noteBlocksRemoved = reader.readInt()
        ),
        schematicName = reader.readString(),
        looping = if (version >= 4) {
            Looping(
                enabled = reader.readBoolean(),
                maxLoop = reader.readByte(),
                startTick = reader.readShort()
            )
        } else {
            Looping(false, 0, 0)
        }
    )

    // Parse every note in the NBS file
    val noteMap: MutableMap<Int, List<Note>> = mutableMapOf()
    var currentTick = -1
    var noteCount = 0
    while (true) {
        val tickOffset = reader.readShort()
        if (tickOffset == 0.toShort()) {
            break
        }
        currentTick += tickOffset

        val notes = mutableListOf<Note>()
        var currentLayer: Short = -1
        while (true) {
            val layerOffset = reader.readShort()
            if (layerOffset == 0.toShort()) {
                break
            }
            currentLayer = (currentLayer + layerOffset).toShort()

            notes += Note(
                instrument = reader.readByte(),
                key = reader.readByte(),
                velocity = if (version >= 4) reader.readByte() else 100,
                panning = if (version >= 4) reader.readUByte() else 100,
                pitch = if (version >= 4) reader.readShort() else 0,
                layer = currentLayer
            )
            noteCount++
        }
        noteMap[currentTick] = notes.toList()
    }

    // Parse layers
    val layers = List(header.layerCount.toInt()) {
        Layer(
            name = reader.readString(),
            locked = if (version >= 4) reader.readBoolean() else false,
            volume = reader.readByte(),
            stereo = if (version >= 2) reader.readUByte() else 100
        )
    }

    // Parse custom instruments
    val customInstruments  = List(reader.readUByte()) {
        CustomInstrument(
            name = reader.readString(),
            soundFile = reader.readString(),
            soundKey = reader.readByte(),
            pressPianoKey = reader.readBoolean()
        ).also { println(it) }
    }

    // Metrics
    val metrics = SongMetrics(
        noteCount = noteCount,
        layerCount = layers.size,
        customInstruments = customInstruments.size
    )

    return NBSFile(header, noteMap.toMap(), layers, customInstruments, metrics)
}