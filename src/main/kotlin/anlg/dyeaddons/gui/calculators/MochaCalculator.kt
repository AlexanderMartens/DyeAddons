package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class MochaCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Mocha Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Potion Level" to DropDownCalcWidget(x, y, width, 25, Component.literal("Potion Level"), listOf("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8"), "T7"),
        "Potions per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Potions per hour"), Parsers.FLOAT),
        "Cost per three Potions" to EditTextCalcWidget(x, y, width, 25, Component.literal("Cost per three Potions"), Parsers.FLOAT))
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val baseChance = when (context.getString("Potion Level")) {
            "T1" -> 100_000_000f
            "T2" -> 5_000_000f
            "T3" -> 2_500_000f
            "T4" -> 1_000_000f
            "T5" -> 750_000f
            "T6" -> 500_000f
            "T7" -> 250_000f
            "T8" -> 100_000f
            else -> 0f
        }
        val potionsPerHour = context.getFloat("Potions per hour")
        val cost = context.getFloat("Cost per three Potions") / 3f

        if (baseChance == 0f || potionsPerHour == 0f) {
            return "Invalid Input"
        }
        val result = baseChance / potionsPerHour / vincent
        return DecimalFormat("#,###.##").format(result) + " hours" +
                if (cost != 0f) {
                    " " + DecimalFormat("#,###.##").format(baseChance * cost / vincent) + " coins"
                } else ""
    }
}