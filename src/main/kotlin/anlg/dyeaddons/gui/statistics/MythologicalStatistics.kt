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
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        val t1Kills = playerStats?.sumOfKills(listOf("minos_hunter", "siamese_lynx", "stranded_nymph", "cretan_bull")) ?: 0
        val t2Kills = playerStats?.sumOfKills(listOf("harpy", "gaia_construct")) ?: 0
        val t3Kills = playerStats?.sumOfKills(listOf("minotaur", "minos_champion")) ?: 0
        val t4Kills = playerStats?.sumOfKills(listOf("sphinx", "minos_inquisitor")) ?: 0
        val t5Kills = playerStats?.sumOfKills(listOf("king_minos", "manticore")) ?: 0

        (this.widgets["Common/Uncommon Mythological Creature Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["Rare Mythological Creature Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["Epic Mythological Creature Kills"]?.widget as EditBox).value = t3Kills.toString()
        (this.widgets["Legendary Mythological Creature Kills"]?.widget as EditBox).value = t4Kills.toString()
        (this.widgets["Mythic Mythological Creature Kills"]?.widget as EditBox).value = t5Kills.toString()
    }

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