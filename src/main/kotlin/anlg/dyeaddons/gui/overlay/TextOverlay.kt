package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.withScale
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import kotlin.math.max

class TextOverlay(
    name: String,
    x : Int,
    y : Int,
    scale : Float,
    toggled : Boolean = false,
    val provider: TextOverlayProvider,
    val defaultWidth: Int = 50,
    val defaultHeight: Int = 13,
) : AbstractOverlay(
    "Text:$name",
    x,
    y,
    defaultWidth,
    defaultHeight,
    scale,
    toggled
) {

    override fun shouldRender(): Boolean {
        return SkyblockUtils.isInSkyblock() && super.shouldRender() && provider.shouldRender()
    }

    //? if >=26.1 {
    override fun extractRenderState(context: GuiGraphicsExtractor, deltaTracker: DeltaTracker) = renderText(context, deltaTracker)
    //?} else {
    /*override fun render(context: GuiGraphicsExtractor, deltaTracker: DeltaTracker) = renderText(context, deltaTracker)
    *///?}

    private fun renderText(context: GuiGraphicsExtractor, deltaTracker: DeltaTracker) {
        val textRenderer = mc.font
        val title = provider.textOverlayData.title
        val lines = provider.textOverlayData.lines
        val color = provider.textOverlayData.color

        context.withScale(x, y, scale) {
            var lineOffset = 0

            title?.let {
                context.text(textRenderer,
                    it,
                    0,
                    lineOffset,
                    color)
                lineOffset += textRenderer.lineHeight
            }

            for (line in lines) {
                context.text(textRenderer,
                    line,
                    0,
                    lineOffset,
                    color)
                lineOffset += textRenderer.lineHeight
            }
        }
        height = (provider.textOverlayData.lines.size + if (provider.textOverlayData.title != null) 1 else 0) * textRenderer.lineHeight
        width = max(provider.textOverlayData.lines.maxOfOrNull { textRenderer.width(it)} ?: 0,
            textRenderer.width(provider.textOverlayData.title ?: Component.literal("")))
        if (height == 0) height = defaultHeight
        if (width == 0) width = defaultWidth
    }
}