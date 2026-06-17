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
        StatisticField("Basic Kuudra Completions + Kismets Used", Parsers.INT),
        StatisticField("Hot Kuudra Completions + Kismets Used", Parsers.INT),
        StatisticField("Burning Kuudra Completions + Kismets Used", Parsers.INT),
        StatisticField("Fiery Kuudra Completions + Kismets Used", Parsers.INT),
        StatisticField("Infernal Kuudra Completions + Kismets Used", Parsers.INT)),
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

        val basic = context.getInt("Basic Kuudra Completions + Kismets Used")
        val hot = context.getInt("Hot Kuudra Completions + Kismets Used")
        val burning = context.getInt("Burning Kuudra Completions + Kismets Used")
        val fiery = context.getInt("Fiery Kuudra Completions + Kismets Used")
        val infernal = context.getInt("Infernal Kuudra Completions + Kismets Used")

        val result = basic / 100_000.0 +
                hot / 80_000.0 +
                burning / 60_000.0 +
                fiery / 40_000.0 +
                infernal / 20_000.0
        return result
    }
}