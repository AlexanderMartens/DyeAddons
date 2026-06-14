package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class MythologicalStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Mythological Dye"),
    listOf(
        StatisticField("Common/Uncommon Mythological Creature Kills", Parsers.INT),
        StatisticField("Rare Mythological Creature Kills", Parsers.INT),
        StatisticField("Epic Mythological Creature Kills", Parsers.INT),
        StatisticField("Legendary Mythological Creature Kills", Parsers.INT),
        StatisticField("Mythic Mythological Creature Kills", Parsers.INT),
        StatisticField("Magic Find", Parsers.FLOAT),
        StatisticField("Looting", Parsers.INT)),
    Dye.MYTHOLOGICAL
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1MythoCreatureKills = context.getInt("Common/Uncommon Mythological Creature Kills")
        val t2MythoCreatureKills = context.getInt("Rare Mythological Creature Kills")
        val t3MythoCreatureKills = context.getInt("Epic Mythological Creature Kills")
        val t4MythoCreatureKills = context.getInt("Legendary Mythological Creature Kills")
        val t5MythoCreatureKills = context.getInt("Mythic Mythological Creature Kills")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")

        val result = (t1MythoCreatureKills / 1_000_000.0 +
                t2MythoCreatureKills / 500_000.0 +
                t3MythoCreatureKills / 250_000.0 +
                t4MythoCreatureKills / 50_000.0 +
                t5MythoCreatureKills / 10_000.0) *
                (1.0 + magicFind / 100.0) *
                (1.0 + looting * 0.15)
        return result
    }
}