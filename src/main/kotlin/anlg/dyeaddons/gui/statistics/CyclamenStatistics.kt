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
        StatisticField("1/10m Mob Kills", Parsers.INT),
        StatisticField("1/2.5m Mob Kills", Parsers.INT),
        StatisticField("1/250k Mob Kills", Parsers.INT),
        StatisticField("Miniboss Kills", Parsers.INT),
        StatisticField("Magic Find", Parsers.FLOAT),
        StatisticField("Looting", Parsers.INT)),
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
            "barbarian",
            "goliath_barbarian",
            "fire_mage",
            "magma_glare",
            "unstable_magma",
            "smoldering_blaze",
            "old_blaze",
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

        (this.widgets["1/10m Mob Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["1/2.5m Mob Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["1/250k Mob Kills"]?.widget as EditBox).value = t3Kills.toString()
        (this.widgets["Miniboss Kills"]?.widget as EditBox).value = bossKills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getInt("1/10m Mob Kills")
        val t2Kills = context.getInt("1/2.5m Mob Kills")
        val t3Kills = context.getInt("1/250k Mob Kills")
        val miniKills = context.getInt("Miniboss Kills")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")

        val result = (t1Kills / 10_000_000.0 + t2Kills / 2_500_000.0 + t3Kills / 250_000.0) *
                (1.0 + magicFind / 100.0) *
                (1.0 + looting * 0.15) +
                miniKills / 250_000.0
        return result
    }
}