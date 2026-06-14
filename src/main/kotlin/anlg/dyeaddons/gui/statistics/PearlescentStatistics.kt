package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class PearlescentStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Pearlescent Dye"),
    listOf(
        StatisticField("1/10m Mob Kills", Parsers.INT),
        StatisticField("1/5m Mob Kills", Parsers.INT),
        StatisticField("1/100k Mob Kills", Parsers.INT),
        StatisticField("Boss Kills", Parsers.INT),
        StatisticField("Magic Find", Parsers.FLOAT),
        StatisticField("Looting", Parsers.INT)),
    Dye.PEARLESCENT
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getInt("1/10m Mob Kills")
        val t2Kills = context.getInt("1/5m Mob Kills")
        val t3Kills = context.getInt("1/100k Mob Kills")
        val bossKills = context.getInt("Boss Kills")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")

        val result = (t1Kills / 10_000_000.0 + t2Kills / 5_000_000.0 + t3Kills / 100_000.0) *
                (1.0 + magicFind / 100.0) *
                (1.0 + looting * 0.15) +
                bossKills / 100_000.0
        return result
    }
}