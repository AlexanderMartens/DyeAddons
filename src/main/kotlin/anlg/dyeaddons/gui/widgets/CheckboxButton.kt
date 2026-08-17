package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.DyeAddons.Companion.mc
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Checkbox
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import java.awt.Color

class CheckboxButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Component,
    val padding : Int = 2,
    val default : Boolean = false,
) : AbstractWidget(
    x,
    y,
    width,
    height,
    message
) {
    val checkbox = Checkbox.builder(message, mc.font).pos(x, y).selected(default).maxWidth(height).build()

    val selected get() = checkbox.selected()

    override fun extractWidgetRenderState(
        context: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        a: Float
    ) {
        val textRenderer = mc.font
        // Widget Background
        context.fill(
            x,
            y,
            x + width,
            y + height,
            Color(25, 25, 25, 200).rgb
        )

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

        checkbox.x = x + padding * 2
        checkbox.y = y + padding * 2
        checkbox.width = width - padding * 2
        checkbox.height = height - padding * 2
        checkbox.adjustWidth(checkbox.width, textRenderer)

        checkbox.extractRenderState(context, mouseX, mouseY, a)
    }

    override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
        super.onClick(event, doubleClick)
        if (event.buttonInfo.button != 0) return
        checkbox.onClick(event, doubleClick)
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {}
}