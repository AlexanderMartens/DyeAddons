package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class CyclamenStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Cyclamen Dye"),
    listOf(
        StatisticField("1/10m Mob Kills", Parsers.INT),
        StatisticField("1/2.5m Mob Kills", Parsers.INT),
        StatisticField("1/250k Mob Kills", Parsers.INT),
        StatisticField("Miniboss Kills", Parsers.INT),
        StatisticField("Magic Find", Parsers.FLOAT),
        StatisticField("Looting", Parsers.INT)),
    Dye.CYCLAMEN
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getInt("1/10m Mob Kills")
        val t2Kills = context.getInt("1/2.5m Mob Kills")
        val t3Kills = context.getInt("1/250k Mob Kills")
        val miniKills = context.getInt("Miniboss Kills")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")

        val result = (t1Kills / 10_000_000.0 + t2Kills / 2_500_000.0 + t3Kills / 250_000.0) *
                (1.0 + magicFind / 100.0) *
                (1.0 + looting * 0.15) +
                miniKills / 250_000.0
        return result
    }
}