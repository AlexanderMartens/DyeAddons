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

class BoneStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Bone Dye"),
    listOf(
        StatisticField("Skeleton Kills", Parsers.INT),
        StatisticField("Magic Find", Parsers.FLOAT),
        StatisticField("Looting", Parsers.INT)),
    Dye.BONE
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        // Probably not all skeletons, but close enough
        val kills = playerStats?.sumOfKills(listOf(
            "skeleton",
            "jockey_skeleton",
            "scared_skeleton",
            "dungeon_respawning_skeleton",
            "skeleton_grunt",
            "sniper_skeleton",
            "skeleton_soldier",
            "skeleton_master",
            "night_respawning_skeleton",
            "crypt_witherskeleton",
            "respawning_skeleton",
            "skeleton_lord",
            "diamond_skeleton",
            "master_dungeon_respawning_skeleton",
            "master_scared_skeleton",
            "master_skeleton_grunt",
            "master_skeleton_soldier",
            "master_sniper_skeleton",
            "master_skeleton_master",
            "wither_skeleton",
            "master_crypt_witherskeleton",
            "master_skeleton_lord",
            "bladesoul",
            "bogged",
            "chillblade",
            "chillshot",
            "ragnarok",
            "sea_archer",
            "skeletor",
            "skeletor_prime",
            "master_skeletor",
            "master_skeletor_prime",
            "super_archer",
            "master_super_archer",
            "guardian_emperor",
            "master_wither_husk",
            "obsidian_wither",
            "wither_gourd",
            "wither_spectre",
            "wither_miner",
            "wither_guard",
            "wither_defender_guard",
            "master_wither_miner",
            "master_wither_guard",
            "master_wither_husk",
            "wither_sentry",
        )) ?: 0

        (this.widgets["Skeleton Kills"]?.widget as EditBox).value = kills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val kills = context.getInt("Skeleton Kills")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")

        val result = (kills / 3_000_000.0) *
                (1.0 + magicFind / 100.0) *
                (1.0 + looting * 0.15)
        return result
    }
}