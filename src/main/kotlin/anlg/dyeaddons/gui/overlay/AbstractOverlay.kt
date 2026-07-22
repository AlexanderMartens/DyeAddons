package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.utils.extensions.withScale
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import java.awt.Color

abstract class AbstractOverlay(
    var x : Int,
    var y : Int,
    var width : Int = 0,
    var height : Int = 0,
    var scale : Float = 1f,
    var enabled: Boolean = true,
) : HudElement {
    open fun shouldRender() = enabled

    open fun drawSample(context: GuiGraphicsExtractor) {
        context.withScale(x, y, scale) {
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
        return mouseX.toInt() in x..(x + width * scale).toInt() &&
                mouseY.toInt() in y..(y + height * scale).toInt()
    }

    open fun onClick(mouseX : Double, mouseY : Double) {}

    abstract override fun extractRenderState(context: GuiGraphicsExtractor, deltaTracker: DeltaTracker)
}