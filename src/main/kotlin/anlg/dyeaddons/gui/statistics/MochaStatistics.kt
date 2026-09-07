package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class MochaStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Mocha Dye"),
    listOf(
        StatisticField("T1 Potions Brewed", Parsers.INT, true),
        StatisticField("T2 Potions Brewed", Parsers.INT, true),
        StatisticField("T3 Potions Brewed", Parsers.INT, true),
        StatisticField("T4 Potions Brewed", Parsers.INT, true),
        StatisticField("T5 Potions Brewed", Parsers.INT, true),
        StatisticField("T6 Potions Brewed", Parsers.INT, true),
        StatisticField("T7 Potions Brewed", Parsers.INT, true),
        StatisticField("T8 Potions Brewed", Parsers.INT, true)),
    Dye.MOCHA
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Potions = context.getMultipliedInt("T1 Potions Brewed")
        val t2Potions = context.getMultipliedInt("T2 Potions Brewed")
        val t3Potions = context.getMultipliedInt("T3 Potions Brewed")
        val t4Potions = context.getMultipliedInt("T4 Potions Brewed")
        val t5Potions = context.getMultipliedInt("T5 Potions Brewed")
        val t6Potions = context.getMultipliedInt("T6 Potions Brewed")
        val t7Potions = context.getMultipliedInt("T7 Potions Brewed")
        val t8Potions = context.getMultipliedInt("T8 Potions Brewed")

        val result = t1Potions / 100_000_000.0 +
                t2Potions / 5_000_000.0 +
                t3Potions / 2_500_000.0 +
                t4Potions / 1_000_000.0 +
                t5Potions / 750_000.0 +
                t6Potions / 500_000.0 +
                t7Potions / 250_000.0 +
                t8Potions / 100_000.0
        return result
    }
}