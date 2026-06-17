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
        StatisticField("Bacte Kills", Parsers.INT),
        StatisticField("Blobbercyst Kills", Parsers.INT)),
    Dye.CELADON
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)?.playerStats
        val blobbercystKills = profileStats?.kills["blobbercyst"] ?: 0
        val bacteKills = profileStats?.rift?.get("colosseum_bacte_defeated")?.asInt ?: 0

        (this.widgets["Bacte Kills"]?.widget as EditBox).value = bacteKills.toString()
        (this.widgets["Blobbercyst Kills"]?.widget as EditBox).value = blobbercystKills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val bacteKills = context.getInt("Bacte Kills")
        val blobbercystKills = context.getInt("Blobbercyst Kills")

        val result = bacteKills / 10_000.0 + blobbercystKills / 100_000.0
        return result
    }
}