package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class FrostbittenStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Frostbitten Dye"),
    listOf(
        StatisticField("Lapis Corpses Looted", Parsers.INT),
        StatisticField("Umber/Tungsten Corpses Looted", Parsers.INT),
        StatisticField("Vanguard Corpses Looted", Parsers.INT),
        StatisticField("Gifts from the Departed Perk", Parsers.INT),
        StatisticField("Frozen Corpse Milestone", Parsers.INT)),
    Dye.FROSTBITTEN
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val lapis = context.getInt("Lapis Corpses Looted")
        val umberTungsten = context.getInt("Umber/Tungsten Corpses Looted")
        val vanguard = context.getInt("Vanguard Corpses Looted")
        val hotmPerk = context.getInt("Gifts from the Departed Perk")
        val milestone = context.getInt("Frozen Corpse Milestone")

        var extraItems = 0.0
        extraItems += hotmPerk / 500.0
        if (milestone >= 3) extraItems += 0.1
        if (milestone >= 6) extraItems += 0.1

        val result = (lapis * (4.5 + extraItems) / 250_000.0 +
                umberTungsten * (5.5 + extraItems) / 100_000.0 +
                vanguard * (6.5 + extraItems) / 10_000.0)

        return result
    }
}