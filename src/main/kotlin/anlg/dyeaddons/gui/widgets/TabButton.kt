package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.DyeAddons.Companion.mc
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import java.awt.Color

class TabButton(
    val name : String,
    val screen : Screen?,
    width : Int,
    height : Int,
    x : Int,
    y : Int
) : Button(width, height, x, y, 0){

    override fun draw(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int) {
        val mc = Minecraft.getInstance()
        val textRenderer = mc.font

        super.draw(context, mouseX, mouseY)

        context.centeredText(
            textRenderer,
            name,
            x + width / 2,
            y + 4,
            Color(255, 255, 255, 255).rgb
        )

    }

    override fun onClick(mouseX: Int, mouseY: Int) {
        if (isHovered(mouseX, mouseY)) {
            mc.setScreen(screen)
        }
    }
}