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

class CyclamenStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Cyclamen Dye"),
    listOf(
        StatisticField("T1 Mob Kills", Parsers.INT, true),
        StatisticField("T2 Mob Kills", Parsers.INT, true),
        StatisticField("T3 Mob Kills", Parsers.INT, true),
        StatisticField("Miniboss Kills", Parsers.INT, true),
        StatisticField("Magic Find", Parsers.FLOAT)),
    Dye.CYCLAMEN
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        val t1Kills = playerStats?.sumOfKills(listOf(
            "blaze",
            "mutated_blaze",
            "bezal",
            "magma_cube",
            "pack_magma_cube",
            "flaming_spider",
            "kada_knight",
            "magma_cube_rider",
            "charging_mushroom_cow",
            "wither_skeleton",
            "wither_spectre",
            "flare"
        )) ?: 0
        val t2Kills = playerStats?.sumOfKills(listOf(
            "dive_ghast",
            "ghast",
            "magma_glare",
            "unstable_magma",
            "smoldering_blaze",
            "old_blaze",
            "wither_guard",
            "ashfang_blue_blaze",
            "ashfang_red_blaze",
            "ashfang_gray_blaze"
        )) ?: 0
        val t3Kills = playerStats?.sumOfKills(listOf("hellwisp", "vanquisher", "cinder_bat", "matcho")) ?: 0
        val bossKills = profileStats?.sumOfBestiaryKills(
            listOf(
                "ashfang_200",
                "bladesoul_200",
                "mage_outlaw_200",
                "barbarian_duke_x_200",
                "magma_boss_500"
        )) ?: 0

        (this.widgets["T1 Mob Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["T2 Mob Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["T3 Mob Kills"]?.widget as EditBox).value = t3Kills.toString()
        (this.widgets["Miniboss Kills"]?.widget as EditBox).value = bossKills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val legacyT1Kills = context.getMultipliedInt("1/10m Mob Kills", legacy = true)
        val legacyT2Kills = context.getMultipliedInt("1/2.5m Mob Kills", legacy = true)
        val legacyT3Kills = context.getMultipliedInt("1/250k Mob Kills", legacy = true)
        val legacyMiniKills = context.getMultipliedInt("Miniboss Kills", legacy = true)
        val legacyMagicFind = context.getFloat("Magic Find (Legacy)")
        val legacyLooting = context.getInt("Looting (Legacy)")

        val t1Kills = context.getMultipliedInt("T1 Mob Kills")
        val t2Kills = context.getMultipliedInt("T2 Mob Kills")
        val t3Kills = context.getMultipliedInt("T3 Mob Kills")
        val miniKills = context.getMultipliedInt("Miniboss Kills")
        val magicFind = context.getFloat("Magic Find")

        val legacyProgress = (legacyT1Kills / 10_000_000.0 + legacyT2Kills / 2_500_000.0 + legacyT3Kills / 250_000.0) *
                (1.0 + legacyMagicFind / 100.0) *
                (1.0 + legacyLooting * 0.15) +
                legacyMiniKills / 250_000.0

        val currentProgress = (t1Kills / dye.getDropRate("T1") +
                t2Kills / dye.getDropRate("T2") +
                t3Kills / dye.getDropRate("T3")) * (1.0 + magicFind / 100.0) +
                miniKills / dye.getDropRate("T3")

        return legacyProgress + currentProgress
    }
}