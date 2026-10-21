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

class AquamarineStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Aquamarine Dye"),
    listOf(
        StatisticField("Common Sea Creatures", Parsers.INT, true),
        StatisticField("Uncommon Sea Creatures", Parsers.INT, true),
        StatisticField("Rare Sea Creatures", Parsers.INT, true),
        StatisticField("Epic Sea Creatures", Parsers.INT, true),
        StatisticField("Legendary Sea Creatures", Parsers.INT, true),
        StatisticField("Mythic Sea Creatures", Parsers.INT, true),
        StatisticField("Magic Find", Parsers.FLOAT)),
    Dye.AQUAMARINE
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        val common = playerStats?.sumOfKills(listOf(
            "bogged",
            "frog_man",
            "sea_walker",
            "pond_squid",
            "trash_gobbler",
            "atoll_croaker",
            "haggard",
            "frozen_steve",
            "sea_guardian",
        )) ?: 0
        val uncommon = playerStats?.sumOfKills(listOf(
            "small_mithril_grubber",
            "medium_mithril_grubber",
            "large_mithril_grubber",
            "bloated_mithril_grubber",
            "night_squid",
            "chicken_deep",
            "sea_archer",
            "sea_witch",
            "wetwing_chicken",
            "dumpster_diver",
            "lotus_guardian",
            "brineling",
            "frosty_the_snowman",
            "nurse_shark",
            "scarecrow",
        )) ?: 0
        val rare = playerStats?.sumOfKills(listOf(
            "agarimoo",
            "carrot_king",
            "catfish",
            "oasis_sheep",
            "oasis_rabbit",
            "poisoned_water_worm",
            "sea_leech",
            "snapping_turtle",
            "water_worm",
            "tadgang_frog",
            "banshee",
            "gorf",
            "sprawl",
            "grinch",
            "blue_shark",
            "nightmare",
        )) ?: 0
        val epic = playerStats?.sumOfKills(listOf(
            "deep_sea_protector",
            "ent",
            "guardian_defender",
            "oasis_sheep",
            "oasis_rabbit",
            "bayou_sludge",
            "baby_bayou_sludge",
            "drowned_captain",
            "manta_ray",
            "torrid",
            "nutcracker",
            "tiger_shark",
            "werewolf",
        )) ?: 0
        val legendary = playerStats?.sumOfKills(listOf(
            "zombie_miner",
            "blue_ringed_octopus",
            "guardian_emperor",
            "water_hydra",
            "alligator",
            "puddle_jumper",
            "silkbreeze",
            "yeti",
            "great_white_shark",
            "phantom_fisherman",
        )) ?: 0
        val mythic = playerStats?.sumOfKills(listOf(
            "wiki_tiki",
            "titanoboa",
            "frog_prince",
            "giant_isopod",
            "reindrake",
            "grim_reaper",
        )) ?: 0

        (this.widgets["Common Sea Creatures"]?.widget as EditBox).value = common.toString()
        (this.widgets["Uncommon Sea Creatures"]?.widget as EditBox).value = uncommon.toString()
        (this.widgets["Rare Sea Creatures"]?.widget as EditBox).value = rare.toString()
        (this.widgets["Epic Sea Creatures"]?.widget as EditBox).value = epic.toString()
        (this.widgets["Legendary Sea Creatures"]?.widget as EditBox).value = legendary.toString()
        (this.widgets["Mythic Sea Creatures"]?.widget as EditBox).value = mythic.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1SeaCreatureKills = context.getMultipliedInt("Common/Uncommon Sea Creature Kills", legacy = true)
        val t2SeaCreatureKills = context.getMultipliedInt("Rare/Epic Sea Creature Kills", legacy = true)
        val t3SeaCreatureKills = context.getMultipliedInt("Legendary/Mythic Sea Creature Kills", legacy = true)
        val magicFindT1 = context.getFloat("Magic Find on Common-Epic (Legacy)", context.getFloat("Magic Find (Legacy)"))
        val lootingT1 = context.getInt("Looting on Common-Epic (Legacy)", context.getInt("Looting (Legacy)"))
        val magicFindT2 = context.getFloat("Magic Find on Legendary-Mythic (Legacy)", context.getFloat("Magic Find (Legacy)"))
        val lootingT2 = context.getInt("Looting on Legendary-Mythic (Legacy)", context.getInt("Looting (Legacy)"))

        val commonSeaCreature = context.getMultipliedInt("Common Sea Creatures")
        val uncommonSeaCreature = context.getMultipliedInt("Uncommon Sea Creatures")
        val rareSeaCreature = context.getMultipliedInt("Rare Sea Creatures")
        val epicSeaCreature = context.getMultipliedInt("Epic Sea Creatures")
        val legendarySeaCreature = context.getMultipliedInt("Legendary Sea Creatures")
        val mythicSeaCreature = context.getMultipliedInt("Mythic Sea Creatures")
        val magicFind = context.getFloat("Magic Find")

        val legacyProgress = (t1SeaCreatureKills / 5_000_000.0 + t2SeaCreatureKills / 2_500_000.0) * (1.0 + magicFindT1 / 100.0) * (1.0 + lootingT1 * 0.15) +
                (t3SeaCreatureKills / 50_000.0) * (1.0 + magicFindT2 / 100.0) * (1.0 + lootingT2 * 0.15)

        val currentProgress = (commonSeaCreature / dye.getDropRate("Common") +
                uncommonSeaCreature / dye.getDropRate("Uncommon") +
                rareSeaCreature / dye.getDropRate("Rare") +
                epicSeaCreature / dye.getDropRate("Epic") +
                legendarySeaCreature / dye.getDropRate("Legendary") +
                mythicSeaCreature / dye.getDropRate("Mythic")) * (1.0 + magicFind / 100.0)

        return legacyProgress + currentProgress
    }
}