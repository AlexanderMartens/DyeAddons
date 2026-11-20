package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.api.sumOfBestiary
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
        StatisticField("Common Mythological Creatures", Parsers.INT, true),
        StatisticField("Common Mythological Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Uncommon Mythological Creatures", Parsers.INT, true),
        StatisticField("Uncommon Mythological Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Rare Mythological Creatures", Parsers.INT, true),
        StatisticField("Rare Mythological Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Epic Mythological Creatures", Parsers.INT, true),
        StatisticField("Epic Mythological Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Legendary Mythological Creatures", Parsers.INT, true),
        StatisticField("Legendary Mythological Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Mythic Mythological Creatures", Parsers.INT, true),
        StatisticField("Mythic Mythological Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Magic Find", Parsers.FLOAT)),
    Dye.MYTHOLOGICAL,
    requiresApi = true
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        val common = playerStats?.sumOfKills(listOf("minos_hunter", "siamese_lynx")) ?: 0
        val uncommon = playerStats?.sumOfKills(listOf("stranded_nymph", "cretan_bull")) ?: 0
        val rare = playerStats?.sumOfKills(listOf("harpy", "gaia_construct")) ?: 0
        val epic = playerStats?.sumOfKills(listOf("minotaur", "minos_champion")) ?: 0
        val legendary = playerStats?.sumOfKills(listOf("sphinx", "minos_inquisitor")) ?: 0
        val mythic = playerStats?.sumOfKills(listOf("king_minos", "manticore")) ?: 0

        val commonLootshare = profileStats?.sumOfBestiary(listOf("minos_hunter", "siamese_lynx"))?.minus(common)?.coerceAtLeast(0) ?: 0
        val uncommonLootshare = profileStats?.sumOfBestiary(listOf("stranded_nymph", "cretan_bull"))?.minus(uncommon)?.coerceAtLeast(0) ?: 0
        val rareLootshare = profileStats?.sumOfBestiary(listOf("harpy", "gaia_construct"))?.minus(rare)?.coerceAtLeast(0) ?: 0
        val epicLootshare = profileStats?.sumOfBestiary(listOf("minotaur", "minos_champion"))?.minus(epic)?.coerceAtLeast(0) ?: 0
        val legendaryLootshare = profileStats?.sumOfBestiary(listOf("sphinx", "minos_inquisitor"))?.minus(legendary)?.coerceAtLeast(0) ?: 0
        val mythicLootshare = profileStats?.sumOfBestiary(listOf("king_minos", "manticore"))?.minus(mythic)?.coerceAtLeast(0) ?: 0

        (this.widgets["Common Mythological Creatures"]?.widget as EditBox).value = common.toString()
        (this.widgets["Uncommon Mythological Creatures"]?.widget as EditBox).value = uncommon.toString()
        (this.widgets["Rare Mythological Creatures"]?.widget as EditBox).value = rare.toString()
        (this.widgets["Epic Mythological Creatures"]?.widget as EditBox).value = epic.toString()
        (this.widgets["Legendary Mythological Creatures"]?.widget as EditBox).value = legendary.toString()
        (this.widgets["Mythic Mythological Creatures"]?.widget as EditBox).value = mythic.toString()

        (this.widgets["Common Mythological Creatures (Lootshare)"]?.widget as EditBox).value = commonLootshare.toString()
        (this.widgets["Uncommon Mythological Creatures (Lootshare)"]?.widget as EditBox).value = uncommonLootshare.toString()
        (this.widgets["Rare Mythological Creatures (Lootshare)"]?.widget as EditBox).value = rareLootshare.toString()
        (this.widgets["Epic Mythological Creatures (Lootshare)"]?.widget as EditBox).value = epicLootshare.toString()
        (this.widgets["Legendary Mythological Creatures (Lootshare)"]?.widget as EditBox).value = legendaryLootshare.toString()
        (this.widgets["Mythic Mythological Creatures (Lootshare)"]?.widget as EditBox).value = mythicLootshare.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1MythoCreatureKills = context.getMultipliedInt("Common/Uncommon Mythological Creature Kills", legacy = true)
        val t2MythoCreatureKills = context.getMultipliedInt("Rare Mythological Creature Kills", legacy = true)
        val t3MythoCreatureKills = context.getMultipliedInt("Epic Mythological Creature Kills", legacy = true)
        val t4MythoCreatureKills = context.getMultipliedInt("Legendary Mythological Creature Kills", legacy = true)
        val t5MythoCreatureKills = context.getMultipliedInt("Mythic Mythological Creature Kills", legacy = true)
        val magicFindT1 = context.getFloat("Magic Find on Common-Epic (Legacy)", stats?.get("Magic Find (Legacy)")?.asFloat() ?: 0f)
        val lootingT1 = context.getInt("Looting on Common-Epic (Legacy)", stats?.get("Looting (Legacy)")?.asInt() ?: 0)
        val magicFindT2 = context.getFloat("Magic Find on Legendary-Mythic (Legacy)", stats?.get("Magic Find (Legacy)")?.asFloat() ?: 0f)
        val lootingT2 = context.getInt("Looting on Legendary-Mythic (Legacy)", stats?.get("Looting (Legacy)")?.asInt() ?: 0)

        val commonMythologicalCreature = context.getMultipliedInt("Common Mythological Creatures")
        val uncommonMythologicalCreature = context.getMultipliedInt("Uncommon Mythological Creatures")
        val rareMythologicalCreature = context.getMultipliedInt("Rare Mythological Creatures")
        val epicMythologicalCreature = context.getMultipliedInt("Epic Mythological Creatures")
        val legendaryMythologicalCreature = context.getMultipliedInt("Legendary Mythological Creatures")
        val mythicMythologicalCreature = context.getMultipliedInt("Mythic Mythological Creatures")
        val commonMythologicalCreatureLootshare = context.getMultipliedInt("Common Mythological Creatures (Lootshare)")
        val uncommonMythologicalCreatureLootshare = context.getMultipliedInt("Uncommon Mythological Creatures (Lootshare)")
        val rareMythologicalCreatureLootshare = context.getMultipliedInt("Rare Mythological Creatures (Lootshare)")
        val epicMythologicalCreatureLootshare = context.getMultipliedInt("Epic Mythological Creatures (Lootshare)")
        val legendaryMythologicalCreatureLootshare = context.getMultipliedInt("Legendary Mythological Creatures (Lootshare)")
        val mythicMythologicalCreatureLootshare = context.getMultipliedInt("Mythic Mythological Creatures (Lootshare)")
        val magicFind = context.getFloat("Magic Find")

        val currentProgress = (commonMythologicalCreature / dye.getDropRate("Common") +
                uncommonMythologicalCreature / dye.getDropRate("Uncommon") +
                rareMythologicalCreature / dye.getDropRate("Rare") +
                epicMythologicalCreature / dye.getDropRate("Epic") +
                legendaryMythologicalCreature / dye.getDropRate("Legendary") +
                mythicMythologicalCreature / dye.getDropRate("Mythic") +
                commonMythologicalCreatureLootshare / dye.getDropRate("Common") / 5.0 +
                uncommonMythologicalCreatureLootshare / dye.getDropRate("Uncommon") / 5.0 +
                rareMythologicalCreatureLootshare / dye.getDropRate("Rare") / 5.0 +
                epicMythologicalCreatureLootshare / dye.getDropRate("Epic") / 5.0 +
                legendaryMythologicalCreatureLootshare / dye.getDropRate("Legendary") / 5.0 +
                mythicMythologicalCreatureLootshare / dye.getDropRate("Mythic") / 5.0) * (1.0 + magicFind / 100.0)
        val legacyProgress = (t1MythoCreatureKills / dye.getDropRate("Common") +
                t2MythoCreatureKills / dye.getDropRate("Rare") +
                t3MythoCreatureKills / dye.getDropRate("Epic")) * (1.0 + magicFindT1 / 100.0) * (1.0 + lootingT1 * 0.15) +
                (t4MythoCreatureKills / dye.getDropRate("Legendary") + t5MythoCreatureKills / dye.getDropRate("Mythic")) *
                (1.0 + magicFindT2 / 100.0) * (1.0 + lootingT2 * 0.15)

        return currentProgress + legacyProgress
    }
}