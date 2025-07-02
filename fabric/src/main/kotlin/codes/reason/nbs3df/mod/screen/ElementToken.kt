package codes.reason.nbs3df.mod.screen

data class ElementToken(
    val width: ElementDimension,
    val height: ElementDimension,
    val render: (MenuRenderContext, FixedSize) -> Unit
)

