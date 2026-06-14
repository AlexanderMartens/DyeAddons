package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class HollyStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Holly Dye"),
    listOf(
        StatisticField("Red Gifts given/opened", Parsers.INT),),
    Dye.HOLLY
) {
    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val gifts = context.getInt("Red Gifts given/opened")

        val result = gifts / 8_000.0
        return result
    }
}