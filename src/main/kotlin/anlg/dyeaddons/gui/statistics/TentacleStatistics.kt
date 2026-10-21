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

class TentacleStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Tentacle Dye"),
    listOf(
        StatisticField("Basic Kuudra Completions + Kismets Used", Parsers.INT, true),
        StatisticField("Hot Kuudra Completions + Kismets Used", Parsers.INT, true),
        StatisticField("Burning Kuudra Completions + Kismets Used", Parsers.INT, true),
        StatisticField("Fiery Kuudra Completions + Kismets Used", Parsers.INT, true),
        StatisticField("Infernal Kuudra Completions + Kismets Used", Parsers.INT, true)),
    Dye.TENTACLE
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val kuudraStats = profileStats?.netherData?.objPath("kuudra_completed_tiers")

        val basic = kuudraStats?.get("none") ?: 0
        val hot = kuudraStats?.get("hot") ?: 0
        val burning = kuudraStats?.get("burning") ?: 0
        val fiery = kuudraStats?.get("fiery") ?: 0
        val infernal = kuudraStats?.get("infernal") ?: 0

        (this.widgets["Basic Kuudra Completions + Kismets Used"]?.widget as EditBox).value = basic.toString()
        (this.widgets["Hot Kuudra Completions + Kismets Used"]?.widget as EditBox).value = hot.toString()
        (this.widgets["Burning Kuudra Completions + Kismets Used"]?.widget as EditBox).value = burning.toString()
        (this.widgets["Fiery Kuudra Completions + Kismets Used"]?.widget as EditBox).value = fiery.toString()
        (this.widgets["Infernal Kuudra Completions + Kismets Used"]?.widget as EditBox).value = infernal.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val basic = context.getMultipliedInt("Basic Kuudra Completions + Kismets Used")
        val hot = context.getMultipliedInt("Hot Kuudra Completions + Kismets Used")
        val burning = context.getMultipliedInt("Burning Kuudra Completions + Kismets Used")
        val fiery = context.getMultipliedInt("Fiery Kuudra Completions + Kismets Used")
        val infernal = context.getMultipliedInt("Infernal Kuudra Completions + Kismets Used")

        val legacyBasic = context.getMultipliedInt("Basic Kuudra Completions + Kismets Used", legacy = true)
        val legacyHot = context.getMultipliedInt("Hot Kuudra Completions + Kismets Used", legacy = true)
        val legacyBurning = context.getMultipliedInt("Burning Kuudra Completions + Kismets Used", legacy = true)
        val legacyFiery = context.getMultipliedInt("Fiery Kuudra Completions + Kismets Used", legacy = true)
        val legacyInfernal = context.getMultipliedInt("Infernal Kuudra Completions + Kismets Used", legacy = true)

        val currentProgress = basic / dye.getDropRate("Basic") +
                hot / dye.getDropRate("Hot") +
                burning / dye.getDropRate("Burning") +
                fiery / dye.getDropRate("Fiery") +
                infernal / dye.getDropRate("Infernal")
        val legacyProgress = legacyBasic / 100_000.0 +
                legacyHot / 80_000.0 +
                legacyBurning / 60_000.0 +
                legacyFiery / 40_000.0 +
                legacyInfernal / 20_000.0

        return currentProgress + legacyProgress
    }
}