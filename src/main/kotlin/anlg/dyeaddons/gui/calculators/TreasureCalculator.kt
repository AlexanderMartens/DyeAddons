package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.calc.TreasureTable
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class TreasureCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Treasure Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Fishing Catches per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Fishing Catches per hour"), Parsers.FLOAT),
        "Treasure Chance" to EditTextCalcWidget(x, y, width, 25, Component.literal("Treasure Chance"), Parsers.FLOAT),
        "Hermit Crab Buff" to EditTextCalcWidget(x, y, width, 25, Component.literal("Hermit Crab Buff"), Parsers.FLOAT),
        "Blessing Level" to EditTextCalcWidget(x, y, width, 25, Component.literal("Blessing Level"), Parsers.INT),
        "Blessed Bait" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Blessed Bait"))
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
        val catchesPerHour = context.getFloat("Fishing Catches per hour")
        val treasureChance = context.getFloat("Treasure Chance")
        val hermitCrabBuff = context.getFloat("Hermit Crab Buff")
        val blessingLevel = context.getInt("Blessing Level")
        val blessedBait = context.getBoolean("Blessed Bait")

        if (catchesPerHour == 0f || treasureChance == 0f) {
            return "Invalid Input"
        }

        val result = TreasureTable(hermitCrabBuff, blessingLevel, blessedBait).getAverageDropChance() / catchesPerHour / (treasureChance / 100) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}