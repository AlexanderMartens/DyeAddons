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
        StatisticField("Dark Auction Items Seen", Parsers.INT),),
    Dye.DARK_PURPLE
) {
    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val darkAuctionItems = context.getInt("Dark Auction Items Seen")

        val result = darkAuctionItems / 400.0
        return result
    }
}