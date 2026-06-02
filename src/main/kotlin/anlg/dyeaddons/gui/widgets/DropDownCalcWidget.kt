package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.gui.calculators.CalcValue
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.CycleButton
import net.minecraft.network.chat.Component

class DropDownCalcWidget (
    x : Int,
    y : Int,
    width : Int,
    height:  Int,
    message : Component,
    values : List<String> = listOf(),
) : AbstractCalcWidget(
    x,
    y,
    width,
    height,
    message,
    CycleButton.builder<String>(
        { s : String -> Component.literal(s)},
        {values.first()})
        .withValues(values)
        .displayOnlyValue()
        .create(
        x,
        y,
        width,
        height,
        message
    )
) {
    val cycleButton = widget as CycleButton<*>

    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractWidgetRenderState(context, mouseX, mouseY, a)

        cycleButton.x = x + width - 60
        cycleButton.y = y + 3
        cycleButton.width = 50
        cycleButton.height = height - 6
        cycleButton.extractRenderState(context, mouseX, mouseY, a)
    }

    override fun getValue(): CalcValue {
        return CalcValue.StringVal(cycleButton.value.toString())
    }
}