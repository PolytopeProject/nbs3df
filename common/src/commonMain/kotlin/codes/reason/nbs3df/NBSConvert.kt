package codes.reason.nbs3df

import codes.reason.nbs3df.plugins.ResamplePlugin
import codes.reason.nbs3df.plugins.TransposeNotesPlugin
import codes.reason.nbs3df.plugins.apply
import codes.reason.nbs3df.template.*
import codes.reason.nbs3df.util.ByteWriter
import codes.reason.nbs3df.util.chunked
import codes.reason.nbs3df.util.compress
import codes.reason.nbs3df.util.toBase64
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.pow

// The default instrument items stored in minecraft.
val vanillaInstruments = listOf(
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Harp")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Bass")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Bass Drum")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Snare Drum")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Hat")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Guitar")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Flute")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Bell")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Chime")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Xylophone")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Iron Xylophone")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Cow Bell")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Didgeridoo")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Bit")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Banjo")),
    SoundItem(SoundItem.SoundData(1.0f, 2.0f, sound = "Pling"))
)

fun NBSFile.convert(): List<CodeTemplateData> {
    val fileToConvert = listOf(
        ResamplePlugin(20.0),
        TransposeNotesPlugin
    ).apply(this)

    val lineStarter = createLineStarter(fileToConvert)
    val (chunks, data) = createDataList(fileToConvert)
    val instruments = createInstrumentList(fileToConvert)
    val metadata = createMetaDict(
        ConvertedMetadata(
            chunks = chunks,
            notes = fileToConvert.notes.values.sumOf { it.size }
        )
    )

    return data.mapIndexed { index, currentBlock ->
        val blocks = buildList {
            if (index == 0) add(lineStarter)
            add(currentBlock)
            if (index == data.lastIndex) {
                addAll(instruments)
                add(metadata)
            }
        }
        CodeTemplateData(blocks)
    }
}

@Serializable
data class ConvertedMetadata(
    val chunks: Int,
    val notes: Int
)

private fun createLineStarter(file: NBSFile): CodeBlock {
    return CodeBlock(
        block = "func",
        data = file.header.meta.name,
        args = Args(
            items = listOf(
                SlotItem(
                    item = BlockTagItem(
                        BlockTagItem.BlockTagData(
                            option = "False",
                            tag = "Is Hidden",
                            action = "dynamic",
                            block = "func"
                        )
                    ),
                    slot = 26
                )
            )
        )
    )
}

private fun createDataList(file: NBSFile): Pair<Int, List<CodeBlock>> {
    val dataVariable = VariableItem(
        VariableItem.VariableData(
            name = "nbs:data",
            scope = VariableItem.Scope.LOCAL
        )
    )
    val chunks = createByteData(file)
        .chunked(10_000)
        .map { chunk ->
            StringItem(
                StringItem.StringData(
                    name = chunk.compress().toBase64()
                )
            )
        }
    return Pair(
        chunks.size,
        createListCodeBlocks(
            variableItem = dataVariable,
            chunkSize = 17,
            codeItems = chunks
        )
    )
}

private fun createInstrumentList(file: NBSFile): List<CodeBlock> {
    val instrumentVariable = VariableItem(
        VariableItem.VariableData(
            name = "nbs:instruments",
            scope = VariableItem.Scope.LOCAL
        )
    )
    val instruments = buildList {
        val vanillaCount = file.header.vanillaInstruments.toInt()
        addAll(vanillaInstruments.take(vanillaCount))
        addAll(file.instruments.map {
            val key = "minecraft:${it.name}"
            SoundItem(
                SoundItem.SoundData(
                    pitch = 1.0f,
                    vol = 2.0f,
                    key = key
                )
            )
        })
    }
    return createListCodeBlocks(
        variableItem = instrumentVariable,
        codeItems = instruments
    )
}

private fun createMetaDict(meta: ConvertedMetadata): CodeBlock {
    val metaVariable = VariableItem(
        VariableItem.VariableData(
            name = "nbs:meta",
            scope = VariableItem.Scope.LOCAL
        )
    )
    val jsonItem = StringItem(
        StringItem.StringData(
            name = Json.encodeToString(meta)
        )
    )
    return CodeBlock(
        block = "set_var",
        action = "JsonFromValue",
        args = Args(
            items = listOf(
                SlotItem(item = metaVariable, slot = 0),
                SlotItem(item = jsonItem, slot = 1)
            )
        )
    )
}

/*
    Byte data format v1:
      unsigned short - Delay from previous note
       unsigned byte - Instrument
         signed byte - Volume
      unsigned short - Pitch
         signed byte - Panning (-100 to 100)
 */
private fun createByteData(file: NBSFile): ByteArray {
    val writer = ByteWriter()

    var previousTick = 0
    file.notes.keys.sorted().forEach { tick ->
        val initialDelay = (tick - previousTick).toShort()
        previousTick = tick

        file.notes[tick].orEmpty().forEachIndexed { index, note ->
            val layer = file.layers[note.layer]
            val delay = if (index == 0) initialDelay else 0.toShort()

            writer.writeShort(delay)
            writer.writeByte(note.instrument)
            writer.writeByte((note.velocity * layer.volume) / 100)
            // Calculate the custom instrument offset
            if (note.instrument >= file.header.vanillaInstruments) {
                val instrumentKey = file.instruments[note.instrument - file.header.vanillaInstruments].soundKey

                writer.writeShort(calculatePitchValue(note.key + instrumentKey - 45, note.pitch))
            } else {
                writer.writeShort(calculatePitchValue(note.key.toInt(), note.pitch))
            }
            writer.writeByte((((note.panning + layer.stereo) / 2) - 100) * -1)
        }
    }

    return writer.toByteArray()
}

private fun calculatePitchValue(key: Int, finePitch: Short): Int {
    val coercedKey = key.coerceIn(33, 57)
    val clampedFinePitch = finePitch.coerceIn(-1200, 1200)

    val semitones = ((coercedKey - 33.0) + (clampedFinePitch / 100.0)).coerceIn(0.0, 24.0)
    val freqRatio = 2.0.pow(semitones / 12.0)

    return ((0.5 + ((freqRatio - 1) / 3) * 1.5) * 1000.0).toInt()
}