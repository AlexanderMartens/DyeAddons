package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class LividCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Livid Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "M5 Runs per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("M5 Runs per hour"), Parsers.FLOAT),
        "Kismet" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Kismet")),
        "Full Meter" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Full Meter"))
    )
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val runsPerHour = context.getFloat("M5 Runs per hour")
        val kismet = context.getBoolean("Kismet")
        val fullMeter = context.getBoolean("Full Meter")

        if (runsPerHour == 0f) {
            return "Invalid Input"
        }
        val result = if (fullMeter) {
            1_000_000f / (runsPerHour * 300f)
        } else {
            (5_000f / runsPerHour / if (kismet) 2f else 1f )/ vincent
        }
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}