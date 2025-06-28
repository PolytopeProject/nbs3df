package codes.reason.nbs3df.nbs

import codes.reason.nbs3df.df.*
import codes.reason.nbs3df.soundMappings
import codes.reason.nbs3df.util.chunk
import codes.reason.nbs3df.util.compress
import codes.reason.nbs3df.util.toBase64

val defaultInstrumentItems = listOf(
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

fun NBSFile.generateTemplate(): CodeTemplateData {
    val chunked = this.toDFData().chunk(10_000).map {
        it.compress().toBase64()
    }.chunk(26)

    val blocks = mutableListOf(
        CodeBlock(
            block = "func",
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
            ),
            data = this.header.meta.name
        )
    )

    chunked.forEachIndexed { index, it ->
        val items = mutableListOf(
            SlotItem(
                item = VariableItem(
                    VariableItem.VariableData(
                        name = "nbs:data",
                        scope = VariableItem.Scope.LOCAL
                    )
                ),
                slot = 0
            )
        )

        it.forEachIndexed { innerIndex, dataEntry ->
            items += SlotItem(
                item = StringItem(
                    StringItem.StringData(
                        name = dataEntry
                    )
                ),
                slot = innerIndex + 1
            )
        }

        blocks += CodeBlock(
            block = "set_var",
            action = if (index == 0) "CreateList" else "AppendValue",
            args = Args(
                items = items
            )
        )
    }

    val instrumentItems = mutableListOf<SoundItem>()
    instrumentItems.addAll(defaultInstrumentItems)
    this.instruments.forEach { instrument ->
        val sound = soundMappings[instrument.name]
        val key = "minecraft:${instrument.name}"
        val variant = instrument.soundFile.substringAfterLast("/").removeSuffix(".ogg")

        instrumentItems += SoundItem(
            SoundItem.SoundData(
                pitch = 1.0f,
                vol = 2.0f,
                sound = sound,
                key = if (sound != null) null else key,
                variant = if (sound != null) variant else null
            )
        )
    }

    instrumentItems.chunk(26).forEachIndexed { index, it ->
        val items = mutableListOf(
            SlotItem(
                item = VariableItem(
                    VariableItem.VariableData(
                        name = "nbs:instruments",
                        scope = VariableItem.Scope.LOCAL
                    )
                ),
                slot = 0
            )
        )

        it.forEachIndexed { innerIndex, sound ->
            items += SlotItem(
                item = sound,
                slot = innerIndex + 1
            )
        }

        blocks += CodeBlock(
            block = "set_var",
            action = if (index == 0) "CreateList" else "AppendValue",
            args = Args(
                items = items
            )
        )
    }

    return CodeTemplateData(blocks)
}