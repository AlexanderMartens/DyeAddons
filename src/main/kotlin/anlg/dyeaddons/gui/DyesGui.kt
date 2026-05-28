package anlg.dyeaddons.gui

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import java.awt.Color
import kotlin.math.max
import kotlin.math.sign

class DyesScreen : Screen(Component.literal("Dye Addons")) {

    private val numbers = (1..41).toList()

    private var scrollOffset = 0

    private val numCols = 3

    private val numRows = 4

    private val maxScrollOffset = (numbers.size + numCols - 1) / numCols - numRows

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {

        val mc = minecraft
        val textRenderer = mc.font

        fun drawDyePanel(width : Int, height : Int, x : Int, y : Int, padding : Int = 0, number : Int) {
            context.fill(
                x + padding,
                y + padding,
                x + width - padding,
                y + height - padding,
                Color(255, 255, 255, 255).rgb
            )

            context.text(
                textRenderer,
                number.toString(),
                x + width / 2,
                y + height / 2,
                Color(0, 0, 0, 255).rgb,
                true)
        }

        // Draw background
        context.fill(
            0,
            0,
            width,
            height,
            Color(0, 0, 0, 125).rgb
        )

        // Draw Panel
        val panelX = (width * 0.15).toInt()
        val panelY = (height * 0.15).toInt()
        val panelWidth = (width * 0.7).toInt()
        val panelHeight = (height * 0.7).toInt()

        context.fill(
            panelX,
            panelY,
            panelX + panelWidth,
            panelY + panelHeight,
            Color(25, 25, 25, 200).rgb
        )

        // Draw each cell
        numbers.forEachIndexed { index, number ->
            if (((scrollOffset * numCols)..<(scrollOffset + numRows) * numCols).contains(index)) {
                drawDyePanel(
                    panelWidth / 3,
                    panelHeight / 4,
                    panelX + panelWidth * (index % 3) / 3,
                    panelY + panelHeight * (index / 3 - scrollOffset) / 4,
                    panelHeight / 80,
                    number)
            }
        }

        // Draw scroll bar
        if (maxScrollOffset > 0) {
            context.fill(
                panelX + panelWidth,
                panelY + (panelHeight * (scrollOffset / (maxScrollOffset + numRows).toFloat())).toInt(),
                panelX + panelWidth + 5,
                panelY + panelHeight - (panelHeight * ((maxScrollOffset - scrollOffset)  / (maxScrollOffset + numRows).toFloat())).toInt(),
                Color(155, 155, 155, 200).rgb,
            )
        }

        super.extractRenderState(context, mouseX, mouseY, delta)
    }

    override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {

        val maxScroll = max(0, maxScrollOffset)

        scrollOffset -= sign(scrollY).toInt()

        scrollOffset = scrollOffset.coerceIn(0, maxScroll)

        return super.mouseScrolled(x, y, scrollX, scrollY)
    }

}