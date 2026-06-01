package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.gui.GuideScreen
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import java.awt.Color

class DyePanel(
    val dye : Dye,
    var padding : Int = 0,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Component
) : AbstractWidget(x, y, width, height, message) {

    val dyeTexture = dye.getTexture()

    override fun extractWidgetRenderState(
        context: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        a: Float
    ) {
        val textRenderer = mc.font

        // Draw Background
        context.fill(
            x + padding,
            y + padding,
            x + width - padding,
            y + height - padding,
            if (isHovered()) {
                Color(166, 166, 166, 100).rgb
            } else {
                Color(133, 133, 133, 100).rgb
            }
        )

        // Draw Dye Text
        context.text(
            textRenderer,
            dye.toString(),
            x + width / 3,
            y + height / 3 - 5,
            Color(dye.color, false).rgb)

        // Draw Dye Description
        context.textWithWordWrap(
            textRenderer,
            net.minecraft.network.chat.FormattedText.of(dye.description),
            x + padding + 5,
            y + height - 20 - padding,
            width - 2 * padding - 10,
            Color(66, 66, 66, 255).rgb
        )

        // Draw Dye Texture
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            dyeTexture,
            x + width / 15,
            y + height / 10,
            0f, 0f,
            height / 2, height / 2,
            height / 2,
            height / 2)
    }

    override fun onClick(event: MouseButtonEvent, doubleClick: Boolean){
        super.onClick(event, doubleClick)

        mc.setScreen(GuideScreen(dye))
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {}

}