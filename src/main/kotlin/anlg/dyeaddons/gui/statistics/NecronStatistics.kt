package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class NecronStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Necron Dye"),
    listOf(
        StatisticField("Master Mode Floor 7 S+ Completions", Parsers.INT),
        StatisticField("Kismet Feathers used on Bedrock Chests", Parsers.INT)),
    Dye.NECRON
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val runs = context.getInt("Master Mode Floor 7 S+ Completions")
        val kismets = context.getInt("Kismet Feathers used on Bedrock Chests")

        val result = (runs + kismets) / 2_500.0
        return result
    }
}