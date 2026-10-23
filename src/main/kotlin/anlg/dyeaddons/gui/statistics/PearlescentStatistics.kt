package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.api.sumOfBestiaryKills
import anlg.dyeaddons.api.sumOfKills
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class PearlescentStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Pearlescent Dye"),
    listOf(
        StatisticField("T1 Mob Kills", Parsers.INT, true),
        StatisticField("T2 Mob Kills", Parsers.INT, true),
        StatisticField("T3 Mob Kills", Parsers.INT, true),
        StatisticField("Boss Kills", Parsers.INT, true),
        StatisticField("Magic Find", Parsers.FLOAT)),
    Dye.PEARLESCENT
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        val t1Kills = playerStats?.sumOfKills(listOf(
            "enderman",
            "endermite",
            "obsidian_wither",
            "watcher",
            "zealot_enderman",
            "zealot_bruiser"
        )) ?: 0
        val t2Kills = playerStats?.sumOfKills(listOf("nest_endermite", "voidling_fanatic", "voidling_extremist")) ?: 0
        val t3Kills = playerStats?.sumOfKills(listOf("zealot_special_enderman")) ?: 0
        var bossKills = profileStats?.sumOfBestiaryKills(
            listOf(
                "unstable_dragon_100",
                "superior_dragon_100",
                "protector_dragon_100",
                "young_dragon_100",
                "old_dragon_100",
                "strong_dragon_100",
                "wise_dragon_100",
                "corrupted_protector_100")) ?: 0
        bossKills += profileStats?.sumOfBestiaryKills(listOf("superior_dragon_100"))?.times(3) ?: 0 // quadrupling superior because they have 1/25k chance

        (this.widgets["T1 Mob Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["T2 Mob Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["T3 Mob Kills"]?.widget as EditBox).value = t3Kills.toString()
        (this.widgets["Boss Kills"]?.widget as EditBox).value = bossKills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val legacyT1Kills = context.getMultipliedInt("1/10m Mob Kills", legacy = true)
        val legacyT2Kills = context.getMultipliedInt("1/5m Mob Kills", legacy = true)
        val legacyT3Kills = context.getMultipliedInt("1/100k Mob Kills", legacy = true)
        val legacyBossKills = context.getMultipliedInt("Boss Kills", legacy = true)
        val legacyMagicFind = context.getFloat("Magic Find (Legacy)")
        val legacyLooting = context.getInt("Looting (Legacy)")

        val t1Kills = context.getMultipliedInt("T1 Mob Kills")
        val t2Kills = context.getMultipliedInt("T2 Mob Kills")
        val t3Kills = context.getMultipliedInt("T3 Mob Kills")
        val bossKills = context.getMultipliedInt("Boss Kills")
        val magicFind = context.getFloat("Magic Find")

        val currentProgress = (t1Kills / dye.getDropRate("T1") +
                t2Kills / dye.getDropRate("T2") +
                t3Kills / dye.getDropRate("T3")) * (1.0 + magicFind / 100.0) +
                bossKills / dye.getDropRate("T3")
        val legacyProgress = (legacyT1Kills / 10_000_000.0 + legacyT2Kills / 5_000_000.0 + legacyT3Kills / 100_000.0) *
                (1.0 + legacyMagicFind / 100.0) *
                (1.0 + legacyLooting * 0.15) +
                legacyBossKills / 100_000.0

        return currentProgress + legacyProgress
    }
}