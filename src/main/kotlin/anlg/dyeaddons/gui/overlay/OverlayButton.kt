package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.data.ColorCodes.*
import anlg.dyeaddons.utils.extensions.withScale
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.awt.Color

class OverlayButton(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val text: String,
    val color: Int,
    val onClick: () -> Unit,
    val textScale: Float = 1f
) : HudElement {

    //? if >=26.1 {
    override fun extractRenderState(context: GuiGraphicsExtractor, deltaTracker: DeltaTracker) = renderButton(context)
    //?} else {
    /*override fun render(context: GuiGraphicsExtractor, deltaTracker: DeltaTracker) = renderButton(context)
    *///?}

    private fun renderButton(context: GuiGraphicsExtractor) {
        val textRenderer = mc.font

        context.withScale(x, y, 1f) {
            context.fill(
                0,
                0,
                width,
                height,
                color
            )
            context.withScale(0, 0, textScale) {
                context.centeredText(
                    textRenderer,
                    Component.literal("${BOLD}$text"),
                    ((width / 2) / textScale).toInt(),
                    ((height / 2 - textRenderer.lineHeight / 2 * textScale) / textScale).toInt(),
                    Color(255, 255, 255, 255).rgb
                )
            }
            context.outline(
                0,
                0,
                width,
                height,
                Color(0, 0, 0, 155).rgb
            )
        }
    }

    fun onClick(mouseX: Double, mouseY: Double) {
        if (mouseX.toInt() in x..(x + width) && mouseY.toInt() in y..(y + height)) {
            onClick()
        }
    }
}