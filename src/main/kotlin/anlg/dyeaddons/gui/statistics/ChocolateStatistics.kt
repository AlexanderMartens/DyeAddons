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

class ChocolateStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Chocolate Dye"),
    listOf(
        StatisticField("Chocolate", Parsers.LONG),),
    Dye.CHOCOLATE
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val chocolate = profileStats?.events?.objPath("easter")?.get("chocolate")?.asLong ?: 0L

        (this.widgets["Chocolate"]?.widget as EditBox).value = chocolate.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val chocolate = context.getLong("Chocolate")

        val result = chocolate / 40_000_000_000.0
        return result
    }
}