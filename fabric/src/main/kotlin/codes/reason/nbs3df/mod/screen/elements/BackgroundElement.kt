package codes.reason.nbs3df.mod.screen.elements

import codes.reason.nbs3df.mod.screen.AvailableSize
import codes.reason.nbs3df.mod.screen.Element
import codes.reason.nbs3df.mod.screen.ElementToken
import codes.reason.nbs3df.mod.screen.builder.ScopedElementBuilder
import codes.reason.nbs3df.mod.screen.layout.collapseIntoColumn
import net.minecraft.util.math.ColorHelper
import java.awt.Color

class BackgroundElement(
    backgroundColor: Color,
    private val child: Element
) : Element() {

    private val argb: Int = ColorHelper.getArgb(
        backgroundColor.alpha.toInt(),
        backgroundColor.red.toInt(),
        backgroundColor.green.toInt(),
        backgroundColor.blue.toInt()
    )

    override fun createToken(availableSize: AvailableSize): ElementToken {
        val childToken = child.createToken(availableSize)
        return ElementToken(
            width = childToken.width,
            height = childToken.height
        ) { ctx, size ->
            val (width, height) = size
            ctx.drawContext.fill(0, 0, width, height, argb)
            childToken.render(ctx, size)
        }
    }
}

inline fun ScopedElementBuilder.background(
    color: Color,
    content: ScopedElementBuilder.() -> Unit
) = addNested(
    builder = ScopedElementBuilder(),
    content = content,
    buildElement = { builder -> BackgroundElement(color, builder.elements.collapseIntoColumn()) }
)
