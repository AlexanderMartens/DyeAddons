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

class MidnightStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Midnight Dye"),
    listOf(
        StatisticField("Common/Uncommon/Rare/Epic Sea Creature Kills", Parsers.INT),
        StatisticField("Legendary/Mythic Sea Creature Kills", Parsers.INT),
        StatisticField("Spooky Mob Kills", Parsers.INT),
        StatisticField("Headless Horseman Kills", Parsers.INT),
        StatisticField("Magic Find", Parsers.FLOAT),
        StatisticField("Looting", Parsers.INT)),
    Dye.MIDNIGHT
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        // sea guardian is jumping jack (also includes sea guardian kills, but i cant tell that apart)
        val t1Kills = playerStats?.sumOfKills(listOf("sea_guardian", "scarecrow", "nightmare", "werewolf")) ?: 0
        val t2Kills = playerStats?.sumOfKills(listOf("phantom_fisherman", "grim_reaper")) ?: 0
        val spookyKills = playerStats?.sumOfKills(
            listOf("bat_pinata", "mega_bat", "trick_or_treater", "wither_gourd", "phantom_spirit", "scary_jerry", "wraith", "batty_witch")
        ) ?: 0
        val headlessKills = playerStats?.sumOfKills(listOf("headless_horseman", "horseman_horse")) ?: 0

        (this.widgets["Common/Uncommon/Rare/Epic Sea Creature Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["Legendary/Mythic Sea Creature Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["Spooky Mob Kills"]?.widget as EditBox).value = spookyKills.toString()
        (this.widgets["Headless Horseman Kills"]?.widget as EditBox).value = headlessKills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1SeaCreatureKills = context.getInt("Common/Uncommon/Rare/Epic Sea Creature Kills")
        val t3SeaCreatureKills = context.getInt("Legendary/Mythic Sea Creature Kills")
        val spookyKills = context.getInt("Spooky Mob Kills")
        val headlessKills = context.getInt("Headless Horseman Kills")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")

        val result = (t1SeaCreatureKills / 1_000_000.0 + t3SeaCreatureKills / 50_000.0 + spookyKills / 500_000.0 + headlessKills / 50_000.0) *
                (1.0 + magicFind / 100.0) *
                (1.0 + looting * 0.15)
        return result
    }
}