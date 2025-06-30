package codes.reason.nbs3df.song.converter

import codes.reason.nbs3df.song.NBSSong
import codes.reason.nbs3df.song.plugin.PluginChain
import codes.reason.nbs3df.song.plugin.ResamplePlugin
import codes.reason.nbs3df.song.plugin.TransposeNotesPlugin
import codes.reason.nbs3df.song.plugin.apply
import codes.reason.nbs3df.template.CodeTemplateData
import codes.reason.nbs3df.util.chunked

object SongConverter {
    private const val MAJOR_VERSION = 1
    private const val BYTES_PER_CHUNK = 10_000
    private const val TARGET_TPS = 20.0
    private const val JUMPS_PER_NOTE = 7

    private val pluginsToApply: PluginChain = listOf(
        ResamplePlugin(targetTempo = TARGET_TPS),
        TransposeNotesPlugin
    )

    fun convertSong(song: NBSSong): List<CodeTemplateData> {
        val modifiedSong = pluginsToApply.apply(song)
        val chunks = SongByteEncoder.encodeSongBytes(modifiedSong)
            .chunked(BYTES_PER_CHUNK)

        val function = LineStarterGenerator.createLineStarter(modifiedSong)
        val data = SongDataConverter.convertNotes(chunks)
        val instruments = InstrumentConverter.convertInstruments(modifiedSong)

        val metadata = SongPlayerMetadata(
            chunks = chunks.size,
            notes = modifiedSong.notes.values.sumOf { it.size },
            bytesPerNote = JUMPS_PER_NOTE,
            majorVersion = MAJOR_VERSION
        )

        val convertedMetadata = MetadataConverter.convertMetadata(metadata)

        return data.mapIndexed { index, currentBlock ->
            val blocks = buildList {
                if (index == 0) add(function)
                add(currentBlock)
                if (index == data.lastIndex) {
                    addAll(instruments)
                    add(convertedMetadata)
                }
            }
            CodeTemplateData(blocks)
        }
    }
}