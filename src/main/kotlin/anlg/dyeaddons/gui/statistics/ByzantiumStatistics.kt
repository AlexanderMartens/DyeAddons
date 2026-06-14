package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class ByzantiumStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Byzantium Dye"),
    listOf(
        StatisticField("T1 Voidgloom Seraph Kills", Parsers.INT),
        StatisticField("T2 Voidgloom Seraph Kills", Parsers.INT),
        StatisticField("T3 Voidgloom Seraph Kills", Parsers.INT),
        StatisticField("T4 Voidgloom Seraph Kills", Parsers.INT)),
    Dye.BYZANTIUM
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getInt("T1 Voidgloom Seraph Kills")
        val t2Kills = context.getInt("T2 Voidgloom Seraph Kills")
        val t3Kills = context.getInt("T3 Voidgloom Seraph Kills")
        val t4Kills = context.getInt("T4 Voidgloom Seraph Kills")

        val result = t1Kills / 10_000_000.0 +
                t2Kills / 2_500_000.0 +
                t3Kills / 1_000_000.0 +
                t4Kills / 500_000.0
        return result
    }
}