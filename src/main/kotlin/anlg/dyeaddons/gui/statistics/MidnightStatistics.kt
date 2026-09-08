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
        StatisticField("Common/Uncommon/Rare/Epic Sea Creature Kills", Parsers.INT, true),
        StatisticField("Legendary/Mythic Sea Creature Kills", Parsers.INT, true),
        StatisticField("Spooky Mob Kills", Parsers.INT, true),
        StatisticField("Headless Horseman Kills", Parsers.INT, true),
        StatisticField("Magic Find on Common-Epic", Parsers.FLOAT),
        StatisticField("Looting on Common-Epic", Parsers.INT),
        StatisticField("Magic Find on Legendary-Mythic", Parsers.FLOAT),
        StatisticField("Looting on Legendary-Mythic", Parsers.INT),
        StatisticField("Magic Find on Horseman", Parsers.FLOAT),
        StatisticField("Looting on Horseman", Parsers.INT),),
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

        val t1SeaCreatureKills = context.getMultipliedInt("Common/Uncommon/Rare/Epic Sea Creature Kills")
        val t3SeaCreatureKills = context.getMultipliedInt("Legendary/Mythic Sea Creature Kills")
        val spookyKills = context.getMultipliedInt("Spooky Mob Kills")
        val headlessKills = context.getMultipliedInt("Headless Horseman Kills")
        val magicFindT1 = context.getFloat("Magic Find on Common-Epic", stats?.get("Magic Find")?.asFloat() ?: 0f)
        val lootingT1 = context.getInt("Looting on Common-Epic", stats?.get("Looting")?.asInt() ?: 0)
        val magicFindT2 = context.getFloat("Magic Find on Legendary-Mythic", stats?.get("Magic Find")?.asFloat() ?: 0f)
        val lootingT2 = context.getInt("Looting on Legendary-Mythic", stats?.get("Looting")?.asInt() ?: 0)
        val magicFindHorseman = context.getFloat("Magic Find on Horseman", stats?.get("Magic Find")?.asFloat() ?: 0f)
        val lootingHorseman = context.getInt("Looting on Horseman", stats?.get("Looting")?.asInt() ?: 0)

        val result = (t1SeaCreatureKills / 1_000_000.0 + spookyKills / 500_000.0)  * (1.0 + magicFindT1 / 100.0) * (1.0 + lootingT1 * 0.15) +
                (t3SeaCreatureKills / 50_000.0) * (1.0 + magicFindT2 / 100.0) * (1.0 + lootingT2 * 0.15) +
                (headlessKills / 50_000.0) * (1.0 + magicFindHorseman / 100.0) * (1.0 + lootingHorseman * 0.15)
        return result
    }
}