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
        StatisticField("Mining Commissions Completed", Parsers.INT, true)),
    Dye.NYANZA
) {
    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val commissions = context.getMultipliedInt("Mining Commissions Completed")
        val legacyCommissions = context.getMultipliedInt("Mining Commissions Completed", legacy = true)

        val currentProgress = commissions / dye.getDropRate("Commission")
        val legacyProgress = legacyCommissions / 250_000.0

        return currentProgress + legacyProgress
    }
}