package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class TentacleStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Tentacle Dye"),
    listOf(
        StatisticField("Basic Kuudra Completions + Kismets Used", Parsers.INT),
        StatisticField("Hot Kuudra Completions + Kismets Used", Parsers.INT),
        StatisticField("Burning Kuudra Completions + Kismets Used", Parsers.INT),
        StatisticField("Fiery Kuudra Completions + Kismets Used", Parsers.INT),
        StatisticField("Infernal Kuudra Completions + Kismets Used", Parsers.INT)),
    Dye.TENTACLE
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val basic = context.getInt("Basic Kuudra Completions + Kismets Used")
        val hot = context.getInt("Hot Kuudra Completions + Kismets Used")
        val burning = context.getInt("Burning Kuudra Completions + Kismets Used")
        val fiery = context.getInt("Fiery Kuudra Completions + Kismets Used")
        val infernal = context.getInt("Infernal Kuudra Completions + Kismets Used")

        val result = basic / 100_000.0 +
                hot / 80_000.0 +
                burning / 60_000.0 +
                fiery / 40_000.0 +
                infernal / 20_000.0
        return result
    }
}