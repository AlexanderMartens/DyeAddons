package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class TentacleCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Tentacle Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Kuudra Tier" to DropDownCalcWidget(x, y, width, 25, Component.literal("Kuudra Tier"), listOf("Basic", "Hot", "Burning", "Fiery", "Infernal")),
        "Runs per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Runs per hour"), Parsers.FLOAT),
        "Kismet" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Kismet")))
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val baseChance = when (context.getString("Kuudra Tier")) {
            "Basic" -> 100_000f
            "Hot" -> 80_000f
            "Burning" -> 60_000f
            "Fiery" -> 40_000f
            "Infernal" -> 20_000f
            else -> 0f
        }
        val runsPerHour = context.getFloat("Runs per hour")
        val kismet = context.getBoolean("Kismet")

        if (baseChance == 0f || runsPerHour == 0f) {
            return "Invalid Input"
        }
        val result = baseChance / runsPerHour / vincent / if (kismet) 2f else 1f
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}