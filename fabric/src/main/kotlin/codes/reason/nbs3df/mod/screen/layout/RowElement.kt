package codes.reason.nbs3df.mod.screen.layout

import codes.reason.nbs3df.mod.screen.*
import codes.reason.nbs3df.mod.screen.builder.RowElementBuilder
import codes.reason.nbs3df.mod.screen.builder.ScopedElementBuilder
import codes.reason.nbs3df.util.BoundedValue.Companion.sumOfBounded
import kotlin.math.abs

class RowElement(
    private val alignment: VerticalAlignment = VerticalAlignment.TOP,
    private val elements: List<Element>,
    private val spacing: Int = 0
) : Element() {
    override fun createToken(availableSize: AvailableSize): ElementToken {
        // Initial fit pass to just figure out element sizes.
        val childrenWithTokens = elements.map { ElementWithToken(it, it.createToken(AvailableSize())) }
        val totalSpacing = spacing * (childrenWithTokens.size - 1).coerceAtLeast(0)

        val totalWidth = childrenWithTokens.sumOf { it.token.width.preferredValue } + totalSpacing
        val widthDifference = availableSize.width?.minus(totalWidth)

        // Grow and shrink the elements' width
        if (widthDifference != null) {
            when {
                widthDifference > 0 -> {
                    GrowUtil.growChildrenWidth(childrenWithTokens, availableSize, widthDifference)
                }
                widthDifference < 0 -> {
                    ShrinkUtil.shrinkChildrenWidth(childrenWithTokens, availableSize, abs(widthDifference))
                }
            }
        }

        val maxHeight = childrenWithTokens.maxOfOrNull { it.token.height.preferredValue } ?: 0
        val heightDifference = availableSize.height?.minus(maxHeight)

        // Grow and shrink the elements' height
        if (heightDifference != null) {
            when {
                heightDifference > 0 -> {
                    GrowUtil.growChildrenHeight(childrenWithTokens, availableSize, heightDifference)
                }
                heightDifference < 0 -> {
                    ShrinkUtil.shrinkChildrenHeight(childrenWithTokens, availableSize, abs(heightDifference))
                }
            }
        }

        return ElementToken(
            width = ElementDimension(
                minimumValue = childrenWithTokens.sumOfBounded {
                    it.token.width.minimumValue
                } + totalSpacing,
                preferredValue = childrenWithTokens.sumOf {
                    it.token.width.preferredValue
                } + totalSpacing,
                maximumValue = childrenWithTokens.sumOfBounded {
                    it.token.width.maximumValue
                } + totalSpacing
            ),
            height = ElementDimension(
                minimumValue = childrenWithTokens.sumOfBounded {
                    it.token.height.minimumValue
                },
                preferredValue = childrenWithTokens.sumOf {
                    it.token.height.preferredValue
                },
                maximumValue = childrenWithTokens.sumOfBounded {
                    it.token.height.maximumValue
                }
            )
        ) { ctx, (containerWidth, containerHeight) ->
            ctx.push()
            var remainingWidth = containerWidth
            childrenWithTokens.forEachIndexed { index, childWithToken ->
                if (index > 0) {
                    if (remainingWidth <= spacing) return@forEachIndexed
                    ctx.translate(spacing, 0)
                    remainingWidth -= spacing
                }

                val coercedWidth = childWithToken.token.width.preferredValue.coerceAtMost(remainingWidth)
                val coercedHeight = childWithToken.token.height.preferredValue.coerceAtMost(containerHeight)

                if (coercedWidth <= 0) {
                    return@forEachIndexed
                }

                ctx.push()
                val offset = when (alignment) {
                    VerticalAlignment.TOP -> 0
                    VerticalAlignment.BOTTOM -> containerHeight - coercedHeight
                    VerticalAlignment.CENTER -> (containerHeight - coercedHeight) / 2
                }
                ctx.translate(0, offset)
                childWithToken.token.render(ctx, FixedSize(coercedWidth, coercedHeight))
                ctx.pop()

                ctx.translate(coercedWidth, 0)

                remainingWidth -= coercedWidth
            }
            ctx.pop()
        }
    }
}

fun List<Element>.collapseIntoRow(): Element =
    when {
        isEmpty() -> EmptyElement
        size == 1 -> this[0]
        size > 1 -> RowElement(spacing = 10, elements = this)
        else -> EmptyElement
    }

inline fun ScopedElementBuilder.row(
    alignment: VerticalAlignment = VerticalAlignment.TOP,
    spacing: Int = 0,
    block: RowElementBuilder.() -> Unit
) = this.addNested(
    builder = RowElementBuilder(),
    content = block,
    buildElement = {
        RowElement(
            alignment = alignment,
            elements = it.elements,
            spacing = spacing
        )
    }
)