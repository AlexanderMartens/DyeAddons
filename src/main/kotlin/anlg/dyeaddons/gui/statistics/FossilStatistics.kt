package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class FossilStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Fossil Dye"),
    listOf(
        StatisticField("Suspicious Scrap Excavated", Parsers.INT),
        StatisticField("Prehistorian Perk Level", Parsers.INT),
        StatisticField("At least one citrine in chisel", Parsers.BOOL),),
    Dye.FOSSIL
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)

        val prehistorian = profileStats?.playerData?.perks["prehistorian"] ?: 0

        (this.widgets["Prehistorian Perk Level"]?.widget as EditBox).value = prehistorian.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val scraps = context.getInt("Suspicious Scrap Excavated")
        val prehistorian = context.getInt("Prehistorian Perk Level")
        val citrine = context.getBoolean("At least one citrine in chisel")

        val dyeRollsPerScrap = if (citrine) {
            11.5
        } else {
            24.0 / 54.0 * 11.5
        }

        val result = scraps * dyeRollsPerScrap / 500_000.0 * (1.0 + prehistorian / 100.0)
        return result
    }
}