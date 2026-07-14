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
        StatisticField("Emerald Blocks Mined", Parsers.INT)),
    Dye.EMERALD
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)

        val emeraldCollection = profileStats?.collection["EMERALD"] ?: 0

        val blocksMined = ((emeraldCollection / 5.0 ) / (1.0 + 2_000 / 100.0)).toInt() // Assuming 2000 Mining Fortune

        (this.widgets["Emerald Blocks Mined"]?.widget as EditBox).value = blocksMined.toString()

    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val blocksMined = context.getInt("Emerald Blocks Mined")

        val result = blocksMined / 5_000_000.0
        return result
    }
}