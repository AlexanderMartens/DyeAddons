package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.api.objPath
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class CelesteStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Celeste Dye"),
    listOf(
        StatisticField("T1 Sven Packmaster Kills", Parsers.INT),
        StatisticField("T2 Sven Packmaster Kills", Parsers.INT),
        StatisticField("T3 Sven Packmaster Kills", Parsers.INT),
        StatisticField("T4 Sven Packmaster Kills", Parsers.INT)),
    Dye.CELESTE
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val slayerStats = profileStats?.slayer?.objPath("slayer_bosses", "wolf")
        val t1Kills = slayerStats?.get("boss_kills_tier_0")?.asInt ?: 0
        val t2Kills = slayerStats?.get("boss_kills_tier_1")?.asInt ?: 0
        val t3Kills = slayerStats?.get("boss_kills_tier_2")?.asInt ?: 0
        val t4Kills = slayerStats?.get("boss_kills_tier_3")?.asInt ?: 0

        (this.widgets["T1 Sven Packmaster Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["T2 Sven Packmaster Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["T3 Sven Packmaster Kills"]?.widget as EditBox).value = t3Kills.toString()
        (this.widgets["T4 Sven Packmaster Kills"]?.widget as EditBox).value = t4Kills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getInt("T1 Sven Packmaster Kills")
        val t2Kills = context.getInt("T2 Sven Packmaster Kills")
        val t3Kills = context.getInt("T3 Sven Packmaster Kills")
        val t4Kills = context.getInt("T4 Sven Packmaster Kills")

        val result = t1Kills / 10_000_000.0 +
                t2Kills / 2_500_000.0 +
                t3Kills / 1_000_000.0 +
                t4Kills / 500_000.0
        return result
    }
}