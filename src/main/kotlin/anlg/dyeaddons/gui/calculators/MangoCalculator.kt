package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class MangoCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Mango Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Log Collection per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Log Collection per hour"), Parsers.FLOAT),
        "Foraging Fortune" to EditTextCalcWidget(x, y, width, 25, Component.literal("Foraging Fortune"), Parsers.FLOAT))
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val collectionPerHour = context.getFloat("Log Collection per hour")
        val fortune = context.getFloat("Foraging Fortune")

        if (collectionPerHour == 0f) {
            return "Invalid Input"
        }
        val result = 10_000_000 * (1 + fortune/100) / collectionPerHour / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}