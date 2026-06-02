package anlg.dyeaddons.gui

import anlg.dyeaddons.data.DyeData
import anlg.dyeaddons.gui.widgets.DyePanel
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import java.awt.Color
import kotlin.math.max
import kotlin.math.sign


class DyesScreen : Screen(Component.literal("Dye Addons")) {

    private val dyes = DyeData().dyes

    private var scrollOffset = 0

    private val numCols = 4
    private val numRows = 5

    private var dyePanels = dyes.map { dye ->
        DyePanel(
            dye,
            1,
            1,
            1,
            1,
            1,
            Component.literal("$dye")
        )
    }

    override fun init() {
        super.init()
        for (panel in dyePanels) {
            addWidget(panel)
        }
    }

    private val maxScrollOffset = (dyes.size + numCols - 1) / numCols - numRows

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        super.extractRenderState(context, mouseX, mouseY, delta)

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

        for ((index, panel) in dyePanels.withIndex()) {
            panel.padding = panelHeight / 80
            panel.setSize(
                panelWidth / numCols,
                panelHeight / numRows
            )
            panel.setPosition(
                panelX + panelWidth * (index % numCols) / numCols,
                panelY + panelHeight * (index / numCols - scrollOffset) / numRows
            )
        }

        context.enableScissor(
            panelX,
            panelY,
            panelX + panelWidth,
            panelY + panelHeight,
        )
        dyePanels.forEach { panel ->
            panel.extractRenderState(context, mouseX, mouseY, delta)
        }
        context.disableScissor()

        // Draw scroll bar
        if (maxScrollOffset > 0) {
            context.fill(
                panelX + panelWidth - 5,
                panelY + (panelHeight * (scrollOffset / (maxScrollOffset + numRows).toFloat())).toInt(),
                panelX + panelWidth,
                panelY + panelHeight - (panelHeight * ((maxScrollOffset - scrollOffset)  / (maxScrollOffset + numRows).toFloat())).toInt(),
                Color(155, 155, 155, 200).rgb,
            )
        }

    }

    override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {

        val maxScroll = max(0, maxScrollOffset)

        scrollOffset -= sign(scrollY).toInt()

        scrollOffset = scrollOffset.coerceIn(0, maxScroll)

        return super.mouseScrolled(x, y, scrollX, scrollY)
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

}