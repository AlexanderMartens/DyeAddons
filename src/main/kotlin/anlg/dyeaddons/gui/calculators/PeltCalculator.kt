package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.calc.TrapperTable
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class PeltCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Pelt Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Trapper Mobs per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Trapper Mobs per hour"), Parsers.FLOAT),
        "Tracking" to EditTextCalcWidget(x, y, width, 25, Component.literal("Tracking"), Parsers.FLOAT),
        "Tracker Crest" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Tracker Crest")))
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val mobsPerHour = context.getFloat("Trapper Mobs per hour")
        val tracking = context.getFloat("Tracking")
        val crest = context.getBoolean("Tracker Crest")

        if (mobsPerHour == 0f) {
            return "Invalid Input"
        }
        val result = TrapperTable(tracking, crest).getAverageDropChance() / mobsPerHour  / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}