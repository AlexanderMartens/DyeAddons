package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.gui.GuideScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import java.awt.Color

class DyePanel(
    val dye : Dye,
    width : Int,
    height : Int,
    x : Int,
    y : Int,
    padding : Int
) : Button(width, height, x, y, padding){

    val dyeTexture = dye.getTexture()

    override fun draw(context : GuiGraphicsExtractor, mouseX : Int, mouseY : Int) {
        val mc = Minecraft.getInstance()
        val textRenderer = mc.font

        super.draw(context, mouseX, mouseY)

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

    override fun onClick(mouseX: Int, mouseY: Int) {
        if (isHovered(mouseX, mouseY)) {
            mc.setScreen(GuideScreen(dye))
        }
    }
}