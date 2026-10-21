package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class CeladonStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Celadon Dye"),
    listOf(
        StatisticField("Bacte Kills", Parsers.INT, true),
        StatisticField("Blobbercyst Kills", Parsers.INT, true),
        StatisticField("Leech Supreme Kills", Parsers.INT, true)),
    Dye.CELADON
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)?.playerStats
        val blobbercystKills = profileStats?.kills["blobbercyst"] ?: 0
        val bacteKills = profileStats?.rift?.get("colosseum_bacte_defeated")?.asInt ?: 0
        val leechKills = profileStats?.rift?.get("lagoon_leech_supreme_killed")?.asInt ?: 0

        (this.widgets["Bacte Kills"]?.widget as EditBox).value = bacteKills.toString()
        (this.widgets["Blobbercyst Kills"]?.widget as EditBox).value = blobbercystKills.toString()
        (this.widgets["Leech Supreme Kills"]?.widget as EditBox).value = leechKills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val legacyBacteKills = context.getMultipliedInt("Bacte Kills", legacy = true)
        val legacyBlobbercystKills = context.getMultipliedInt("Blobbercyst Kills", legacy = true)

        val bacteKills = context.getMultipliedInt("Bacte Kills")
        val blobbercystKills = context.getMultipliedInt("Blobbercyst Kills")
        val supremeKills = context.getMultipliedInt("Leech Supreme Kills")

        val legacyProgress = legacyBacteKills / 10_000.0 + legacyBlobbercystKills / 100_000.0

        val currentProgress = bacteKills / dye.getDropRate("Bacte") +
                blobbercystKills / dye.getDropRate("Blobbercyst") +
                supremeKills / dye.getDropRate("Leech Supreme")

        return legacyProgress + currentProgress
    }
}