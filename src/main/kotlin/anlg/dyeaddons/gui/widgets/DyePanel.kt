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
import java.text.DecimalFormat
import kotlin.math.min

class DyePanel(
    val dye : Dye,
    var padding : Int = 0,
    val dyesDropped : Int = 0,
    val dyeProgress : Double = 0.0,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Component
) : AbstractWidget(x, y, width, height, message) {

    val dyeTexture = dye.getTexture()
    val progressBar = min(dyeProgress, 1.0)

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
            x + width / 6 + padding * 2,
            y + padding * 2 + textRenderer.lineHeight,
            Color(dye.color, false).rgb)

        // Draw Dye Description
        context.textWithWordWrap(
            textRenderer,
            net.minecraft.network.chat.FormattedText.of(dye.description),
            x + padding * 2,
            y + height - 20 - padding,
            width - 2 * padding - 10,
            Color(66, 66, 66, 255).rgb
        )

        // Draw Dye Texture
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            dyeTexture,
            x + padding * 2,
            y + padding * 2,
            0f, 0f,
            height / 3, height / 3,
            height / 3,
            height / 3)

        // Draw Dyes Dropped
        val droppedScale = 1.5f
        context.pose().pushMatrix()
        context.pose().scale(droppedScale)
        context.text(
            textRenderer,
            dyesDropped.toString(),
            ((x + width - padding * 2 - textRenderer.width(dyesDropped.toString()) * droppedScale) / droppedScale).toInt(),
            ((y + padding * 2) / droppedScale).toInt(),
            Color(dye.color, false).rgb
        )
        context.pose().popMatrix()

        // Draw Dye Progress bar
        context.fill(
            x + padding * 2,
            y + height / 2,
            x + width - padding * 2,
            y + height / 2 + 10,
            Color(111, 111, 111, 255).rgb
        )
        context.fill(
            x + padding * 2 + 1,
            y + height / 2 + 1,
            x + width - padding * 2 - 1,
            y + height / 2 + 9,
            Color(55, 55, 55, 255).rgb
        )
        context.fill(
            x + padding * 2 + 1,
            y + height / 2 + 1,
            (x + padding * 2 + 1) + (progressBar * (width - padding * 4 - 2)).toInt(),
            y + height / 2 + 9,
            Color(dye.color, false).rgb
        )
        val progressText = DecimalFormat("#.##%").format(dyeProgress)
        val progressTextScale = 0.75f
        context.pose().pushMatrix()
        context.pose().scale(progressTextScale)
        context.text(
            textRenderer,
            progressText,
            ((x + width - padding * 2 - textRenderer.width(progressText) * progressTextScale) / progressTextScale).toInt(),
            ((y + height / 2 - textRenderer.lineHeight * progressTextScale) / progressTextScale).toInt(),
            Color(dye.color, false).rgb
        )
        context.pose().popMatrix()
    }

    override fun onClick(event: MouseButtonEvent, doubleClick: Boolean){
        super.onClick(event, doubleClick)

        mc.setScreen(GuideScreen(dye))
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {}

}