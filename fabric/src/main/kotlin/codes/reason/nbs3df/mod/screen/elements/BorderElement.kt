package codes.reason.nbs3df.mod.screen.elements

import codes.reason.nbs3df.mod.screen.AvailableSize
import codes.reason.nbs3df.mod.screen.Element
import codes.reason.nbs3df.mod.screen.ElementToken
import codes.reason.nbs3df.mod.screen.builder.ScopedElementBuilder
import codes.reason.nbs3df.mod.screen.elements.BorderElement.Shadow
import codes.reason.nbs3df.mod.screen.layout.collapseIntoColumn
import net.minecraft.util.math.ColorHelper
import java.awt.Color

class BorderElement(
    color: Color,
    private val shadow: Shadow? = null,
    private val child: Element
) : Element() {

    private val borderColor: Int = ColorHelper.getArgb(
        color.alpha.toInt(),
        color.red.toInt(),
        color.green.toInt(),
        color.blue.toInt()
    )

    class Shadow(color: Color, val distance: Int) {
        val color: Int = ColorHelper.getArgb(
            color.alpha.toInt(),
            color.red.toInt(),
            color.green.toInt(),
            color.blue.toInt()
        )
    }

    override fun createToken(availableSize: AvailableSize): ElementToken {
        val childToken = child.createToken(availableSize)
        return ElementToken(
            width = childToken.width,
            height = childToken.height
        ) { ctx, size ->
            val (width, height) = size

            childToken.render(ctx, size)

            shadow?.let {
                for (i in 1..it.distance) {
                    ctx.drawContext.drawBorder(i, i, width, height, it.color)
                }
            }
            ctx.drawContext.drawBorder(0, 0, width, height, borderColor)

        }
    }
}

inline fun ScopedElementBuilder.border(
    color: Color,
    shadow: Shadow? = null,
    content: ScopedElementBuilder.() -> Unit
) = addNested(
    builder = ScopedElementBuilder(),
    content = content,
    buildElement = { builder ->
        BorderElement(
            color = color,
            shadow = shadow,
            child = builder.elements.collapseIntoColumn()
        )
    }
)
