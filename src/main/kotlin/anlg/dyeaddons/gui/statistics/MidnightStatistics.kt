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
        StatisticField("Common Sea Creatures", Parsers.INT, true),
        StatisticField("Uncommon Sea Creatures", Parsers.INT, true),
        StatisticField("Rare Sea Creatures", Parsers.INT, true),
        StatisticField("Epic Sea Creatures", Parsers.INT, true),
        StatisticField("Legendary Sea Creatures", Parsers.INT, true),
        StatisticField("Mythic Sea Creatures", Parsers.INT, true),
        StatisticField("Spooky Mob Kills", Parsers.INT, true),
        StatisticField("Bat Pinata Kills", Parsers.INT, true),
        StatisticField("Headless Horseman Kills", Parsers.INT, true),
        StatisticField("Magic Find", Parsers.FLOAT)),
    Dye.MIDNIGHT
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        // sea guardian is jumping jack (also includes sea guardian kills, but i cant tell that apart)
        val common = playerStats?.sumOfKills(listOf("sea_guardian",)) ?: 0
        val uncommon = playerStats?.sumOfKills(listOf("scarecrow")) ?: 0
        val rare = playerStats?.sumOfKills(listOf("nightmare")) ?: 0
        val epic = playerStats?.sumOfKills(listOf("werewolf")) ?: 0
        val legendary = playerStats?.sumOfKills(listOf("phantom_fisherman")) ?: 0
        val mythic = playerStats?.sumOfKills(listOf("grim_reaper")) ?: 0
        val spookyKills = playerStats?.sumOfKills(
            listOf("trick_or_treater", "wither_gourd", "phantom_spirit", "scary_jerry", "wraith", "batty_witch")
        ) ?: 0
        val batKills = playerStats?.sumOfKills(listOf("bat_pinata", "mega_bat")) ?: 0
        val headlessKills = playerStats?.sumOfKills(listOf("headless_horseman", "horseman_horse")) ?: 0

        (this.widgets["Common Sea Creatures"]?.widget as EditBox).value = common.toString()
        (this.widgets["Uncommon Sea Creatures"]?.widget as EditBox).value = uncommon.toString()
        (this.widgets["Rare Sea Creatures"]?.widget as EditBox).value = rare.toString()
        (this.widgets["Epic Sea Creatures"]?.widget as EditBox).value = epic.toString()
        (this.widgets["Legendary Sea Creatures"]?.widget as EditBox).value = legendary.toString()
        (this.widgets["Mythic Sea Creatures"]?.widget as EditBox).value = mythic.toString()
        (this.widgets["Spooky Mob Kills"]?.widget as EditBox).value = spookyKills.toString()
        (this.widgets["Bat Pinata Kills"]?.widget as EditBox).value = batKills.toString()
        (this.widgets["Headless Horseman Kills"]?.widget as EditBox).value = headlessKills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1SeaCreatureKills = context.getMultipliedInt("Common/Uncommon/Rare/Epic Sea Creature Kills", legacy = true)
        val t3SeaCreatureKills = context.getMultipliedInt("Legendary/Mythic Sea Creature Kills", legacy = true)
        val legacySpookyKills = context.getMultipliedInt("Spooky Mob Kills", legacy = true)
        val legacyHeadlessKills = context.getMultipliedInt("Headless Horseman Kills", legacy = true)
        val magicFindT1 = context.getFloat("Magic Find on Common-Epic (Legacy)", context.getFloat("Magic Find (Legacy)"))
        val lootingT1 = context.getInt("Looting on Common-Epic (Legacy)", context.getInt("Looting (Legacy)"))
        val magicFindT2 = context.getFloat("Magic Find on Legendary-Mythic (Legacy)", context.getFloat("Magic Find (Legacy)"))
        val lootingT2 = context.getInt("Looting on Legendary-Mythic (Legacy)", context.getInt("Looting (Legacy)"))
        val magicFindHorseman = context.getFloat("Magic Find on Horseman (Legacy)", context.getFloat("Magic Find (Legacy)"))
        val lootingHorseman = context.getInt("Looting on Horseman (Legacy)", context.getInt("Looting (Legacy)"))

        val commonSeaCreature = context.getMultipliedInt("Common Sea Creatures")
        val uncommonSeaCreature = context.getMultipliedInt("Uncommon Sea Creatures")
        val rareSeaCreature = context.getMultipliedInt("Rare Sea Creatures")
        val epicSeaCreature = context.getMultipliedInt("Epic Sea Creatures")
        val legendarySeaCreature = context.getMultipliedInt("Legendary Sea Creatures")
        val mythicSeaCreature = context.getMultipliedInt("Mythic Sea Creatures")
        val spookyKills = context.getMultipliedInt("Mythic Sea Creatures")
        val batKills = context.getMultipliedInt("Mythic Sea Creatures")
        val headlessKills = context.getMultipliedInt("Mythic Sea Creatures")
        val magicFind = context.getFloat("Magic Find")


        val legacyProgress = (t1SeaCreatureKills / 1_000_000.0 + legacySpookyKills / 500_000.0)  * (1.0 + magicFindT1 / 100.0) * (1.0 + lootingT1 * 0.15) +
                (t3SeaCreatureKills / 50_000.0) * (1.0 + magicFindT2 / 100.0) * (1.0 + lootingT2 * 0.15) +
                (legacyHeadlessKills / 50_000.0) * (1.0 + magicFindHorseman / 100.0) * (1.0 + lootingHorseman * 0.15)

        val currentProgress = (commonSeaCreature / dye.getDropRate("Common") +
                uncommonSeaCreature / dye.getDropRate("Uncommon") +
                rareSeaCreature / dye.getDropRate("Rare") +
                epicSeaCreature / dye.getDropRate("Epic") +
                legendarySeaCreature / dye.getDropRate("Legendary") +
                mythicSeaCreature / dye.getDropRate("Mythic") +
                spookyKills / dye.getDropRate("Spooky") +
                batKills / dye.getDropRate("Bat") +
                headlessKills / dye.getDropRate("Horseman")) * (1.0 + magicFind / 100.0)

        return legacyProgress + currentProgress
    }
}