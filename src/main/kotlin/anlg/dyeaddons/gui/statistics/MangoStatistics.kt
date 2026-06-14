package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class MangoStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Mango Dye"),
    listOf(
        StatisticField("Log Collection", Parsers.INT),
        StatisticField("Foraging Fortune", Parsers.FLOAT)),
    Dye.MANGO
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val logs = context.getInt("Log Collection")
        val foragingFortune = context.getFloat("Foraging Fortune")

        val result = (logs / 10_000_000.0) /
                (1.0 + foragingFortune / 100.0)
        return result
    }
}