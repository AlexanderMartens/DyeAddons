package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.utils.extensions.renderElement
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Checkbox
import net.minecraft.network.chat.Component


class CheckboxCalcWidget (
    x : Int,
    y : Int,
    width : Int,
    height:  Int,
    message : Component,
    defaultValue : Boolean = false,
    hidden : Boolean = false,
) : AbstractCalcWidget(
    x,
    y,
    width,
    height,
    message,
    Checkbox.builder(Component.literal(""), mc.font)
        .selected(defaultValue)
        .build(),
    hidden
) {

    val checkbox = widget as Checkbox

    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractWidgetRenderState(context, mouseX, mouseY, a)

        checkbox.x = x + width - 27
        checkbox.y = y + 3
        checkbox.width = 50
        checkbox.height = height - 6
        checkbox.renderElement(context, mouseX, mouseY, a)
    }

    override fun getValue(): CalcValue {
        return CalcValue.BoolVal(checkbox.selected())
    }
}