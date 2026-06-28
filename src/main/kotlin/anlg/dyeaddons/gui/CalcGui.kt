package anlg.dyeaddons.gui

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.gui.widgets.TabButton
import anlg.dyeaddons.utils.extensions.withScale
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import java.awt.Color

class CalcScreen(val dye : Dye) : Screen(Component.literal("Calculator")) {

    val textRenderer = mc.font

    var panelX = 100
    var panelY = 100
    var panelWidth = 100
    var panelHeight = 100

    val calculator = dye.calculator?.let {
        it(
            panelX,
            panelY + 50,
            panelWidth,
            panelHeight - 100)
    }

    var guideTab = TabButton(
        "Guide",
        null,
        panelWidth / 6,
        15,
        panelX,
        panelY - 15,
        Component.literal("Guide"))

    var statsTab = TabButton(
        "Stats",
        null,
        panelWidth / 6,
        15,
        panelX + panelWidth / 3,
        panelY - 15,
        Component.literal("Stats"))

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
                "$dye Dye Calculator",
                0,
                0,
                Color(dye.color, false).rgb
            )
        }

        // Draw Tabs
        guideTab = TabButton(
            "Guide",
            GuideScreen(dye),
            panelWidth / 6,
            15,
            panelX,
            panelY - 15,
            Component.literal("Guide"))

        statsTab = TabButton(
            "Stats",
            StatsScreen(dye),
            panelWidth / 6,
            15,
            panelX + panelWidth / 3,
            panelY - 15,
            Component.literal("Stats"))

        calculator?.x = panelX
        calculator?.y = panelY + 50
        calculator?.width = panelWidth
        calculator?.height = panelHeight - 100

        context.fill(
            panelX,
            panelY - 15,
            panelX + panelWidth / 3,
            panelY,
            Color(25, 25, 25, 200).rgb
        )

        context.centeredText(
            textRenderer,
            "Calculator",
            panelX + panelWidth / 4,
            panelY - 11,
            Color(255, 255, 255, 255).rgb
        )

        addRenderableWidget(guideTab)
        dye.statistics?.let {
            addRenderableWidget(statsTab)
        }
        calculator?.let {
            addRenderableWidget(it)
        }

        // Draw Result
        context.withScale(
            panelX + panelWidth / 2,
            panelY + panelHeight - 22,
            1.5f
        ) {
            context.centeredText(
                textRenderer,
                calculator?.getOutput() ?: "Calculator Missing",
                0,
                0,
                Color(dye.color, false).rgb
            )
        }

        super.extractRenderState(context, mouseX, mouseY, a)
    }

    override fun keyPressed(event : KeyEvent): Boolean {
        if (calculator != null && calculator.keyPressed(event)) {
            return true
        }

        return super.keyPressed(event)
    }

    override fun charTyped(event : CharacterEvent): Boolean {
        if (calculator != null && calculator.charTyped(event)) {
            return true
        }

        return super.charTyped(event)
    }

    override fun onClose() {
        mc.setScreen(DyesScreen())
    }

    override fun isPauseScreen(): Boolean {
        return false
    }
}