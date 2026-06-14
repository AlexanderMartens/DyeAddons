package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class JadeStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Jade Dye"),
    listOf(
        StatisticField("Nucleus Runs Completed", Parsers.INT),
        StatisticField("Mole Pet Level", Parsers.INT),
        StatisticField("High Roller Perk", Parsers.BOOL),
        StatisticField("Bigger Box Level", Parsers.INT),
        StatisticField("Echo of Box Level", Parsers.INT),
        StatisticField("Echo of Echo Level", Parsers.INT)),
    Dye.JADE
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val nucleusRuns = context.getInt("Nucleus Runs Completed")
        val molePet = context.getInt("Mole Pet Level")
        val highRoller = context.getBoolean("High Roller Perk")
        val biggerBox = context.getInt("Bigger Box Level")
        val echoBox = context.getInt("Echo of Box Level")
        val echoEcho = context.getInt("Echo of Echo Level")

        var extraItems = 0.0
        extraItems += molePet / 100.0
        if (highRoller) extraItems += 1.0
        extraItems += (biggerBox / 20.0) * (1.0 + (echoBox / 50.0) * (1.0 + (echoEcho / 20.0)))

        val result = nucleusRuns * (17.0 + extraItems) / 500_000.0

        return result
    }
}