package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.DyeAddons.Companion.mc
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import java.awt.Color

class SortButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Component,
    val padding : Int = 2
) : AbstractWidget(
    x,
    y,
    width,
    height,
    message
) {
    private val sorts = listOf("A-Z", "Z-A", "# ↓", "# ↑", "% ↓", "% ↑")
    private var currentIndex = 0
    var currentSort = sorts[currentIndex]

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

        context.centeredText(
            textRenderer,
            currentSort,
            x + width / 2,
            y + height / 2 - textRenderer.lineHeight / 2,
            Color(255, 255, 255, 255).rgb
        )

    }

    override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
        super.onClick(event, doubleClick)
        currentIndex = (currentIndex + 1) % sorts.size
        currentSort = sorts[currentIndex]
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {}
}