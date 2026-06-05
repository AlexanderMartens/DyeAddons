package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.DyeAddons.Companion.logger
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.gui.calculators.CalcValue
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import java.awt.Color


abstract class AbstractCalcWidget(
    x : Int,
    y : Int,
    width : Int,
    height : Int,
    message : Component,
    val widget : AbstractWidget,
    var hidden : Boolean = false,) : AbstractWidget(
    x,
    y,
    width,
    height,
    message
), GuiEventListener {
    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        val textRenderer = mc.font
        context.fill(
            x,
            y,
            x + width,
            y + height,
            Color(100, 100, 100, 55).rgb
        )

        context.text(
            textRenderer,
            message,
            x + 10,
            y + height / 2 - textRenderer.lineHeight / 2,
            Color(255, 255, 255, 255).rgb
        )
    }

    abstract fun getValue(): CalcValue

    override fun updateWidgetNarration(output: NarrationElementOutput) {}

    override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
        super.onClick(event, doubleClick)
        logger.info("${message.string} clicked!")
        widget.onClick(event, doubleClick)
    }

    override fun mouseClicked(
        event: MouseButtonEvent,
        doubleClick: Boolean
    ): Boolean {
        val clicked = widget.mouseClicked(event, doubleClick)

        if (clicked) {
            widget.isFocused = true
        }

        return clicked
    }

    override fun mouseReleased(
        event: MouseButtonEvent
    ): Boolean {
        return widget.mouseReleased(event)
    }

    override fun mouseDragged(
        event: MouseButtonEvent,
        dx: Double,
        dy: Double
    ): Boolean {
        return widget.mouseDragged(event, dx, dy)
    }


    override fun keyPressed(event : KeyEvent): Boolean {
        return widget.keyPressed(event)
    }

    override fun charTyped(event : CharacterEvent): Boolean {
        return widget.charTyped(event)
    }
}