package anlg.dyeaddons.gui

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.gui.widgets.TabButton
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import java.awt.Color

class StatsScreen(val dye : Dye) : Screen(Component.literal("Statistics")) {

    val textRenderer = mc.font

    var panelX = 100
    var panelY = 100
    var panelWidth = 100
    var panelHeight = 100

    val statistics = dye.statistics?.let {
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
        panelX + panelWidth / 6,
        panelY - 15,
        Component.literal("Guide"))

    var calcTab = TabButton(
        "Calculator",
        null,
        panelWidth / 6,
        15,
        panelX + panelWidth / 6,
        panelY - 15,
        Component.literal("Calculator")
    )

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
        val titleScale = 2.0f
        context.pose().pushMatrix()
        context.pose().scale(titleScale)
        context.centeredText(
            textRenderer,
            "$dye Dye Statistics",
            ((panelX + panelWidth / 2) / titleScale).toInt(),
            ((panelY + 10) / titleScale).toInt(),
            Color(dye.color, false).rgb
        )
        context.pose().popMatrix()

        // Draw Tabs
        guideTab = TabButton(
            "Guide",
            GuideScreen(dye),
            panelWidth / 6,
            15, panelX,
            panelY - 15,
            Component.literal("Guide"))

        calcTab = TabButton(
            "Calculator",
            CalcScreen(dye),
            panelWidth / 6,
            15,
            panelX + panelWidth / 6,
            panelY - 15,
            Component.literal("Calculator"))

        statistics?.x = panelX
        statistics?.y = panelY + 50
        statistics?.width = panelWidth
        statistics?.height = panelHeight - 100

        context.fill(
            panelX,
            panelY - 15,
            panelX + panelWidth / 3 + (dye.calculator?.let {panelWidth / 6} ?: 0),
            panelY,
            Color(25, 25, 25, 200).rgb
        )

        context.centeredText(
            textRenderer,
            "Stats",
            panelX + panelWidth / 4 + (dye.calculator?.let {panelWidth / 6} ?: 0),
            panelY - 11,
            Color(255, 255, 255, 255).rgb
        )

        addRenderableWidget(guideTab)
        dye.calculator?.let {
            addRenderableWidget(calcTab)
        }

        statistics?.let {
            addRenderableWidget(it)
        }

        // Draw Result
        val resultScale = 1.5f
        context.pose().pushMatrix()
        context.pose().scale(resultScale)
        context.centeredText(
            textRenderer,
            statistics?.getOutput() ?: "Statistics Missing",
            ((panelX + panelWidth / 2) / resultScale).toInt(),
            ((panelY + panelHeight - 22) / resultScale).toInt(),
            Color(dye.color, false).rgb
        )
        context.pose().popMatrix()

        // Draw Notes
        context.text(
            textRenderer,
            "This tab is for generating initial estimates for dyes or if your statistics got deleted",
            panelX + 12,
            panelY + panelHeight - 55,
            Color(255, 255, 255).rgb
        )
        context.text(
            textRenderer,
            "Multiply statistics if they were during a dye rotation (e.g. if you killed 100 mobs during 3x, then add 200)",
            panelX + 12,
            panelY + panelHeight - 40,
            Color(255, 255, 255).rgb
        )


        // Save Button
        val saveButton = Button.builder(
            Component.literal("Save")
        ) { statistics?.onSave() }
            .bounds(panelX + panelWidth - 50,
                panelY + panelHeight - 30,
                40,
                20,)
            .build()

        addRenderableWidget(saveButton)

        super.extractRenderState(context, mouseX, mouseY, a)
    }

    override fun keyPressed(event : KeyEvent): Boolean {
        if (statistics != null && statistics.keyPressed(event)) {
            return true
        }

        return super.keyPressed(event)
    }

    override fun charTyped(event : CharacterEvent): Boolean {
        if (statistics != null && statistics.charTyped(event)) {
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