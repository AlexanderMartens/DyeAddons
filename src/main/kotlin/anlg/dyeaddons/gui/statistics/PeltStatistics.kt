package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.api.sumOfKills
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.utils.calc.TrapperAnimal
import anlg.dyeaddons.utils.calc.TrapperTable
import net.minecraft.client.gui.components.EditBox
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
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        val totalTrapperKills = playerStats?.sumOfKills(
            listOf("trapper_horse", "trapper_chiecken", "trapper_cow", "trapper_sheep", "trapper_pig", "trapper_rabbit")
        ) ?: 0

        val estimatedTrapperKills = TrapperTable(0f, true).getExpectedDistribution(totalTrapperKills)

        val trackableKills = estimatedTrapperKills[TrapperAnimal.TRACKABLE]
        val untrackableKills = estimatedTrapperKills[TrapperAnimal.UNTRACKABLE]
        val undetectedKills = estimatedTrapperKills[TrapperAnimal.UNDETECTED]
        val endangeredKills = estimatedTrapperKills[TrapperAnimal.ENDANGERED]
        val elusiveKills = estimatedTrapperKills[TrapperAnimal.ELUSIVE]

        (this.widgets["Trackable Animal Kills"]?.widget as EditBox).value = trackableKills.toString()
        (this.widgets["Untrackable Animal Kills"]?.widget as EditBox).value = untrackableKills.toString()
        (this.widgets["Undetected Animal Kills"]?.widget as EditBox).value = undetectedKills.toString()
        (this.widgets["Endangered Animal Kills"]?.widget as EditBox).value = endangeredKills.toString()
        (this.widgets["Elusive Animal Kills"]?.widget as EditBox).value = elusiveKills.toString()
    }

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