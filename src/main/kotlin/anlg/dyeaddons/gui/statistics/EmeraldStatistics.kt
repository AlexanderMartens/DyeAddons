package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class EmeraldStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Emerald Dye"),
    listOf(
        StatisticField("Emerald Collection", Parsers.INT),
        StatisticField("Mining Fortune", Parsers.FLOAT)),
    Dye.EMERALD
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)

        val emeraldCollection = profileStats?.collection["EMERALD"] ?: 0

        (this.widgets["Emerald Collection"]?.widget as EditBox).value = emeraldCollection.toString()

    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val emeralds = context.getInt("Emerald Collection")
        val miningFortune = context.getFloat("Mining Fortune")

        val result = (emeralds / 5.0 / 5_000_000.0) /
                (1.0 + miningFortune / 100.0)
        return result
    }
}