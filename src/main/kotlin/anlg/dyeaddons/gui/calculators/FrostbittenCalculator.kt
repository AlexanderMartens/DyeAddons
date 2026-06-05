package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class FrostbittenCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Frostbitten Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Lapis Corpses per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Lapis Corpses per hour"), Parsers.FLOAT),
        "Umber/Tungsten Corpses per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Umber/Tungsten Corpses per hour"), Parsers.FLOAT),
        "Vanguard Corpses per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Vanguard Corpses per hour"), Parsers.FLOAT),
        "Full Meter" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Full Meter"))
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
        val lapisPerHour = context.getFloat("Lapis Corpses per hour")
        val umbTunPerHour = context.getFloat("Umber/Tungsten Corpses per hour")
        val vanguardPerHour = context.getFloat("Vanguard Corpses per hour")
        val fullMeter = context.getBoolean("Full Meter")

        if (lapisPerHour == 0f && umbTunPerHour == 0f && vanguardPerHour == 0f) {
            return "Invalid Input"
        }
        val result = if (fullMeter) {
            5_000_000 / (lapisPerHour * 500f + umbTunPerHour * 2500f + vanguardPerHour * 25000f)
        } else {
            1 / (4.9f * lapisPerHour / 250_000f + 5.9f * umbTunPerHour / 100_000f + 6.9f * vanguardPerHour / 10_000f) / vincent
        }
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}