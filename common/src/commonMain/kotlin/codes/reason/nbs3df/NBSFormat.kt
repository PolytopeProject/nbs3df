package codes.reason.nbs3df

import codes.reason.nbs3df.util.ByteReader

data class NBSFile(
    val header: NBSHeader,
    val notes: Map<Int, List<Note>>,
    val layers: List<Layer>,
    val instruments: List<CustomInstrument>,
    val metrics: SongMetrics
) {
    companion object {
        // The documentation for the NBS format can be found at:
        //     https://noteblock.studio/nbs
        fun parse(reader: ByteReader): NBSFile {
            var version = 0.toByte()

            // In version 0 of the NBS format, the first 2 bytes encode
            // the length of the song, if the value is 0 then it is assumed
            // that the file is a versioned NBS file, in which the actual
            // version is read.
            val legacyLength = reader.readShort()
            if (legacyLength.toInt() == 0) {
                version = reader.readByte()
            }

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

            // Parse the notes of the file
            var noteCount = 0
            val notes: Map<Int, List<Note>> = buildMap {
                var currentTick = -1

                generateSequence {
                    val tickOffset = reader.readShort()
                    if (tickOffset == 0.toShort()) null
                    else tickOffset
                }.forEach {
                    currentTick += it

                    put(currentTick, buildList {
                        var currentLayer = -1

                        generateSequence {
                            val layerOffset = reader.readShort()
                            if (layerOffset == 0.toShort()) null
                            else layerOffset
                        }.forEach { layerOffset ->
                            currentLayer += layerOffset

                            add(Note(
                                instrument = reader.readByte(),
                                key = reader.readByte(),
                                velocity = if (version >= 4) reader.readByte() else 100,
                                panning = if (version >= 4) reader.readUByte() else 100,
                                pitch = if (version >= 4) reader.readShort() else 0,
                                layer = currentLayer
                            ))
                            noteCount++
                        }
                    })
                }
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

            return NBSFile(header, notes, layers, customInstruments, metrics)
        }
    }
}

data class NBSHeader(
    val version: Byte,
    val vanillaInstruments: Byte,
    val length: Short,
    val layerCount: Short,
    val meta: Metadata,
    val tempo: Short,
    val autoSaveConfig: AutoSaveConfig,
    val timeSignature: Byte,
    val stats: Statistics,
    val schematicName: String,
    val looping: Looping
)

data class AutoSaveConfig(
    val enabled: Boolean,
    val duration: Byte,
)

data class Metadata(
    val name: String,
    val author: String,
    val originalAuthor: String,
    val description: String
)

data class Statistics(
    val minutesSpent: Int,
    val leftClicks: Int,
    val rightClicks: Int,
    val noteBlocksAdded: Int,
    val noteBlocksRemoved: Int
)

data class Looping(
    val enabled: Boolean,
    val maxLoop: Byte,
    val startTick: Short
)

data class Layer(
    val name: String,
    val locked: Boolean,
    val volume: Byte,
    val stereo: Int
)

data class Note(
    val instrument: Byte,
    val key: Byte,
    val velocity: Byte,
    val panning: Int,
    val pitch: Short,
    val layer: Int
)

data class CustomInstrument(
    val name: String,
    val soundFile: String,
    val soundKey: Byte,
    val pressPianoKey: Boolean
)

data class SongMetrics(
    val noteCount: Int,
    val layerCount: Int,
    val customInstruments: Int
)