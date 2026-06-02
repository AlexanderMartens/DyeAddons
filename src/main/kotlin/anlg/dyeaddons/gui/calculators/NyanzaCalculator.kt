package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class NyanzaCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Nyanza Dye"),
    mapOf("Commissions" to EditTextCalcWidget(x, y, width, 25, Component.literal("Commissions per hour"), Parsers.FLOAT))
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val comms = context.getFloat("Commissions")

        if (comms == 0f) {
            return "Invalid Input"
        }
        val result = 250000 / comms
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}