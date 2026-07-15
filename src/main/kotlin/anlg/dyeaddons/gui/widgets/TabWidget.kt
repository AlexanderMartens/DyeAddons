package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.gui.CalcScreen
import anlg.dyeaddons.gui.GuideScreen
import anlg.dyeaddons.gui.StatsScreen
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class TabWidget (
    val dye : Dye,
    var tabWidth : Int,
    val selectedTab : String,
    width : Int,
    height : Int,
    x : Int,
    y : Int,
    message : Component
): AbstractWidget(x, y, width, height, message) {
    private var tabs: List<TabButton> = listOf()

    var guideTab = TabButton(
        "Guide",
        null,
        tabWidth,
        15,
        0,
        0,
        selectedTab == "Guide")
    var calcTab = TabButton(
        "Calculator",
        null,
        tabWidth,
        15,
        0,
        0,
        selectedTab == "Calculator")

    var statsTab = TabButton(
        "Stats",
        null,
        tabWidth,
        15,
        0,
        0,
        selectedTab == "Stats")

    override fun extractWidgetRenderState(
        context: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        a: Float
    ) {
        val tabList = mutableListOf<TabButton>()

        guideTab = TabButton(
            "Guide",
            GuideScreen(dye),
            tabWidth,
            15,
            0,
            0,
            selectedTab == "Guide")

        calcTab = TabButton(
            "Calculator",
            CalcScreen(dye),
            tabWidth,
            15,
            0,
            0,
            selectedTab == "Calculator")

        statsTab = TabButton(
            "Stats",
            StatsScreen(dye),
            tabWidth,
            15,
            0,
            0,
            selectedTab == "Stats")

        tabList.add(guideTab)

        dye.calculator?.let {
            tabList.add(calcTab)
        }

        dye.statistics?.let {
            tabList.add(statsTab)
        }

        tabs = tabList

        width = tabs.sumOf { it.width }
        var currentX = x

        for (tab in tabs) {
            tab.x = currentX
            tab.y = y

            tab.extractRenderState(context, mouseX, mouseY, a)

            currentX += tab.width
        }
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        for (tab in tabs) {
            if (!tab.selected && tab.mouseClicked(event, doubleClick)) {
                return true
            }
        }
        return false
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {}

}