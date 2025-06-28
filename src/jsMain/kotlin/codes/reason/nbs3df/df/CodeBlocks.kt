package codes.reason.nbs3df.df

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

@Serializable
data class Args(
    val items: List<SlotItem>
)

@Serializable
data class SlotItem(
    val item: CodeItem<*>,
    val slot: Int
)