package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.config.Alignment
import anlg.dyeaddons.utils.extensions.withScale
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import java.awt.Color

abstract class AbstractOverlay(
    val name: String,
    var x : Int,
    var y : Int,
    var width : Int = 0,
    var height : Int = 0,
    var scale : Float = 1f,
    var enabled : Boolean = true,
    var alignment : Alignment = Alignment.LEFT,
) : HudElement {
    val leftEdge get() = when (alignment) {
        Alignment.LEFT -> x
        Alignment.CENTER -> x - (width / 2 * scale).toInt()
        Alignment.RIGHT -> x - (width * scale).toInt()
    }

    open fun shouldRender() = enabled

    open fun drawSample(context: GuiGraphicsExtractor) {
        context.withScale(leftEdge, y, scale) {
            context.fill(
                0,
                0,
                width,
                height,
                Color(100, 100, 100, 200).rgb
            )
        }
    }

    open fun isInSample(mouseX : Double, mouseY : Double) : Boolean {
        return mouseX.toInt() in leftEdge..(leftEdge + width * scale).toInt() &&
                mouseY.toInt() in y..(y + height * scale).toInt()
    }

    open fun getDisplayName(): String = name

    open fun onClick(mouseX : Double, mouseY : Double) {}

    abstract override fun extractRenderState(context: GuiGraphicsExtractor, deltaTracker: DeltaTracker)
}