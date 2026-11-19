package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class CeladonCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Celadon Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Bacte Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Bacte Kills per hour"), Parsers.FLOAT),
        "Blobbercyst Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Blobbercyst Kills per hour"), Parsers.FLOAT),
        "Leech Supreme Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Leech Supreme Kills per hour"), Parsers.FLOAT),
        "Leech Alpha Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Leech Alpha Kills per hour"), Parsers.FLOAT),
        "Leech Swarm Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Leech Swarm Kills per hour"), Parsers.FLOAT),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT),
        )
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val bactePerHour = context.getFloat("Bacte Kills per hour")
        val blobbercystPerHour = context.getFloat("Blobbercyst Kills per hour")
        val supremePerHour = context.getFloat("Leech Supreme Kills per hour")
        val alphaPerHour = context.getFloat("Leech Alpha Kills per hour")
        val swarmPerHour = context.getFloat("Leech Swarm Kills per hour")
        val magicFind = context.getFloat("Magic Find")

        if (bactePerHour == 0f && blobbercystPerHour == 0f && supremePerHour == 0f && alphaPerHour == 0f && swarmPerHour == 0f) {
            return "Invalid Input"
        }

        val result = 1 / ((bactePerHour / Dye.CELADON.getDropRate("Bacte") +
                supremePerHour / Dye.CELADON.getDropRate("Leech Supreme") +
                blobbercystPerHour / Dye.CELADON.getDropRate("Blobbercyst") +
                alphaPerHour / Dye.CELADON.getDropRate("Leech Alpha") +
                swarmPerHour / Dye.CELADON.getDropRate("Leech Swarm")) * (1.0 + magicFind / 100.0)) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}