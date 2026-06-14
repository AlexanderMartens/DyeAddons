package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class FossilStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Fossil Dye"),
    listOf(
        StatisticField("Suspicious Scrap Excavated", Parsers.INT),
        StatisticField("Prehistorian Perk Level", Parsers.INT),
        StatisticField("At least one citrine in chisel", Parsers.BOOL),),
    Dye.FOSSIL
) {
    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val scraps = context.getInt("Suspicious Scrap Excavated")
        val prehistorian = context.getInt("Prehistorian Perk Level")
        val citrine = context.getBoolean("At least one citrine in chisel")

        val dyeRollsPerScrap = if (citrine) {
            11.5
        } else {
            24.0 / 54.0 * 11.5
        }

        val result = scraps * dyeRollsPerScrap / 500_000.0 * (1.0 + prehistorian / 100.0)
        return result
    }
}