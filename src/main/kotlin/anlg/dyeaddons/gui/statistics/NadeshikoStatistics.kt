package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class NadeshikoStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Nadeshiko Dye"),
    listOf(
        StatisticField("Supreme Superpairs Experiments", Parsers.INT),
        StatisticField("Transcendent Superpairs Experiments", Parsers.INT),
        StatisticField("Metaphysical Superpairs Experiments", Parsers.INT)),
    Dye.NADESHIKO
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Experiment = context.getInt("Supreme Superpairs Experiments")
        val t2Experiment = context.getInt("Transcendent Superpairs Experiments")
        val t3Experiment = context.getInt("Metaphysical Superpairs Experiments")

        val result = t1Experiment / 75_000.0 +
                t2Experiment / 50_000.0 +
                t3Experiment / 25_000.0
        return result
    }
}