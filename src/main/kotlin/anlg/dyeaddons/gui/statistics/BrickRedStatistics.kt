package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class BrickRedStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Brick Red Dye"),
    listOf(
        StatisticField("T1 Tarantula Broodfather Kills", Parsers.INT),
        StatisticField("T2 Tarantula Broodfather Kills", Parsers.INT),
        StatisticField("T3 Tarantula Broodfather Kills", Parsers.INT),
        StatisticField("T4 Tarantula Broodfather Kills", Parsers.INT),
        StatisticField("T5 Tarantula Broodfather Kills", Parsers.INT)),
    Dye.BRICK_RED
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getInt("T1 Tarantula Broodfather Kills")
        val t2Kills = context.getInt("T2 Tarantula Broodfather Kills")
        val t3Kills = context.getInt("T3 Tarantula Broodfather Kills")
        val t4Kills = context.getInt("T4 Tarantula Broodfather Kills")
        val t5Kills = context.getInt("T5 Tarantula Broodfather Kills")

        val result = t1Kills / 10_000_000.0 +
                t2Kills / 2_500_000.0 +
                t3Kills / 1_000_000.0 +
                t4Kills / 500_000.0 +
                t5Kills / 250_000.0
        return result
    }
}