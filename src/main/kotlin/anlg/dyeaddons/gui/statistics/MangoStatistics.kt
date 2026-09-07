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

class MangoStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Mango Dye"),
    listOf(
        StatisticField("Fig Tree Gifts", Parsers.INT, true),
        StatisticField("Mangrove Tree Gifts", Parsers.INT, true),
        StatisticField("Helix Tree Gifts", Parsers.INT, true),
        ),
    Dye.MANGO
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)

        val treeGifts = profileStats?.foraging?.objPath("tree_gifts")

        val figGifts = treeGifts?.get("FIG")?.asInt ?: 0
        val mangroveGifts = treeGifts?.get("MANGROVE")?.asInt ?: 0
        val helixGifts = treeGifts?.get("HELIX")?.asInt ?: 0

        (this.widgets["Fig Tree Gifts"]?.widget as EditBox).value = figGifts.toString()
        (this.widgets["Mangrove Tree Gifts"]?.widget as EditBox).value = mangroveGifts.toString()
        (this.widgets["Helix Tree Gifts"]?.widget as EditBox).value = helixGifts.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val fig = context.getMultipliedInt("Fig Tree Gifts")
        val mangrove = context.getMultipliedInt("Mangrove Tree Gifts")
        val helix = context.getMultipliedInt("Helix Tree Gifts")

        val result = fig / 1_000_000.0 + mangrove / 500_000.0 + helix / 100_000.0
        return result
    }
}