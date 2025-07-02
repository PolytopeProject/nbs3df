package codes.reason.nbs3df.mod.screen.elements

import codes.reason.nbs3df.mod.screen.*
import codes.reason.nbs3df.mod.screen.builder.ScopedElementBuilder
import codes.reason.nbs3df.util.BoundedValue.Companion.bounded
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences
import net.kyori.adventure.text.Component
import net.minecraft.client.MinecraftClient

typealias ComponentProvider = () -> Component

class TextElement(
    val content: ComponentProvider
) : Element() {

    companion object {
        private const val MINIMUM_WIDTH = 10
    }

    override fun createToken(availableSize: AvailableSize): ElementToken {
        val textRenderer = MinecraftClient.getInstance().textRenderer
        val text = MinecraftClientAudiences.of().asNative(content())
        val lines = textRenderer.wrapLines(text, availableSize.width ?: Int.MAX_VALUE)
        val width = lines.maxOfOrNull {
            textRenderer.getWidth(it)
        } ?: 0

        println("$width at ${lines.size}")

        val height = (textRenderer.fontHeight * lines.size).coerceAtMost(availableSize.height ?: Int.MAX_VALUE)

        return ElementToken(
            width = ElementDimension(
                minimumValue = MINIMUM_WIDTH.bounded,
                preferredValue = width.coerceAtLeast(MINIMUM_WIDTH)
            ),
            height = ElementDimension(
                minimumValue = height.bounded,
                preferredValue = height
            )
        ) { ctx, (width, height) ->
            ctx.push()
            ctx.clip(0, 0, width, height) {
                var currentY = 0
                lines.forEach {
                    ctx.drawContext.drawText(textRenderer, it, 0, currentY, 0xffffff, false)
                    currentY += textRenderer.fontHeight
                }
            }
            ctx.pop()
        }
    }
}

inline fun ScopedElementBuilder.text(
    noinline content: ComponentProvider
) = this.addChild(
    TextElement(
        content = content
    )
)

inline fun ScopedElementBuilder.string(
    crossinline content: () -> String
) = this.addChild(
    TextElement(
        content = { Component.text(content()) }
    )
)
