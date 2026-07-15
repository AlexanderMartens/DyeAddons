package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.DyeAddons.Companion.mc
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import java.awt.Color

class TabButton(
    val name : String,
    val screen : Screen?,
    width : Int,
    height : Int,
    x : Int,
    y : Int,
    val selected : Boolean = false
) : AbstractWidget(x, y, width, height, Component.literal(name)){

    override fun extractWidgetRenderState(
        context: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        a: Float
    ) {
        val mc = Minecraft.getInstance()
        val textRenderer = mc.font

        if (selected) {
            context.fill(
                x,
                y ,
                x + width,
                y + height,
                Color(25, 25, 25, 200).rgb
            )
        } else {
            context.fill(
                x,
                y ,
                x + width,
                y + height,
                Color(25, 25, 25, 200).rgb
            )
            context.fill(
                x,
                y ,
                x + width,
                y + height,
                if (isHovered()) {
                    Color(166, 166, 166, 100).rgb
                } else {
                    Color(133, 133, 133, 100).rgb
                }
            )
        }

        context.centeredText(
            textRenderer,
            name,
            x + width / 2,
            y + 4,
            Color(255, 255, 255, 255).rgb
        )
    }

    override fun onClick(event: MouseButtonEvent, doubleClick: Boolean){
        super.onClick(event, doubleClick)
        if (screen == null || selected) return
        mc.setScreen(screen)
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {}
}