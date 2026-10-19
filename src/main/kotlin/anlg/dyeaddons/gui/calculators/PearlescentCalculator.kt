package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class PearlescentCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Pearlescent Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Base Chance" to DropDownCalcWidget(x, y, width, 25, Component.literal("Tier of Mob"), listOf("1/10m", "1/5m", "1/100k")),
        "Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Kills per hour"), Parsers.FLOAT),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT)),
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val baseChance = when(context.getString("Base Chance")) {
            "1/10m" -> Dye.PEARLESCENT.getDropRate("T1")
            "1/5m" -> Dye.PEARLESCENT.getDropRate("T2")
            "1/100k" -> Dye.PEARLESCENT.getDropRate("T3")
            else -> 0f
        }
        val killsPerHour = context.getFloat("Kills per hour")
        val magicFind = context.getFloat("Magic Find")

        if (killsPerHour == 0f || baseChance == 0f) {
            return "Invalid Input"
        }
        val result = baseChance / killsPerHour / (1 + magicFind / 100) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}