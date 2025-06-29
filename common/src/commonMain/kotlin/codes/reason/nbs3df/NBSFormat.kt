package codes.reason.nbs3df

data class NBSFile(
    val header: NBSHeader,
    val notes: Map<Int, List<Note>>,
    val layers: List<Layer>,
    val instruments: List<CustomInstrument>,
    val metrics: SongMetrics
)

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