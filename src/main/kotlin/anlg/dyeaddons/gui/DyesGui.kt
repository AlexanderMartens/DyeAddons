package anlg.dyeaddons.gui

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.gui.widgets.DyePanel
import anlg.dyeaddons.utils.SkyblockUtils
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

    private val numCols = 4
    private val numRows = 5

    private var dyePanels = dyes.map { dye ->
        DyePanel(
            dye,
            1,
            dyeData?.get(dye)?.dropped ?: 0,
            dyeData?.get(dye)?.progress ?: 0.0,
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
        val titleScale = 2.0f
        context.pose().pushMatrix()
        context.pose().scale(titleScale)
        context.centeredText(
            textRenderer,
            if (profileName != null) "Dyes on $ign - $profileName" else "",
            ((width * 0.5) / titleScale).toInt(),
            ((height * 0.15 - textRenderer.lineHeight * titleScale) / titleScale).toInt(),
            Color(255, 255, 255, 255).rgb
        )
        context.pose().popMatrix()

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
                panelX + panelWidth - 4,
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

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (event.x in (width * 0.15)..(width * 0.85) && event.y in (height * 0.15)..(height * 0.85)) {
            return super.mouseClicked(event, doubleClick)
        }
        return false
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

}