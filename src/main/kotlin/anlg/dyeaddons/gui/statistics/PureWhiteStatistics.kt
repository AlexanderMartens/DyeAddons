package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class PureWhiteStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Pure White Dye"),
    listOf(
        StatisticField("Bits", Parsers.INT),),
    Dye.PURE_WHITE
) {
    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val bits = context.getInt("Bits")

        val result = bits / 250_000.0
        return result
    }
}