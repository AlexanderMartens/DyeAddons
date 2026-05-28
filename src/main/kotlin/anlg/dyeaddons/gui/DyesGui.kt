package anlg.dyeaddons.gui

import anlg.dyeaddons.DyeAddons.Companion.MOD_ID
import anlg.dyeaddons.DyeAddons.Companion.logger
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.DyeData
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import java.awt.Color
import kotlin.math.max
import kotlin.math.sign

class DyePanel (
    val dye : Dye,
    val width : Int,
    val height : Int,
    val x : Int,
    val y : Int,
    val padding : Int){

    val dyeTexture = Identifier.fromNamespaceAndPath(MOD_ID, "dyes/${dye.name.lowercase()}.png")

    fun draw(context : GuiGraphicsExtractor, mouseX : Int, mouseY : Int) {
        val mc = Minecraft.getInstance()
        val textRenderer = mc.font

        context.fill(
            x + padding,
            y + padding,
            x + width - padding,
            y + height - padding,
            if (isHovered(mouseX, mouseY)) {
                Color(166, 166, 166, 100).rgb
            } else {
                Color(133, 133, 133, 100).rgb
            }
        )

        context.text(
            textRenderer,
            dye.toString(),
            x + width / 3,
            y + height / 2 - 5,
            Color(dye.color, false).rgb)

        context.text(
            textRenderer,
            if (dye.description.length > width / 6 - 1) dye.description.take(width / 6 - 3) + "..." else dye.description,
            x + padding + 5,
            y + height - 12 - padding,
            Color(66, 66, 66, 255).rgb
        )

        context.blit(
            RenderPipelines.GUI_TEXTURED,
            dyeTexture,
            x + width / 10,
            y + height / 4 - 5,
            0f, 0f,
            height / 2, height / 2,
            height / 2,
            height / 2)
    }

    fun isHovered(mouseX: Int, mouseY: Int): Boolean {
        return (x..(x+width)).contains(mouseX) && (y..(y+height)).contains(mouseY)
    }

    fun onClick() {
        logger.info("$dye clicked!")
    }
}


class DyesScreen : Screen(Component.literal("Dye Addons")) {

    private val dyes = DyeData().dyes

    private var scrollOffset = 0

    private val numCols = 4
    private val numRows = 5

    private lateinit var dyePanels : List<DyePanel>

    private val maxScrollOffset = (dyes.size + numCols - 1) / numCols - numRows

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {

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

        dyePanels = dyes.mapIndexed { index, dye ->
            DyePanel(
                dye,
                panelWidth / numCols,
                panelHeight / numRows,
                panelX + panelWidth * (index % numCols) / numCols,
                panelY + panelHeight * (index / numCols - scrollOffset) / numRows,
                panelHeight / 80,
            )
        }

        // Draw each cell
        dyePanels.forEachIndexed { index, panel ->
            if (((scrollOffset * numCols)..<(scrollOffset + numRows) * numCols).contains(index)) {
                panel.draw(context, mouseX, mouseY)
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

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (event.button() != 0) return super.mouseClicked(event, doubleClick)

        val mouseX = event.x()
        val mouseY = event.y()

        dyePanels.forEachIndexed { index, panel ->
            if (panel.isHovered(mouseX.toInt(), mouseY.toInt()) && ((scrollOffset * numCols)..<(scrollOffset + numRows) * numCols).contains(index)) {
                panel.onClick()
            }
        }

        return super.mouseClicked(event, doubleClick)
    }



}