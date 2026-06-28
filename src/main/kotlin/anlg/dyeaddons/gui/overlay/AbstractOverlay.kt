package anlg.dyeaddons.gui.overlay

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor

abstract class AbstractOverlay(
    var x : Int,
    var y : Int,
    var width : Int = 0,
    var height : Int = 0,
    var scale : Float = 1f,
    var enabled: Boolean = true,
) : HudElement {
    open fun shouldRender() = enabled

    abstract fun drawSample(context: GuiGraphicsExtractor)

    abstract fun isInSample(mouseX : Double, mouseY : Double) : Boolean

    abstract override fun extractRenderState(context: GuiGraphicsExtractor, deltaTracker: DeltaTracker)
}