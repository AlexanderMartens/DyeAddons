package anlg.dyeaddons.gui

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.gui.widgets.TabButton
import anlg.dyeaddons.utils.extensions.withScale
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
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
            panelHeight - 150)
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

    // Save Buttons
    val saveStatsButton = Button.builder(
        Component.literal("Save Stats")
    ) { statistics?.onSaveStats() }
        .bounds(
            panelX + panelWidth - textRenderer.width("Save Stats") - 10,
            panelY + panelHeight - 42,
            textRenderer.width("Save Stats") + 10,
            20,)
        .build()


    val saveProgressButton = Button.builder(
        Component.literal("Save Progress")
    ) { statistics?.onSaveProgress() }
        .bounds(
            panelX + panelWidth - textRenderer.width("Save Progress") - 10,
            panelY + panelHeight - 20,
            textRenderer.width("Save Progress") + 10,
            20,)
        .build()


    // Api Button
    val apiButton = Button.builder(
        Component.literal("Grab from Api")
    ) { statistics?.getFromApi() }
        .bounds(
            panelX + panelWidth - textRenderer.width("Grab from Api") - 10,
            panelY + panelHeight - 64,
            textRenderer.width("Grab from Api") + 10,
            20,)
        .tooltip(
            Tooltip.create(
                Component.literal(dye.apiTooltip)
            )
        )
        .build()


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
        panelX = (width * 0.1).toInt()
        panelY = (height * 0.1).toInt()
        panelWidth = (width * 0.8).toInt()
        panelHeight = (height * 0.8).toInt()
        val padding = width / 100

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
            panelY + 5,
            2.0f
        ) {
            context.centeredText(
                textRenderer,
                "$dye Dye Statistics",
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
        statistics?.y = panelY + panelHeight / 8
        statistics?.width = panelWidth
        statistics?.height = panelHeight - panelHeight / 8 - 64 - padding * 2

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
        context.withScale(
            panelX + panelWidth / 2,
            panelY + panelHeight - 22,
            1.5f
        ) {
            context.centeredText(
                textRenderer,
                statistics?.getOutput() ?: "Statistics Missing",
                0,
                0,
                Color(dye.color, false).rgb
            )
        }

        // Move buttons
        saveStatsButton.x = panelX + panelWidth - textRenderer.width("Save Stats") - 10 - padding
        saveStatsButton.y = panelY + panelHeight - 42 - padding
        saveStatsButton.width = textRenderer.width("Save Stats") + 10

        saveProgressButton.x = panelX + panelWidth - textRenderer.width("Save Progress") - 10 - padding
        saveProgressButton.y = panelY + panelHeight - 20 - padding
        saveProgressButton.width = textRenderer.width("Save Progress") + 10

        apiButton.x = panelX + panelWidth - textRenderer.width("Grab from Api") - 10 - padding
        apiButton.y = panelY + panelHeight - 64 - padding
        apiButton.width = textRenderer.width("Grab from Api") + 10

        addRenderableWidget(saveStatsButton)
        addRenderableWidget(saveProgressButton)
        addRenderableWidget(apiButton)

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