package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
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
        val pityShard = ProfileStorage.lastPlayedProfile()?.dyeModifiers["Pity Level"] ?: 0
        val milestone = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.FROSTBITTEN]?.statistics["Frozen Corpse Milestone"]?.asInt() ?: 0
        val hotmPerk = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.FROSTBITTEN]?.statistics["Gifts from the Departed Perk"]?.asInt() ?: 0

        if (lapisPerHour == 0f && umbTunPerHour == 0f && vanguardPerHour == 0f) {
            return "Invalid Input"
        }

        var extraItems = 0.0f
        extraItems += hotmPerk / 500.0f
        if (milestone >= 3) extraItems += 0.1f
        if (milestone >= 6) extraItems += 0.1f

        val result = if (fullMeter) {
            5_000_000 / (lapisPerHour * 500f + umbTunPerHour * 2500f + vanguardPerHour * 25000f) / (1f + pityShard / 100f)
        } else {
            1 / ((4.5f + extraItems) * lapisPerHour / 250_000f +
                    (5.5f + extraItems) * umbTunPerHour / 100_000f +
                    (7.5f + extraItems) * vanguardPerHour / 10_000f) / vincent
        }
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}