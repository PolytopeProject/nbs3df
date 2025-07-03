package codes.reason.nbs3df.mod.screen.elements

import codes.reason.nbs3df.mod.screen.AvailableSize
import codes.reason.nbs3df.mod.screen.Element
import codes.reason.nbs3df.mod.screen.ElementDimension
import codes.reason.nbs3df.mod.screen.ElementToken
import codes.reason.nbs3df.mod.screen.FixedSize
import codes.reason.nbs3df.mod.screen.builder.ScopedElementBuilder
import codes.reason.nbs3df.mod.screen.layout.collapseIntoColumn

class PaddingElement(
    private val leftPadding: Int,
    private val topPadding: Int,
    private val rightPadding: Int,
    private val bottomPadding: Int,
    private val child: Element
) : Element() {
    override fun createToken(availableSize: AvailableSize): ElementToken {
        val childToken = child.createToken(availableSize
            .subtractWidth(leftPadding + rightPadding)
            .subtractHeight(topPadding + bottomPadding))
        return ElementToken(
            width = ElementDimension(
                minimumValue = childToken.width.minimumValue + leftPadding + rightPadding,
                preferredValue = childToken.width.preferredValue + leftPadding + rightPadding,
                maximumValue = childToken.width.maximumValue + leftPadding + rightPadding
            ),
            height = ElementDimension(
                minimumValue = childToken.height.minimumValue + topPadding + bottomPadding,
                preferredValue = childToken.height.preferredValue + topPadding + bottomPadding,
                maximumValue = childToken.height.maximumValue + topPadding + bottomPadding
            )
        ) { ctx, size ->
            val (width, height) = size
            ctx.push()
            ctx.translate(leftPadding, topPadding)
            childToken.render(
                ctx, FixedSize(
                    (width - leftPadding - rightPadding).coerceAtLeast(0),
                    (height - topPadding - bottomPadding).coerceAtLeast(0)
                )
            )
            ctx.pop()
        }
    }
}

inline fun ScopedElementBuilder.padding(
    padding: Int,
    content: ScopedElementBuilder.() -> Unit
) = this.addNested(
    builder = ScopedElementBuilder(),
    content = content,
    buildElement = {
        PaddingElement(
            leftPadding = padding,
            topPadding = padding,
            rightPadding = padding,
            bottomPadding = padding,
            it.elements.collapseIntoColumn()
        )
    }
)

inline fun ScopedElementBuilder.padding(
    horizontalPadding: Int,
    verticalPadding: Int,
    content: ScopedElementBuilder.() -> Unit
) = this.addNested(
    builder = ScopedElementBuilder(),
    content = content,
    buildElement = {
        PaddingElement(
            leftPadding = horizontalPadding,
            topPadding = verticalPadding,
            rightPadding = horizontalPadding,
            bottomPadding = verticalPadding,
            child = it.elements.collapseIntoColumn()
        )
    }
)

inline fun ScopedElementBuilder.padding(
    leftPadding: Int,
    topPadding: Int,
    rightPadding: Int,
    bottomPadding: Int,
    content: ScopedElementBuilder.() -> Unit
) = this.addNested(
    builder = ScopedElementBuilder(),
    content = content,
    buildElement = {
        PaddingElement(
            leftPadding = leftPadding,
            topPadding = topPadding,
            rightPadding = rightPadding,
            bottomPadding = bottomPadding,
            child = it.elements.collapseIntoColumn()
        )
    }
)