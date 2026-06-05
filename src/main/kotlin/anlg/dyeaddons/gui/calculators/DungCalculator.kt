package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class DungCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Dung Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Pests per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Pests per hour"), Parsers.FLOAT),
        "Overbloom" to EditTextCalcWidget(x, y, width, 25, Component.literal("Overbloom"), Parsers.FLOAT)),
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val pestsPerHour = context.getFloat("Pests per hour")
        val overbloom = context.getFloat("Overbloom")

        if (pestsPerHour == 0f) {
            return "Invalid Input"
        }
        val result = 250_000f / pestsPerHour / (1 + overbloom / 100) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}