package codes.reason.nbs3df.mod.screen.elements

import codes.reason.nbs3df.mod.screen.AvailableSize
import codes.reason.nbs3df.mod.screen.Element
import codes.reason.nbs3df.mod.screen.ElementToken
import codes.reason.nbs3df.mod.screen.builder.ScopedElementBuilder
import codes.reason.nbs3df.mod.screen.layout.collapseIntoColumn
import net.minecraft.util.math.ColorHelper
import java.awt.Color

class BackgroundElement(
    color: Color,
    private val shadow: DropShadow?,
    private val content: Element
) : Element() {

    private val argb: Int = ColorHelper.getArgb(
        color.alpha.toInt(),
        color.red.toInt(),
        color.green.toInt(),
        color.blue.toInt()
    )

    class DropShadow(
        val x: Int,
        val y: Int,
        color: Color
    ) {
        val color: Int = ColorHelper.getArgb(
            color.alpha.toInt(),
            color.red.toInt(),
            color.green.toInt(),
            color.blue.toInt()
        )
    }

    override fun createToken(availableSize: AvailableSize): ElementToken {
        val childToken = content.createToken(availableSize)
        return ElementToken(
            width = childToken.width,
            height = childToken.height
        ) { ctx, size ->
            val (width, height) = size
            if (shadow != null) {
                ctx.drawContext.fill(shadow.x, shadow.y, width + shadow.x, height + shadow.y, shadow.color)
            }
            ctx.drawContext.fill(0, 0, width, height, argb)
            childToken.render(ctx, size)
        }
    }
}

inline fun ScopedElementBuilder.background(
    color: Color,
    shadow: BackgroundElement.DropShadow? = null,
    content: ScopedElementBuilder.() -> Unit
) = addNested(
    builder = ScopedElementBuilder(),
    content = content,
    buildElement = { builder ->
        BackgroundElement(
            color = color,
            shadow = shadow,
            content = builder.elements.collapseIntoColumn()
        )
    }
)
