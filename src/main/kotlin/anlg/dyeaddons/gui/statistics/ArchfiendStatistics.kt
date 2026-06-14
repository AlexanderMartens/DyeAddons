package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class ArchfiendStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Archfiend Dye"),
    listOf(
        StatisticField("Archfiend Dice Rolls", Parsers.INT),
        StatisticField("High Class Archfiend Dice Rolls", Parsers.INT)),
    Dye.ARCHFIEND
) {
    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val archfiendRolls = context.getInt("Archfiend Dice Rolls")
        val highClassRolls = context.getInt("High Class Archfiend Dice Rolls")

        val result = archfiendRolls / 6_666.0 + highClassRolls / 666.0
        return result
    }
}