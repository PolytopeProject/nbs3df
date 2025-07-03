package codes.reason.nbs3df.mod.screen

data class AvailableSize(
    val width: Int? = null,
    val height: Int? = null
) {
    fun subtractWidth(amount: Int) = AvailableSize(
        width = width?.minus(amount),
        height = height
    )

    fun subtractHeight(amount: Int) = AvailableSize(
        width = width,
        height = height?.minus(amount)
    )
}
