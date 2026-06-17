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

class CarmineStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Carmine Dye"),
    listOf(
        StatisticField("Common/Uncommon Sea Creature Kills", Parsers.INT),
        StatisticField("Rare/Epic Sea Creature Kills", Parsers.INT),
        StatisticField("Legendary/Mythic Sea Creature Kills", Parsers.INT),
        StatisticField("Magic Find", Parsers.FLOAT),
        StatisticField("Looting", Parsers.INT)),
    Dye.CARMINE
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        val t1Kills = playerStats?.sumOfKills(listOf(
            "magma_slug",
            "baby_magma_slug",
            "moogma",
            "fried_chicken",
            "volcanic_snail"
        )) ?: 0
        val t2Kills = playerStats?.sumOfKills(listOf(
            "lava_leech",
            "pyroclastic_worm",
            "lava_flame",
            "fire_eel",
            "pig_rider",
            "lava_pigman",
            "lava_blaze",
            "flaming_worm",
            "fireproof_witch",
            "magma_pillar",
        )) ?: 0
        val t3Kills = playerStats?.sumOfKills(listOf(
            "thunder",
            "lord_jawbus",
            "fiery_scuttler",
            "ragnarok",
            "pond_squid_300",
        )) ?: 0

        (this.widgets["Common/Uncommon Sea Creature Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["Rare/Epic Sea Creature Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["Legendary/Mythic Sea Creature Kills"]?.widget as EditBox).value = t3Kills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1SeaCreatureKills = context.getInt("Common/Uncommon Sea Creature Kills")
        val t2SeaCreatureKills = context.getInt("Rare/Epic Sea Creature Kills")
        val t3SeaCreatureKills = context.getInt("Legendary/Mythic Sea Creature Kills")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")

        val result = (t1SeaCreatureKills / 5_000_000.0 + t2SeaCreatureKills / 2_500_000.0 + t3SeaCreatureKills / 50_000.0) *
                (1.0 + magicFind / 100.0) *
                (1.0 + looting * 0.15)
        return result
    }
}