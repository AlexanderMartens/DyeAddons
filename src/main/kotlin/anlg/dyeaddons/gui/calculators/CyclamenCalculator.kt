package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class CyclamenCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Cyclamen Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Base Chance" to DropDownCalcWidget(x, y, width, 25, Component.literal("Tier of Mob"), listOf("1/10m", "1/2.5m", "1/250k")),
        "Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Kills per hour"), Parsers.FLOAT),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT),
        "Looting" to EditTextCalcWidget(x, y, width, 25, Component.literal("Looting"), Parsers.INT)),
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
            "1/10m" -> 10_000_000f
            "1/2.5m" -> 2_500_000f
            "1/250k" -> 250_000f
            else -> 0f
        }
        val killsPerHour = context.getFloat("Kills per hour")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")

        if (killsPerHour == 0f || baseChance == 0f) {
            return "Invalid Input"
        }
        val result = baseChance / killsPerHour / (1 + magicFind / 100) / (1 + looting * 0.15f) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}