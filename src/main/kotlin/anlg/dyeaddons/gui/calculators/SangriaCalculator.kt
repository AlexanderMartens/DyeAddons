package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class SangriaCalculator(
    x : Int,
    y : Int,
    width : Int,
    height : Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Sangria Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Boss Tier" to DropDownCalcWidget(x, y, width, 25, Component.literal("Boss Tier"), listOf("Tier 1", "Tier 2", "Tier 3", "Tier 4", "Tier 5")),
        "Bosses per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Bosses per hour"), Parsers.FLOAT),
        "Full Meter" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Full Meter")),
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
        val tier = context.getString("Boss Tier")
        val bossesPerHour = context.getFloat("Bosses per hour")
        val fullMeter = context.getBoolean("Full Meter")

        val (motesPerBoss, xpPerBoss, oddsPerBoss) = when(tier) {
            "Tier 1" -> Triple(2000f, 10f, 100_000f)
            "Tier 2" -> Triple(4000f, 25f, 80_000f)
            "Tier 3" -> Triple(5000f, 60f, 60_000f)
            "Tier 4" -> Triple(7000f, 120f, 40_000f)
            "Tier 5" -> Triple(10000f, 150f, 10_000f)
            else -> Triple(0f, 0f, 0f)
        }

        if (bossesPerHour == 0f || motesPerBoss == 0f) {
            return "Invalid Input"
        }

        val motes = if (fullMeter) {
            750_000f / xpPerBoss * motesPerBoss
        } else {
            oddsPerBoss * motesPerBoss / vincent
        }

        val result = if (fullMeter) {
            750_000f / (bossesPerHour * xpPerBoss)
        } else {
            (oddsPerBoss / (bossesPerHour)) / vincent
        }
        return DecimalFormat("#,###.##").format(result) + " hours and ${DecimalFormat("#,###.##").format(motes)} motes"
    }
}