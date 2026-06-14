package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class NyanzaStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Nyanza Dye"),
    listOf(
        StatisticField("Mining Commissions Completed", Parsers.INT),),
    Dye.NYANZA
) {
    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val commissions = context.getInt("Mining Commissions Completed")

        val result = commissions / 250_000.0
        return result
    }
}