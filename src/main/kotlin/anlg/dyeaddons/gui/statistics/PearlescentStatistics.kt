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
        StatisticField("1/10m Mob Kills", Parsers.INT),
        StatisticField("1/5m Mob Kills", Parsers.INT),
        StatisticField("1/100k Mob Kills", Parsers.INT),
        StatisticField("Boss Kills", Parsers.INT),
        StatisticField("Magic Find", Parsers.FLOAT),
        StatisticField("Looting", Parsers.INT)),
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
        bossKills += profileStats?.sumOfBestiaryKills(listOf("superior_dragon_100")) ?: 0 // doubling superior because they have 1/50k chance

        (this.widgets["1/10m Mob Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["1/5m Mob Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["1/100k Mob Kills"]?.widget as EditBox).value = t3Kills.toString()
        (this.widgets["Boss Kills"]?.widget as EditBox).value = bossKills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getInt("1/10m Mob Kills")
        val t2Kills = context.getInt("1/5m Mob Kills")
        val t3Kills = context.getInt("1/100k Mob Kills")
        val bossKills = context.getInt("Boss Kills")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")

        val result = (t1Kills / 10_000_000.0 + t2Kills / 5_000_000.0 + t3Kills / 100_000.0) *
                (1.0 + magicFind / 100.0) *
                (1.0 + looting * 0.15) +
                bossKills / 100_000.0
        return result
    }
}