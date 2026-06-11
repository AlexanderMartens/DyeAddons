package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.data.CalcValue
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class EditTextCalcWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Component,
    private val parser : (String) -> CalcValue,
    hidden : Boolean = false,
    val defaultValue : String = ""
) : AbstractCalcWidget(
    x,
    y,
    width,
    height,
    message,
    EditBox(
        mc.font,
        x + width - 50,
        y + 2,
        50,
        height - 4,
        message,
        ),
    hidden
) {
    val editBox = widget as EditBox

    init {
        editBox.value = defaultValue
    }

    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractWidgetRenderState(context, mouseX, mouseY, a)

        editBox.setHint(Component.literal("0"))

        editBox.x = x + width - 60
        editBox.y = y + 3
        editBox.width = 50
        editBox.height = height - 6
        editBox.extractRenderState(context, mouseX, mouseY, a)
    }
        override fun getValue(): CalcValue {
            return parser(editBox.value)
    }
}