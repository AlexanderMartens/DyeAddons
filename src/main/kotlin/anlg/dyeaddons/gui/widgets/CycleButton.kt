package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import java.awt.Color

class CycleButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Component,
    val padding : Int = 2,
    val sorts : List<String>,
    var currentIndex : Int = 0,
    val title : String = "",
) : AbstractWidget(
    x,
    y,
    width,
    height,
    message
) {
    var value = sorts[currentIndex]
    var sortText = value

    override fun extractWidgetRenderState(
        context: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        a: Float
    ) {
        val textRenderer = mc.font

        sortText = when (value) {
            "Chance to get at least X dyes" -> "Chance to get at least ${ConfigManager.data.config.atLeastXDyesProgressType} dye" +
                    (if (ConfigManager.data.config.atLeastXDyesProgressType > 1) "s" else "")
            else -> value
        }
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

        context.centeredText(
            textRenderer,
            title + sortText,
            x + width / 2,
            y + height / 2 - textRenderer.lineHeight / 2,
            Color(255, 255, 255, 255).rgb
        )

        if (value in listOf("Chance to get at least X dyes") && this.isHovered) {
            context.setTooltipForNextFrame(textRenderer, Component.literal("Hold shift to increase/decrease number of dyes"), mouseX, mouseY)
        }
    }

    override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
        super.onClick(event, doubleClick)
        if (event.hasShiftDown()) {
            when (sorts[currentIndex]) {
                "Chance to get at least X dyes" -> {
                    if (event.buttonInfo.button == 0) {
                        ConfigManager.data.config.atLeastXDyesProgressType++
                    } else {
                        ConfigManager.data.config.atLeastXDyesProgressType--
                        ConfigManager.data.config.atLeastXDyesProgressType =
                            ConfigManager.data.config.atLeastXDyesProgressType.coerceAtLeast(1)
                    }
                    return
                }
            }
        }
        currentIndex = if (event.buttonInfo.button == 0) {
            (currentIndex + 1) % sorts.size
        } else {
            (currentIndex + sorts.size - 1) % sorts.size
        }
        value = sorts[currentIndex]
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {}
}