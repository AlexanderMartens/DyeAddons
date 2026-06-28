package anlg.dyeaddons.gui

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.gui.widgets.TabButton
import anlg.dyeaddons.utils.withScale
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import java.awt.Color
import net.minecraft.network.chat.FormattedText
import net.minecraft.util.FormattedCharSequence
import kotlin.math.max
import kotlin.math.sign

class GuideScreen(val dye : Dye) : Screen(Component.literal("Guide")) {

    val textRenderer = mc.font

    val guide = dye.getGuide()

    var panelX = 100
    var panelY = 100
    var panelWidth = 100
    var panelHeight = 100

    var calcTab = TabButton(
        "Calculator",
        null,
        panelWidth / 6,
        15,
        panelX + panelWidth / 6,
        panelY - 15,
        Component.literal("Calculator"))

    var statsTab = TabButton(
        "Stats",
        null,
        panelWidth / 6,
        15,
        panelX + panelWidth / 6,
        panelY - 15,
        Component.literal("Stats")
    )

    private var scrollOffset = 0

    private val lineHeight = textRenderer.lineHeight + 2
    private var visibleLines = 10

    private var wrappedLines: List<FormattedCharSequence> = emptyList()

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {

        clearWidgets()

        // Draw background
        context.fill(
            0,
            0,
            width,
            height,
            Color(0, 0, 0, 125).rgb
        )

        // Draw Panel
        panelX = (width * 0.15).toInt()
        panelY = (height * 0.15).toInt()
        panelWidth = (width * 0.7).toInt()
        panelHeight = (height * 0.7).toInt()

        visibleLines = (panelHeight - 60) / lineHeight
        wrappedLines = textRenderer.split(
            FormattedText.of(guide.content),
            panelWidth - 20 // width of text area
        )

        val maxScrollOffset = max(0, wrappedLines.size - visibleLines)
        scrollOffset = scrollOffset.coerceIn(0, maxScrollOffset)

        context.fill(
            panelX,
            panelY,
            panelX + panelWidth,
            panelY + panelHeight,
            Color(25, 25, 25, 200).rgb
        )

        // Draw title
        context.withScale(
            panelX + panelWidth / 2,
            panelY + 10,
            2.0f
        ) {
            context.centeredText(
                textRenderer,
                "$dye Dye Guide",
                0,
                0,
                Color(dye.color, false).rgb
            )
        }

        // Draw Tabs
        calcTab = TabButton(
            "Calculator",
            CalcScreen(dye),
            panelWidth / 6,
            15,
            panelX + panelWidth / 6,
            panelY - 15,
            Component.literal("Calculator"))

        statsTab = TabButton(
            "Stats",
            StatsScreen(dye),
            panelWidth / 6,
            15,
            panelX + panelWidth / 6,
            panelY - 15,
            Component.literal("Stats")
        )

        context.fill(
            panelX,
            panelY - 15,
            panelX + panelWidth / 6,
            panelY,
            Color(25, 25, 25, 200).rgb
        )

        context.centeredText(
            textRenderer,
            "Guide",
            panelX + panelWidth / 12,
            panelY - 11,
            Color(255, 255, 255, 255).rgb
        )

        dye.calculator?.let {
            addRenderableWidget(calcTab)
            statsTab.x = panelX + panelWidth / 3
        }

        dye.statistics?.let {
            addRenderableWidget(statsTab)
        }

        // Draw guide text
        for (i in scrollOffset until scrollOffset + visibleLines) {

            if (i >= wrappedLines.size) {
                break
            }

            context.text(
                textRenderer,
                wrappedLines[i],
                panelX + 10,
                panelY + 40 + (i - scrollOffset) * lineHeight,
                Color(255, 255, 255, 200).rgb
            )
        }

        context.text(
            textRenderer,
            "Author: ${guide.author}",
            panelX + 10,
            panelY + panelHeight - 20,
            Color(155, 155, 155, 200).rgb
        )

        // Draw Scroll Bar
        if (maxScrollOffset > 0) {
            context.fill(
                panelX + panelWidth - 5,
                panelY + (panelHeight * (scrollOffset / (maxScrollOffset + visibleLines).toFloat())).toInt(),
                panelX + panelWidth,
                panelY + panelHeight - (panelHeight * ((maxScrollOffset - scrollOffset)  / (maxScrollOffset + visibleLines).toFloat())).toInt(),
                Color(155, 155, 155, 200).rgb,
            )
        }

        super.extractRenderState(context, mouseX, mouseY, a)
    }

    override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {

        val maxScrollOffset = max(0, wrappedLines.size - visibleLines)

        scrollOffset -= sign(scrollY).toInt()

        scrollOffset = scrollOffset.coerceIn(0, maxScrollOffset)

        return super.mouseScrolled(x, y, scrollX, scrollY)
    }

    override fun onClose() {
        mc.setScreen(DyesScreen())
    }

    override fun isPauseScreen(): Boolean {
        return false
    }
}