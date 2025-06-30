package codes.reason.nbs3df.song.converter

import codes.reason.nbs3df.song.NBSSong
import codes.reason.nbs3df.template.CodeBlock
import codes.reason.nbs3df.template.SoundItem
import codes.reason.nbs3df.template.VariableItem
import codes.reason.nbs3df.template.createListCodeBlocks

object InstrumentConverter {
    private const val INSTRUMENTS_VARIABLE_NAME = "nbs:instruments"

    private val instrumentVariable = VariableItem(
        VariableItem.VariableData(
            name = INSTRUMENTS_VARIABLE_NAME,
            scope = VariableItem.Scope.LOCAL
        )
    )

    private val vanillaInstruments = listOf(
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

    fun convertInstruments(song: NBSSong): List<CodeBlock> {
        val instruments = buildList {
            val vanillaCount = song.header.vanillaInstruments.toInt()
            addAll(vanillaInstruments.take(vanillaCount))
            addAll(song.instruments.map {
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
}