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
        StatisticField("Common Sea Creatures", Parsers.INT, true),
        StatisticField("Common Sea Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Uncommon Sea Creatures", Parsers.INT, true),
        StatisticField("Uncommon Sea Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Rare Sea Creatures", Parsers.INT, true),
        StatisticField("Rare Sea Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Epic Sea Creatures", Parsers.INT, true),
        StatisticField("Epic Sea Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Legendary Sea Creatures", Parsers.INT, true),
        StatisticField("Legendary Sea Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Mythic Sea Creatures", Parsers.INT, true),
        StatisticField("Mythic Sea Creatures (Lootshare)", Parsers.INT, true),
        StatisticField("Magic Find", Parsers.FLOAT)),
    Dye.ICEBERG,
    requiresApi = true
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        val common = playerStats?.sumOfKills(listOf("frozen_steve",)) ?: 0
        val uncommon = playerStats?.sumOfKills(listOf("frosty_the_snowman")) ?: 0
        val rare = playerStats?.sumOfKills(listOf("grinch")) ?: 0
        val epic = playerStats?.sumOfKills(listOf("nutcracker")) ?: 0
        val legendary = playerStats?.sumOfKills(listOf("yeti")) ?: 0
        val mythic = playerStats?.sumOfKills(listOf("reindrake_100")) ?: 0

        val commonLootshare = profileStats?.sumOfBestiary(listOf("frozen_steve"))?.minus(common)?.coerceAtLeast(0) ?: 0
        val uncommonLootshare = profileStats?.sumOfBestiary(listOf("frosty_the_snowman"))?.minus(uncommon)?.coerceAtLeast(0) ?: 0
        val rareLootshare = profileStats?.sumOfBestiary(listOf("grinch"))?.minus(rare)?.coerceAtLeast(0) ?: 0
        val epicLootshare = profileStats?.sumOfBestiary(listOf("nutcracker"))?.minus(epic)?.coerceAtLeast(0) ?: 0
        val legendaryLootshare = profileStats?.sumOfBestiary(listOf("yeti"))?.minus(legendary)?.coerceAtLeast(0) ?: 0
        val mythicLootshare = 0 // counting all reindrakes as owned

        (this.widgets["Common Sea Creatures"]?.widget as EditBox).value = common.toString()
        (this.widgets["Uncommon Sea Creatures"]?.widget as EditBox).value = uncommon.toString()
        (this.widgets["Rare Sea Creatures"]?.widget as EditBox).value = rare.toString()
        (this.widgets["Epic Sea Creatures"]?.widget as EditBox).value = epic.toString()
        (this.widgets["Legendary Sea Creatures"]?.widget as EditBox).value = legendary.toString()
        (this.widgets["Mythic Sea Creatures"]?.widget as EditBox).value = mythic.toString()

        (this.widgets["Common Sea Creatures (Lootshare)"]?.widget as EditBox).value = commonLootshare.toString()
        (this.widgets["Uncommon Sea Creatures (Lootshare)"]?.widget as EditBox).value = uncommonLootshare.toString()
        (this.widgets["Rare Sea Creatures (Lootshare)"]?.widget as EditBox).value = rareLootshare.toString()
        (this.widgets["Epic Sea Creatures (Lootshare)"]?.widget as EditBox).value = epicLootshare.toString()
        (this.widgets["Legendary Sea Creatures (Lootshare)"]?.widget as EditBox).value = legendaryLootshare.toString()
        (this.widgets["Mythic Sea Creatures (Lootshare)"]?.widget as EditBox).value = mythicLootshare.toString()
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
        val commonSeaCreatureLootshare = context.getMultipliedInt("Common Sea Creatures (Lootshare)")
        val uncommonSeaCreatureLootshare = context.getMultipliedInt("Uncommon Sea Creatures (Lootshare)")
        val rareSeaCreatureLootshare = context.getMultipliedInt("Rare Sea Creatures (Lootshare)")
        val epicSeaCreatureLootshare = context.getMultipliedInt("Epic Sea Creatures (Lootshare)")
        val legendarySeaCreatureLootshare = context.getMultipliedInt("Legendary Sea Creatures (Lootshare)")
        val mythicSeaCreatureLootshare = context.getMultipliedInt("Mythic Sea Creatures (Lootshare)")
        val magicFind = context.getFloat("Magic Find")

        val legacyProgress = (t1SeaCreatureKills / dye.getDropRate("Common (Legacy)") +
                t2SeaCreatureKills / dye.getDropRate("Rare (Legacy)")) * (1.0 + magicFindT1 / 100.0) * (1.0 + lootingT1 * 0.15) +
                (t3SeaCreatureKills / dye.getDropRate("Legendary (Legacy)")) * (1.0 + magicFindT2 / 100.0) * (1.0 + lootingT2 * 0.15)

        val currentProgress = (commonSeaCreature / dye.getDropRate("Common") +
                uncommonSeaCreature / dye.getDropRate("Uncommon") +
                rareSeaCreature / dye.getDropRate("Rare") +
                epicSeaCreature / dye.getDropRate("Epic") +
                legendarySeaCreature / dye.getDropRate("Legendary") +
                mythicSeaCreature / dye.getDropRate("Mythic") +
                commonSeaCreatureLootshare / dye.getDropRate("Common") / 5.0 +
                uncommonSeaCreatureLootshare / dye.getDropRate("Uncommon") / 5.0 +
                rareSeaCreatureLootshare / dye.getDropRate("Rare") / 5.0 +
                epicSeaCreatureLootshare / dye.getDropRate("Epic") / 5.0 +
                legendarySeaCreatureLootshare / dye.getDropRate("Legendary") / 5.0 +
                mythicSeaCreatureLootshare / dye.getDropRate("Mythic")) / 5.0 * (1.0 + magicFind / 100.0)

        return legacyProgress + currentProgress
    }
}