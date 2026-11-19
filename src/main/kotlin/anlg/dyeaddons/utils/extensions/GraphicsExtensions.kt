package anlg.dyeaddons.utils.extensions

import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.math.min

inline fun GuiGraphicsExtractor.withScale(
    x: Int,
    y: Int,
    scale: Float,
    block: GuiGraphicsExtractor.() -> Unit
) {
    pose().pushMatrix()
    pose().translate(x.toFloat(), y.toFloat())
    pose().scale(scale)
    block()
    pose().popMatrix()
}

fun GuiGraphicsExtractor.outline(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    color: Int,
    thickness: Int,
) {
    val t = min(thickness, min(width, height) / 2)
    if (t <= 0) return

    this.fill(x, y, x + width, y + t, color)
    this.fill(x, y + height - t, x + width, y + height, color)
    this.fill(x, y + t, x + t, y + height - t, color)
    this.fill(x + width - t, y + t, x + width, y + height - t, color)
}