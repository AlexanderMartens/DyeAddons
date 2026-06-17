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

class BrickRedStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Brick Red Dye"),
    listOf(
        StatisticField("T1 Tarantula Broodfather Kills", Parsers.INT),
        StatisticField("T2 Tarantula Broodfather Kills", Parsers.INT),
        StatisticField("T3 Tarantula Broodfather Kills", Parsers.INT),
        StatisticField("T4 Tarantula Broodfather Kills", Parsers.INT),
        StatisticField("T5 Tarantula Broodfather Kills", Parsers.INT)),
    Dye.BRICK_RED
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val slayerStats = profileStats?.slayer?.objPath("slayer_bosses", "spider")
        val t1Kills = slayerStats?.get("boss_kills_tier_0")?.asInt ?: 0
        val t2Kills = slayerStats?.get("boss_kills_tier_1")?.asInt ?: 0
        val t3Kills = slayerStats?.get("boss_kills_tier_2")?.asInt ?: 0
        val t4Kills = slayerStats?.get("boss_kills_tier_3")?.asInt ?: 0
        val t5Kills = slayerStats?.get("boss_kills_tier_4")?.asInt ?: 0

        (this.widgets["T1 Tarantula Broodfather Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["T2 Tarantula Broodfather Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["T3 Tarantula Broodfather Kills"]?.widget as EditBox).value = t3Kills.toString()
        (this.widgets["T4 Tarantula Broodfather Kills"]?.widget as EditBox).value = t4Kills.toString()
        (this.widgets["T5 Tarantula Broodfather Kills"]?.widget as EditBox).value = t5Kills.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val t1Kills = context.getInt("T1 Tarantula Broodfather Kills")
        val t2Kills = context.getInt("T2 Tarantula Broodfather Kills")
        val t3Kills = context.getInt("T3 Tarantula Broodfather Kills")
        val t4Kills = context.getInt("T4 Tarantula Broodfather Kills")
        val t5Kills = context.getInt("T5 Tarantula Broodfather Kills")

        val result = t1Kills / 10_000_000.0 +
                t2Kills / 2_500_000.0 +
                t3Kills / 1_000_000.0 +
                t4Kills / 500_000.0 +
                t5Kills / 250_000.0
        return result
    }
}