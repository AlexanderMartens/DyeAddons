package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class PeltStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Pelt Dye"),
    listOf(
        StatisticField("Trackable Animal Kills", Parsers.INT),
        StatisticField("Untrackable Animal Kills", Parsers.INT),
        StatisticField("Undetected Animal Kills", Parsers.INT),
        StatisticField("Endangered Animal Kills", Parsers.INT),
        StatisticField("Elusive Animal Kills", Parsers.INT)),
    Dye.PELT
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val trackableKills = context.getInt("Trackable Animal Kills")
        val untrackableKills = context.getInt("Untrackable Animal Kills")
        val undetectedKills = context.getInt("Undetected Animal Kills")
        val endangeredKills = context.getInt("Endangered Animal Kills")
        val elusiveKills = context.getInt("Elusive Animal Kills")

        val result = trackableKills / 250_000.0 +
                untrackableKills / 200_000.0 +
                undetectedKills / 150_000.0 +
                endangeredKills / 100_000.0 +
                elusiveKills / 10_000.0
        return result
    }
}