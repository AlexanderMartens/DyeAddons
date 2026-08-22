package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.api.sumOfKills
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class IcebergStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Iceberg Dye"),
    listOf(
        StatisticField("Common/Uncommon Sea Creature Kills", Parsers.INT),
        StatisticField("Rare/Epic Sea Creature Kills", Parsers.INT),
        StatisticField("Legendary/Mythic Sea Creature Kills", Parsers.INT),
        StatisticField("Magic Find on Common-Epic", Parsers.FLOAT),
        StatisticField("Looting on Common-Epic", Parsers.INT),
        StatisticField("Magic Find on Legendary-Mythic", Parsers.FLOAT),
        StatisticField("Looting on Legendary-Mythic", Parsers.INT)),
    Dye.ICEBERG
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        val t1Kills = playerStats?.sumOfKills(listOf("frozen_steve", "frosty_the_snowman")) ?: 0
        val t2Kills = playerStats?.sumOfKills(listOf("grinch", "nutcracker")) ?: 0
        val t3Kills = playerStats?.sumOfKills(listOf("yeti", "reindrake")) ?: 0

        (this.widgets["Common/Uncommon Sea Creature Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["Rare/Epic Sea Creature Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["Legendary/Mythic Sea Creature Kills"]?.widget as EditBox).value = t3Kills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1SeaCreatureKills = context.getInt("Common/Uncommon Sea Creature Kills")
        val t2SeaCreatureKills = context.getInt("Rare/Epic Sea Creature Kills")
        val t3SeaCreatureKills = context.getInt("Legendary/Mythic Sea Creature Kills")
        val magicFindT1 = context.getFloat("Magic Find on Common-Epic")
        val lootingT1 = context.getInt("Looting on Common-Epic")
        val magicFindT2 = context.getFloat("Magic Find on Legendary-Mythic")
        val lootingT2 = context.getInt("Looting on Legendary-Mythic")

        val result = (t1SeaCreatureKills / 2_500_000.0 + t2SeaCreatureKills / 500_000.0) * (1.0 + magicFindT1 / 100.0) * (1.0 + lootingT1 * 0.15) +
                (t3SeaCreatureKills / 50_000.0) * (1.0 + magicFindT2 / 100.0) * (1.0 + lootingT2 * 0.15)
        return result
    }
}