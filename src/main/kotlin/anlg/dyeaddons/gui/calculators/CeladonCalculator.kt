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
        "Leech Supreme Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Blobbercyst Kills per hour"), Parsers.FLOAT))
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

        if (bactePerHour == 0f && blobbercystPerHour == 0f && supremePerHour == 0f) {
            return "Invalid Input"
        }
        val result = 1/(bactePerHour / Dye.CELADON.getDropRate("Bacte") +
                blobbercystPerHour / Dye.CELADON.getDropRate("Blobbercyst") +
                supremePerHour / Dye.CELADON.getDropRate("Leech Supreme")) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}