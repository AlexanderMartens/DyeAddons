package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class PeriwinkleStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Periwinkle Dye"),
    listOf(
        StatisticField("T1 Runic Kills", Parsers.INT, true),
        StatisticField("T2 Runic Kills", Parsers.INT, true),
        StatisticField("T3 Runic Kills", Parsers.INT, true),
        StatisticField("T4 Runic Kills", Parsers.INT, true),
        StatisticField("T5 Runic Kills", Parsers.INT, true),
        StatisticField("Magic Find", Parsers.FLOAT)),
    Dye.PERIWINKLE
) {
    /** Not using because its very inaccurate
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val bestiaryKills = profileStats?.bestiary?.objPath("kills")?.entrySet()?.mapNotNull { (key, value) ->
            runCatching { key to value.asInt }.getOrNull()
        } ?: return

        var t1Kills = 0
        var t2Kills = 0
        var t3Kills = 0
        var t4Kills = 0
        var t5Kills = 0

        for ((mob, kills) in bestiaryKills) {
            val level = Regex("""_(\d+)""").find(mob)?.groups?.get(1)?.value?.toInt() ?: continue

            when (level) {
                in 1..49 -> t1Kills += kills
                in 50..99 -> t2Kills += kills
                in 100..149 -> t3Kills += kills
                in 150..199 -> t4Kills += kills
                in 200..Int.MAX_VALUE -> t5Kills += kills
                else -> continue
            }
        }

        t1Kills /= 200
        t2Kills /= 200
        t3Kills /= 200
        t4Kills /= 200
        t5Kills /= 200

        (this.widgets["T1 Runic Kills"]?.widget as EditBox).value = t1Kills.toString()
        (this.widgets["T2 Runic Kills"]?.widget as EditBox).value = t2Kills.toString()
        (this.widgets["T3 Runic Kills"]?.widget as EditBox).value = t3Kills.toString()
        (this.widgets["T4 Runic Kills"]?.widget as EditBox).value = t4Kills.toString()
        (this.widgets["T5 Runic Kills"]?.widget as EditBox).value = t5Kills.toString()
    }**/

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val legacyKills = context.getMultipliedInt("Runic Kills", legacy = true)
        val legacyMagicFind = context.getFloat("Magic Find (Legacy)")
        val legacyLooting = context.getInt("Looting (Legacy)")

        val t1Kills = context.getMultipliedInt("T1 Runic Kills")
        val t2Kills = context.getMultipliedInt("T2 Runic Kills")
        val t3Kills = context.getMultipliedInt("T3 Runic Kills")
        val t4Kills = context.getMultipliedInt("T4 Runic Kills")
        val t5Kills = context.getMultipliedInt("T5 Runic Kills")
        val magicFind = context.getFloat("Magic Find")

        val currentProgress = (t1Kills / dye.getDropRate("T1") +
                t2Kills / dye.getDropRate("T2") +
                t3Kills / dye.getDropRate("T3") +
                t4Kills / dye.getDropRate("T4") +
                t5Kills / dye.getDropRate("T5")) * (1.0 + magicFind / 100.0)
        val legacyProgress = (legacyKills / 500_000.0) *
                (1.0 + legacyMagicFind / 100.0) *
                (1.0 + legacyLooting * 0.15)

        return currentProgress + legacyProgress
    }
}