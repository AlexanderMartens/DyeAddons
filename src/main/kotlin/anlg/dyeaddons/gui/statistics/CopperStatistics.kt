package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class CopperStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Copper Dye"),
    listOf(
        StatisticField("Uncommon Visitor Visits", Parsers.INT),
        StatisticField("Rare Visitor Visits", Parsers.INT),
        StatisticField("Legendary Visitor Visits", Parsers.INT),
        StatisticField("Mythic Visitor Visits", Parsers.INT),
        StatisticField("Special Visitor Visits", Parsers.INT)),
    Dye.COPPER
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val uncommonVisits = context.getInt("Uncommon Visitor Visits")
        val rareVisits = context.getInt("Rare Visitor Visits")
        val legendaryVisits = context.getInt("Legendary Visitor Visits")
        val mythicVisits = context.getInt("Mythic Visitor Visits")
        val specialVisits = context.getInt("Special Visitor Visits")

        val result = uncommonVisits / 100_000.0 +
                rareVisits / 50_000.0 +
                legendaryVisits / 25_000.0 +
                mythicVisits / 5_000.0 +
                specialVisits / 500.0
        return result
    }
}