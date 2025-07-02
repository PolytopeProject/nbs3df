package codes.reason.nbs3df.mod.screen

import net.minecraft.client.gui.DrawContext
import org.joml.Vector2f
import org.joml.Vector4f

class MenuRenderContext(
    val drawContext: DrawContext,
    private val initialMouseX: Int,
    private val initialMouseY: Int,
    val deltaTime: Float
) {
    private val mousePosition: Vector2f get() = toLocalSpace(initialMouseX.toFloat(), initialMouseY.toFloat())
    val mouseX: Int get() = mousePosition.x.toInt()
    val mouseY: Int get() = mousePosition.y.toInt()

    fun toLocalSpace(x: Float, y: Float) : Vector2f {
        val mat = drawContext.matrices.peek().positionMatrix.invert()
        val transformed = mat.transform(Vector4f(x, y, 0f, 1f))

        return Vector2f(transformed.x, transformed.y)
    }

    fun toScreenSpace(x: Float, y: Float) : Vector2f {
        val mat = drawContext.matrices.peek().positionMatrix
        val transformed = mat.transform(Vector4f(x, y, 0f, 1f))

        return Vector2f(transformed.x, transformed.y)
    }

    fun push() = drawContext.matrices.push()
    fun pop() = drawContext.matrices.pop()
    fun translate(x: Float, y: Float) = drawContext.matrices.translate(x, y, 0f)
    fun translate(x: Int, y: Int) = translate(x.toFloat(), y.toFloat())
    fun scale(x: Float, y: Float) = drawContext.matrices.scale(x, y, 0f)
    fun scale(x: Int, y: Int) = scale(x.toFloat(), y.toFloat())

    inline fun clip(minX: Int, minY: Int, maxX: Int, maxY: Int, block: () -> Unit) {
        val screenMin = toScreenSpace(minX.toFloat(), minY.toFloat())
        val screenMax = toScreenSpace(maxX.toFloat(), maxY.toFloat())
        drawContext.enableScissor(
            screenMin.x.toInt(),
            screenMin.y.toInt(),
            screenMax.x.toInt(),
            screenMax.y.toInt()
        )
        try {
            block()
        } finally {
            drawContext.disableScissor()
        }
    }

}