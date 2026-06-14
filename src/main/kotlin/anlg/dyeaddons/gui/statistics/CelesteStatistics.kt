package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class CelesteStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Celeste Dye"),
    listOf(
        StatisticField("T1 Sven Packmaster Kills", Parsers.INT),
        StatisticField("T2 Sven Packmaster Kills", Parsers.INT),
        StatisticField("T3 Sven Packmaster Kills", Parsers.INT),
        StatisticField("T4 Sven Packmaster Kills", Parsers.INT)),
    Dye.CELESTE
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getInt("T1 Sven Packmaster Kills")
        val t2Kills = context.getInt("T2 Sven Packmaster Kills")
        val t3Kills = context.getInt("T3 Sven Packmaster Kills")
        val t4Kills = context.getInt("T4 Sven Packmaster Kills")

        val result = t1Kills / 10_000_000.0 +
                t2Kills / 2_500_000.0 +
                t3Kills / 1_000_000.0 +
                t4Kills / 500_000.0
        return result
    }
}