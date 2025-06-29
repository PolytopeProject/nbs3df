package codes.reason.nbs3df.template

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface BlockOrBracket

@Serializable
@SerialName("block")
data class CodeBlock(
    val block: String,
    val args: Args? = null,
    val action: String? = null,
    val data: String? = null
): BlockOrBracket

fun createListCodeBlocks(variableItem: VariableItem, chunkSize: Int = 26, codeItems: List<CodeItem<*>>): List<CodeBlock> {
    return codeItems.toList().chunked(chunkSize).mapIndexed { index, chunk ->
        val slotItems = buildList {
            add(SlotItem(item = variableItem, slot = 0))
            addAll(chunk.mapIndexed { slot, item ->
                SlotItem(item = item, slot = slot + 1)
            })
        }
        CodeBlock(
            block = "set_var",
            action = if (index == 0) "CreateList" else "AppendValue",
            args = Args(slotItems)
        )
    }
}

@Serializable
data class Args(
    val items: List<SlotItem>
)

@Serializable
data class SlotItem(
    val item: CodeItem<*>,
    val slot: Int
)