package codes.reason.nbs3df.mod.screen.elements

import codes.reason.nbs3df.mod.screen.AvailableSize
import codes.reason.nbs3df.mod.screen.Element
import codes.reason.nbs3df.mod.screen.ElementDimension
import codes.reason.nbs3df.mod.screen.ElementToken
import codes.reason.nbs3df.mod.screen.FixedSize
import codes.reason.nbs3df.mod.screen.builder.ScopedElementBuilder
import codes.reason.nbs3df.mod.screen.layout.collapseIntoColumn
import net.minecraft.util.math.ColorHelper
import java.awt.Color

class ButtonElement(
    color: Color,
    hover: Color,
    private val content: Element
) : Element() {
    private val color: Int = ColorHelper.getArgb(
        color.alpha.toInt(),
        color.red.toInt(),
        color.green.toInt(),
        color.blue.toInt()
    )

    private val hover: Int = ColorHelper.getArgb(
        hover.alpha.toInt(),
        hover.red.toInt(),
        hover.green.toInt(),
        hover.blue.toInt()
    )

    override fun createToken(availableSize: AvailableSize): ElementToken {
        val childToken = content.createToken(availableSize)
        return ElementToken(
            width = ElementDimension(
                minimumValue = childToken.width.minimumValue + 10,
                preferredValue = childToken.width.preferredValue + 10,
                maximumValue = childToken.width.maximumValue + 10
            ),
            height = ElementDimension(
                minimumValue = childToken.height.minimumValue + 10,
                preferredValue = childToken.height.preferredValue + 10,
                maximumValue = childToken.height.maximumValue + 10
            )
        ) { ctx, size ->
            val (width, height) = size

            val hovered = ctx.mouseX in 0..width &&
                    ctx.mouseY in 0..height

            ctx.drawContext.fill(0, 0, width, height, if (hovered) hover else color)

            ctx.push()
            ctx.translate(5, 5)
            childToken.render(
                ctx, FixedSize(
                    (width - 10).coerceAtLeast(0),
                    (height - 10).coerceAtLeast(0)
                )
            )
            ctx.pop()
        }
    }
}

inline fun ScopedElementBuilder.button(
    color: Color,
    hover: Color,
    content: ScopedElementBuilder.() -> Unit
) = addNested(
    builder = ScopedElementBuilder(),
    content = content,
    buildElement = { builder ->
        ButtonElement(
            color = color,
            hover = hover,
            content = builder.elements.collapseIntoColumn()
        )
    }
)
