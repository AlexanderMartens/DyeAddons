package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class SangriaStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Sangria Dye"),
    listOf(
        StatisticField("T1 Riftstalker Bloodfiend Kills", Parsers.INT),
        StatisticField("T2 Riftstalker Bloodfiend Kills", Parsers.INT),
        StatisticField("T3 Riftstalker Bloodfiend Kills", Parsers.INT),
        StatisticField("T4 Riftstalker Bloodfiend Kills", Parsers.INT),
        StatisticField("T5 Riftstalker Bloodfiend Kills", Parsers.INT)),
    Dye.SANGRIA
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getInt("T1 Riftstalker Bloodfiend Kills")
        val t2Kills = context.getInt("T2 Riftstalker Bloodfiend Kills")
        val t3Kills = context.getInt("T3 Riftstalker Bloodfiend Kills")
        val t4Kills = context.getInt("T4 Riftstalker Bloodfiend Kills")
        val t5Kills = context.getInt("T5 Riftstalker Bloodfiend Kills")

        val result = t1Kills / 100_000.0 +
                t2Kills / 80_000.0 +
                t3Kills / 60_000.0 +
                t4Kills / 40_000.0 +
                t5Kills / 10_000.0
        return result
    }
}