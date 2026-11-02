package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Parsers
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
        "Hermit Crab Level" to EditTextCalcWidget(x, y, width, 25, Component.literal("Hermit Crab Level"), Parsers.INT),
        "Blessing Level" to EditTextCalcWidget(x, y, width, 25, Component.literal("Blessing Level"), Parsers.INT),
        "Blessed Bait" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Blessed Bait")),
        "Junk Fishing" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Junk Fishing")),
        "Mantis Shrimp Level" to EditTextCalcWidget(x, y, width, 25, Component.literal("Mantis Shrimp Level"), Parsers.INT),
        "Blue Crab Level" to EditTextCalcWidget(x, y, width, 25, Component.literal("Blue Crab Level"), Parsers.INT),
        "Litterbug Level" to EditTextCalcWidget(x, y, width, 25, Component.literal("Litterbug Level"), Parsers.INT),
        "Maggot Level" to EditTextCalcWidget(x, y, width, 25, Component.literal("Maggot Level"), Parsers.INT),
        "Lucky Reforge" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Lucky Reforge")),
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
        val hermitCrab = context.getInt("Hermit Crab Level")
        val blessingLevel = context.getInt("Blessing Level")
        val mantisShrimp = context.getInt("Mantis Shrimp Level")
        val blueCrab = context.getInt("Blue Crab Level")
        val litterbug = context.getInt("Litterbug Level")
        val maggot = context.getInt("Maggot Level")
        val blessedBait = context.getBoolean("Blessed Bait")
        val junkFishing = context.getBoolean("Junk Fishing")
        val luckyReforge = context.getBoolean("Lucky Reforge")

        if (catchesPerHour == 0f || treasureChance == 0f) {
            return "Invalid Input"
        }

        var greatBuff = 1f
        greatBuff += hermitCrab / 1_000f
        greatBuff += blessingLevel / 50f
        if (blessedBait) greatBuff += 0.5f
        greatBuff += mantisShrimp / 100f
        if (junkFishing) greatBuff += litterbug / 100f

        var outstandingBuff = 1f
        outstandingBuff += hermitCrab / 1_000f
        outstandingBuff += blessingLevel / 50f
        if (blessedBait) outstandingBuff += 0.5f
        outstandingBuff += blueCrab / 100f
        if (junkFishing) outstandingBuff += maggot / 100f
        if (luckyReforge) outstandingBuff += 0.07f

        val result = TreasureTable(greatBuff, outstandingBuff).getAverageDropChance() / catchesPerHour / (treasureChance / 100) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}