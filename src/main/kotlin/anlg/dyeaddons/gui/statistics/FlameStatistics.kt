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

class FlameStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Flame Dye"),
    listOf(
        StatisticField("T1 Inferno Demonlord Kills", Parsers.INT, true),
        StatisticField("T2 Inferno Demonlord Kills", Parsers.INT, true),
        StatisticField("T3 Inferno Demonlord Kills", Parsers.INT, true),
        StatisticField("T4 Inferno Demonlord Kills", Parsers.INT, true)),
    Dye.FLAME
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val slayerStats = profileStats?.slayer?.objPath("slayer_bosses", "blaze")
        val t1Kills = slayerStats?.get("boss_kills_tier_0")?.asInt ?: 0
        val t2Kills = slayerStats?.get("boss_kills_tier_1")?.asInt ?: 0
        val t3Kills = slayerStats?.get("boss_kills_tier_2")?.asInt ?: 0
        val t4Kills = slayerStats?.get("boss_kills_tier_3")?.asInt ?: 0

        (this.widgets["T1 Inferno Demonlord Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["T2 Inferno Demonlord Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["T3 Inferno Demonlord Kills"]?.widget as EditBox).value = t3Kills.toString()
        (this.widgets["T4 Inferno Demonlord Kills"]?.widget as EditBox).value = t4Kills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getMultipliedInt("T1 Inferno Demonlord Kills")
        val t2Kills = context.getMultipliedInt("T2 Inferno Demonlord Kills")
        val t3Kills = context.getMultipliedInt("T3 Inferno Demonlord Kills")
        val t4Kills = context.getMultipliedInt("T4 Inferno Demonlord Kills")

        val result = t1Kills / 10_000_000.0 +
                t2Kills / 2_500_000.0 +
                t3Kills / 1_000_000.0 +
                t4Kills / 500_000.0
        return result
    }
}