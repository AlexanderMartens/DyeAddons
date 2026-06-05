package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.calc.CarmineTable
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class CarmineCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Carmine Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Sea Creatures per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Sea Creatures per hour"), Parsers.FLOAT),
        "Hotspot" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Hotspot")),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT),
        "Looting" to EditTextCalcWidget(x, y, width, 25, Component.literal("Looting"), Parsers.INT),
        "Tracking" to EditTextCalcWidget(x, y, width, 25, Component.literal("Tracking"), Parsers.FLOAT),
        "Bait" to DropDownCalcWidget(x, y, width, 25, Component.literal("Bait"), listOf("None", "Hotspot", "Whale")),
        "Hotspot Hook" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Hotspot Hook")),
        "Hermitcrab Pet" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Hermitcrab Pet")),
        "Hotspot Tonic" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Hotspot Tonic")))
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val seaCreaturesPerHour = context.getFloat("Sea Creatures per hour")
        val hotspot = context.getBoolean("Hotspot")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")
        val tracking = context.getFloat("Tracking")
        val whaleBait = context.getString("Bait") == "Whale"
        val hotspotBait = context.getString("Bait") == "Hotspot"
        val hotspotHook = context.getBoolean("Hotspot Hook")
        val hermitcrab = context.getBoolean("Hermitcrab Pet")
        val hotspotTonic = context.getBoolean("Hotspot Tonic")

        var hotspotChance = 0f
        if (hotspot) {
            hotspotChance += 0.15f
            if (hotspotBait) hotspotChance += 0.2f
            if (hotspotHook) hotspotChance += 0.2f
            if (hermitcrab) hotspotChance += 0.2f
            if (hotspotTonic) hotspotChance += 0.1f
        }

        if (seaCreaturesPerHour == 0f) {
            return "Invalid Input"
        }
        val result = CarmineTable(hotspotChance, tracking, whaleBait).getAverageDropChance() / seaCreaturesPerHour / (1f + magicFind / 100f) / (1f + looting * 0.15f) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}