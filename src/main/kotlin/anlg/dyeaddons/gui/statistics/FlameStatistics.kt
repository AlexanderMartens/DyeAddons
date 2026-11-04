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
        StatisticField("T4 Inferno Demonlord Kills", Parsers.INT, true),
        StatisticField("Magic Find", Parsers.FLOAT),
    ),
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
        val magicFind = context.getFloat("Magic Find")

        val legacyT1Kills = context.getMultipliedInt("T1 Inferno Demonlord Kills", legacy = true)
        val legacyT2Kills = context.getMultipliedInt("T2 Inferno Demonlord Kills", legacy = true)
        val legacyT3Kills = context.getMultipliedInt("T3 Inferno Demonlord Kills", legacy = true)
        val legacyT4Kills = context.getMultipliedInt("T4 Inferno Demonlord Kills", legacy = true)

        val legacyProgress = legacyT1Kills / dye.getDropRate("T1 (Legacy)") +
                legacyT2Kills / dye.getDropRate("T2 (Legacy)") +
                legacyT3Kills / dye.getDropRate("T3 (Legacy)") +
                legacyT4Kills / dye.getDropRate("T4 (Legacy)")

        val currentProgress = (t1Kills / dye.getDropRate("T1") +
                t2Kills / dye.getDropRate("T2") +
                t3Kills / dye.getDropRate("T3") +
                t4Kills / dye.getDropRate("T4")) * (1.0 + magicFind / 100.0)

        return legacyProgress + currentProgress
    }
}