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

class SangriaStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Sangria Dye"),
    listOf(
        StatisticField("T1 Riftstalker Bloodfiend Kills", Parsers.INT),
        StatisticField("T2 Riftstalker Bloodfiend Kills", Parsers.INT),
        StatisticField("T3 Riftstalker Bloodfiend Kills", Parsers.INT),
        StatisticField("T4 Riftstalker Bloodfiend Kills", Parsers.INT),
        StatisticField("T5 Riftstalker Bloodfiend Kills", Parsers.INT)),
    Dye.SANGRIA
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val slayerStats = profileStats?.slayer?.objPath("slayer_bosses", "vampire")
        val t1Kills = slayerStats?.get("boss_kills_tier_0")?.asInt ?: 0
        val t2Kills = slayerStats?.get("boss_kills_tier_1")?.asInt ?: 0
        val t3Kills = slayerStats?.get("boss_kills_tier_2")?.asInt ?: 0
        val t4Kills = slayerStats?.get("boss_kills_tier_3")?.asInt ?: 0
        val t5Kills = slayerStats?.get("boss_kills_tier_4")?.asInt ?: 0

        (this.widgets["T1 Riftstalker Bloodfiend Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["T2 Riftstalker Bloodfiend Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["T3 Riftstalker Bloodfiend Kills"]?.widget as EditBox).value = t3Kills.toString()
        (this.widgets["T4 Riftstalker Bloodfiend Kills"]?.widget as EditBox).value = t4Kills.toString()
        (this.widgets["T5 Riftstalker Bloodfiend Kills"]?.widget as EditBox).value = t5Kills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getInt("T1 Riftstalker Bloodfiend Kills")
        val t2Kills = context.getInt("T2 Riftstalker Bloodfiend Kills")
        val t3Kills = context.getInt("T3 Riftstalker Bloodfiend Kills")
        val t4Kills = context.getInt("T4 Riftstalker Bloodfiend Kills")
        val t5Kills = context.getInt("T5 Riftstalker Bloodfiend Kills")

        val result = t1Kills / 100_000.0 +
                t2Kills / 80_000.0 +
                t3Kills / 60_000.0 +
                t4Kills / 40_000.0 +
                t5Kills / 10_000.0
        return result
    }
}