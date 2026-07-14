package anlg.dyeaddons.gui

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.gui.widgets.DyePanel
import anlg.dyeaddons.gui.widgets.SortButton
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.withScale
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import java.awt.Color
import kotlin.math.max
import kotlin.math.sign


class DyesScreen : Screen(Component.literal("Dye Addons")) {

    private val dyeData = ProfileStorage.currentProfile()?.dyeData ?: ProfileStorage.lastPlayedProfile()?.dyeData
    private val dyes = dyeData?.keys ?: Dye.entries

    private var scrollOffset = 0

    private var numCols = 4
    private var numRows = 5
    private var sort = ""

    private var dyePanels = dyes.map { dye ->
        DyePanel(
            dye,
            dyeData?.get(dye)?.dropped ?: 0,
            dyeData?.get(dye)?.progress ?: 0.0,
            1,
            1,
            1,
            1,
            Component.literal("$dye")
        )
    }

    private val sortButton = SortButton(
        1,
        1,
        1,
        1,
        Component.literal("Sort Button")
    )

    override fun init() {
        super.init()
        for (panel in dyePanels) {
            addWidget(panel)
        }
        addRenderableWidget(sortButton)
    }

    private var maxScrollOffset = (dyes.size + numCols - 1) / numCols - numRows

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {

        val basePanelWidth = 160
        val basePanelHeight = 65

        numCols = ((width * 0.8) / basePanelWidth).toInt().coerceAtLeast(1)
        numRows = ((height * 0.8) / basePanelHeight).toInt().coerceAtLeast(1)

        maxScrollOffset = (dyes.size + numCols - 1) / numCols - numRows

        // Sort Dyes
        sort = sortButton.currentSort
        dyePanels = when (sort) {
            "A-Z" -> dyePanels.sortedBy { it.dye }
            "Z-A" -> dyePanels.sortedByDescending { it.dye }
            "# ↓" -> dyePanels.sortedBy { it.dye }.sortedByDescending { it.dyesDropped }
            "# ↑" -> dyePanels.sortedBy { it.dye }.sortedBy { it.dyesDropped }
            "% ↓" -> dyePanels.sortedBy { it.dye }.sortedByDescending { it.dyeProgress }
            "% ↑" -> dyePanels.sortedBy { it.dye }.sortedBy { it.dyeProgress }
            else -> dyePanels
        }

        val textRenderer = mc.font

        // Draw background
        context.fill(
            0,
            0,
            width,
            height,
            Color(0, 0, 0, 125).rgb
        )

        // Draw Title
        val ign = mc.player?.name?.string
        val profileName : String?
        if (!SkyblockUtils.profileName.isEmpty()) {
            profileName = SkyblockUtils.profileName
        } else {
            profileName = ProfileStorage.lastPlayedProfileName()
        }
        context.withScale(
            width / 2,
            height / 10,
            2.0f
        ) {
            context.centeredText(
                textRenderer,
                if (profileName != null) "Dyes on $ign - $profileName" else "",
                0,
                -textRenderer.lineHeight,
            Color(255, 255, 255, 255).rgb
            )
        }

        // Draw Panel
        val panelX = (width * 0.1).toInt()
        val panelY = (height * 0.1).toInt()
        val panelWidth = (width * 0.8).toInt()
        val panelHeight = (height * 0.8).toInt()

        context.fill(
            panelX,
            panelY,
            panelX + panelWidth,
            panelY + panelHeight,
            Color(25, 25, 25, 200).rgb
        )

        for ((index, panel) in dyePanels.withIndex()) {
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
                panelX + panelWidth - 4,
                panelY + (panelHeight * (scrollOffset / (maxScrollOffset + numRows).toFloat())).toInt(),
                panelX + panelWidth,
                panelY + panelHeight - (panelHeight * ((maxScrollOffset - scrollOffset)  / (maxScrollOffset + numRows).toFloat())).toInt(),
                Color(155, 155, 155, 200).rgb,
            )
        }

        // Sort Button
        sortButton.x = panelX + panelWidth - 50
        sortButton.y = panelY - 25
        sortButton.width = 50
        sortButton.height = 25

        super.extractRenderState(context, mouseX, mouseY, delta)
    }

    override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {

        val maxScroll = max(0, maxScrollOffset)

        scrollOffset -= sign(scrollY).toInt()

        scrollOffset = scrollOffset.coerceIn(0, maxScroll)

        return super.mouseScrolled(x, y, scrollX, scrollY)
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (event.x in (width * 0.1)..(width * 0.9) && event.y in (height * 0.1)..(height * 0.9)) {
            dyePanels.forEach { panel ->
                if (panel.isHovered) {
                    panel.onClick(event, doubleClick)
                }
            }
            return super.mouseClicked(event, doubleClick)
        }
        if (sortButton.isHovered) {
            sortButton.onClick(event, doubleClick)
        }
        return false
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

}