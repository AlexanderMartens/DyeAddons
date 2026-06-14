package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class DarkPurpleStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Dark Purple Dye"),
    listOf(
        StatisticField("Dark Auctions Attended", Parsers.INT),),
    Dye.DARK_PURPLE
) {
    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val darkAuctions = context.getInt("Dark Auctions Attended")

        val result = darkAuctions / 400.0 * 3.0
        return result
    }
}