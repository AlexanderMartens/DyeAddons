package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class TreasureStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Treasure Dye"),
    listOf(
        StatisticField("Good Treasure Catches", Parsers.INT),
        StatisticField("Great Treasure Catches", Parsers.INT),
        StatisticField("Outstanding Treasure Catches", Parsers.INT)),
    Dye.TREASURE
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val goodCatches = context.getInt("Good Treasure Catches")
        val greatCatches = context.getInt("Great Treasure Catches")
        val outstandingCatches = context.getInt("Outstanding Treasure Catches")

        val result = goodCatches / 1_000_000.0 +
                greatCatches / 100_000.0 +
                outstandingCatches / 10_000.0
        return result
    }
}