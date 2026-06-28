package anlg.dyeaddons.utils.extensions

import net.minecraft.client.gui.GuiGraphicsExtractor

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