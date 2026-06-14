package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class DungStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Dung Dye"),
    listOf(
        StatisticField("Pest Kills", Parsers.INT),
        StatisticField("Elusive Pest Kills", Parsers.INT),
        StatisticField("Overbloom", Parsers.FLOAT)),
    Dye.DUNG
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val pests = context.getInt("Pest Kills")
        val elusivePests = context.getInt("Elusive Pest Kills")
        val overbloom = context.getFloat("Overbloom")

        val result = (pests / 250_000.0 + elusivePests / 50_000.0) *
                (1.0 + overbloom / 100.0)
        return result
    }
}