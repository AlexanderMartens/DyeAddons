package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class TreasureStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Treasure Dye"),
    listOf(
        StatisticField("Good Treasure Catches", Parsers.INT),
        StatisticField("Great Treasure Catches", Parsers.INT),
        StatisticField("Outstanding Treasure Catches", Parsers.INT)),
    Dye.TREASURE
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val itemsFished = profileStats?.playerStats?.itemsFished

        val goodCatches = itemsFished?.get("treasure") ?: 0
        val greatCatches = itemsFished?.get("large_treasure") ?: 0
        val outstandingCatches = itemsFished?.get("outstanding") ?: 0

        (this.widgets["Good Treasure Catches"]?.widget as EditBox).value = goodCatches.toString()
        (this.widgets["Great Treasure Catches"]?.widget as EditBox).value = greatCatches.toString()
        (this.widgets["Outstanding Treasure Catches"]?.widget as EditBox).value = outstandingCatches.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val goodCatches = context.getInt("Good Treasure Catches")
        val greatCatches = context.getInt("Great Treasure Catches")
        val outstandingCatches = context.getInt("Outstanding Treasure Catches")

        val result = goodCatches / 1_000_000.0 +
                greatCatches / 100_000.0 +
                outstandingCatches / 10_000.0
        return result
    }
}