package codes.reason.nbs3df.mod.screen.layout

import codes.reason.nbs3df.mod.screen.*
import codes.reason.nbs3df.mod.screen.builder.ColumnElementBuilder
import codes.reason.nbs3df.mod.screen.builder.ScopedElementBuilder
import codes.reason.nbs3df.util.BoundedValue.Companion.maxOfBounded
import codes.reason.nbs3df.util.BoundedValue.Companion.sumOfBounded
import kotlin.math.abs

class ColumnElement(
    private val alignment: HorizontalAlignment = HorizontalAlignment.LEFT,
    private val elements: List<Element>,
    private val spacing: Int = 0
) : Element() {
    override fun createToken(availableSize: AvailableSize): ElementToken {
        // Initial fit pass to just figure out element sizes.
        val childrenWithTokens = elements.map {
            ElementWithToken(it, it.createToken(AvailableSize()))
        }

        val totalSpacing = spacing * (childrenWithTokens.size - 1).coerceAtLeast(0)
        val totalHeight = childrenWithTokens.sumOf { it.token.height.preferredValue } + totalSpacing
        val heightDifference = availableSize.height?.minus(totalHeight)

        // Grow and shrink the elements' width
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

        childrenWithTokens.forEach {
            val widthDifference = availableSize.width?.minus(it.token.width.preferredValue)

            if (widthDifference != null) {
                when {
                    widthDifference > 0 -> {
                        GrowUtil.growChildrenWidth(listOf(it), availableSize, widthDifference)
                    }

                    widthDifference < 0 -> {
                        ShrinkUtil.shrinkChildrenWidth(listOf(it), availableSize, abs(widthDifference))
                    }
                }
            }
        }

        return ElementToken(
            width = ElementDimension(
                minimumValue = childrenWithTokens.maxOfBounded {
                    it.token.width.minimumValue
                },
                preferredValue = childrenWithTokens.maxOf {
                    it.token.width.preferredValue
                },
                maximumValue = childrenWithTokens.maxOfBounded {
                    it.token.width.maximumValue
                }
            ),
            height = ElementDimension(
                minimumValue = childrenWithTokens.sumOfBounded {
                    it.token.height.minimumValue
                } + totalSpacing,
                preferredValue = childrenWithTokens.sumOf {
                    it.token.height.preferredValue
                } + totalSpacing,
                maximumValue = childrenWithTokens.sumOfBounded {
                    it.token.height.maximumValue
                } + totalSpacing
            )
        ) { ctx, (containerWidth, containerHeight) ->
            ctx.push()
            var remainingHeight = containerHeight
            childrenWithTokens.forEachIndexed { index, childWithToken ->
                if (index > 0) {
                    if (remainingHeight <= spacing) return@forEachIndexed
                    ctx.translate(0, spacing)
                    remainingHeight -= spacing
                }

                val coercedWidth = childWithToken.token.width.preferredValue.coerceAtMost(containerWidth)
                val coercedHeight = childWithToken.token.height.preferredValue.coerceAtMost(remainingHeight)

                if (coercedHeight <= 0) {
                    return@forEachIndexed
                }

                ctx.push()
                val offset = when (alignment) {
                    HorizontalAlignment.LEFT -> 0
                    HorizontalAlignment.RIGHT -> containerWidth - coercedWidth
                    HorizontalAlignment.CENTER -> (containerWidth - coercedWidth) / 2
                }
                ctx.translate(offset, 0)
                childWithToken.token.render(ctx, FixedSize(coercedWidth, coercedHeight))
                ctx.pop()

                ctx.translate(0, coercedHeight)
                remainingHeight -= coercedHeight
            }
            ctx.pop()
        }
    }
}

fun List<Element>.collapseIntoColumn(): Element =
    when {
        isEmpty() -> EmptyElement
        size == 1 -> this[0]
        size > 1 -> ColumnElement(elements = this)
        else -> EmptyElement
    }

inline fun ScopedElementBuilder.column(
    alignment: HorizontalAlignment = HorizontalAlignment.LEFT,
    spacing: Int = 0,
    block: ColumnElementBuilder.() -> Unit
) = this.addNested(
    builder = ColumnElementBuilder(),
    content = block,
    buildElement = {
        ColumnElement(
            alignment = alignment,
            elements = it.elements,
            spacing = spacing
        )
    }
)