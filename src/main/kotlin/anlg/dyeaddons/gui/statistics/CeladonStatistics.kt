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
        StatisticField("Leech Supreme Kills", Parsers.INT, true),
        StatisticField("Leech Alpha Kills", Parsers.INT, true),
        StatisticField("Leech Swarm Kills", Parsers.INT, true),
        StatisticField("Magic Find", Parsers.FLOAT),
        ),
    Dye.CELADON,
    requiresApi = true
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)?.playerStats
        val blobbercystKills = profileStats?.kills["blobbercyst"] ?: 0
        val bacteKills = profileStats?.rift?.get("colosseum_bacte_defeated")?.asInt ?: 0
        val leechKills = profileStats?.rift?.get("lagoon_leech_supreme_killed")?.asInt ?: 0
        val alphaKills = profileStats?.kills["leech_alpha"] ?: 0
        val swarmKills = profileStats?.kills["leech_swarm"] ?: 0

        (this.widgets["Bacte Kills"]?.widget as EditBox).value = bacteKills.toString()
        (this.widgets["Blobbercyst Kills"]?.widget as EditBox).value = blobbercystKills.toString()
        (this.widgets["Leech Supreme Kills"]?.widget as EditBox).value = leechKills.toString()
        (this.widgets["Leech Alpha Kills"]?.widget as EditBox).value = alphaKills.toString()
        (this.widgets["Leech Swarm Kills"]?.widget as EditBox).value = swarmKills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val legacyBacteKills = context.getMultipliedInt("Bacte Kills", legacy = true)
        val legacyBlobbercystKills = context.getMultipliedInt("Blobbercyst Kills", legacy = true)

        val bacteKills = context.getMultipliedInt("Bacte Kills")
        val blobbercystKills = context.getMultipliedInt("Blobbercyst Kills")
        val supremeKills = context.getMultipliedInt("Leech Supreme Kills")
        val alphaKills = context.getMultipliedInt("Leech Alpha Kills")
        val swarmKills = context.getMultipliedInt("Leech Swarm Kills")
        val magicFind = context.getFloat("Magic Find")

        val legacyProgress = legacyBacteKills / dye.getDropRate("Bacte (Legacy)") +
                legacyBlobbercystKills / dye.getDropRate("Blobbercyst (Legacy)")

        val currentProgress = (bacteKills / dye.getDropRate("Bacte") +
                supremeKills / dye.getDropRate("Leech Supreme") +
                blobbercystKills / dye.getDropRate("Blobbercyst") +
                alphaKills / dye.getDropRate("Leech Alpha") +
                swarmKills / dye.getDropRate("Leech Swarm")) * (1.0 + magicFind / 100.0)

        return legacyProgress + currentProgress
    }
}