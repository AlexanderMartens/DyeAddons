package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class EmeraldCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Emerald Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Emerald Collection per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Emerald Collection per hour"), Parsers.FLOAT),
        "Mining Fortune" to EditTextCalcWidget(x, y, width, 25, Component.literal("Mining Fortune"), Parsers.FLOAT))
    ) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val emeraldsPerHour = context.getFloat("Emerald Collection per hour")
        val fortune = context.getFloat("Mining Fortune")

        if (emeraldsPerHour == 0f) {
            return "Invalid Input"
        }
        val result = 5_000_000 * (1 + fortune/100) * 5f / emeraldsPerHour / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}