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
        StatisticField("T1 Tarantula Broodfather Kills", Parsers.INT, true),
        StatisticField("T2 Tarantula Broodfather Kills", Parsers.INT, true),
        StatisticField("T3 Tarantula Broodfather Kills", Parsers.INT, true),
        StatisticField("T4 Tarantula Broodfather Kills", Parsers.INT, true),
        StatisticField("T5 Tarantula Broodfather Kills", Parsers.INT, true)),
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

        val t1Kills = context.getMultipliedInt("T1 Tarantula Broodfather Kills")
        val t2Kills = context.getMultipliedInt("T2 Tarantula Broodfather Kills")
        val t3Kills = context.getMultipliedInt("T3 Tarantula Broodfather Kills")
        val t4Kills = context.getMultipliedInt("T4 Tarantula Broodfather Kills")
        val t5Kills = context.getMultipliedInt("T5 Tarantula Broodfather Kills")

        val legacyT1Kills = context.getMultipliedInt("T1 Tarantula Broodfather Kills", legacy = true)
        val legacyT2Kills = context.getMultipliedInt("T2 Tarantula Broodfather Kills", legacy = true)
        val legacyT3Kills = context.getMultipliedInt("T3 Tarantula Broodfather Kills", legacy = true)
        val legacyT4Kills = context.getMultipliedInt("T4 Tarantula Broodfather Kills", legacy = true)
        val legacyT5Kills = context.getMultipliedInt("T5 Tarantula Broodfather Kills", legacy = true)

        val legacyProgress = legacyT1Kills / 10_000_000.0 +
                legacyT2Kills / 2_500_000.0 +
                legacyT3Kills / 1_000_000.0 +
                legacyT4Kills / 500_000.0 +
                legacyT5Kills / 250_000.0
        
        val currentProgress = t1Kills / dye.getDropRate("T1") +
                t2Kills / dye.getDropRate("T2") +
                t3Kills / dye.getDropRate("T3") +
                t4Kills / dye.getDropRate("T4") +
                t5Kills / dye.getDropRate("T5")

        return legacyProgress + currentProgress
    }
}