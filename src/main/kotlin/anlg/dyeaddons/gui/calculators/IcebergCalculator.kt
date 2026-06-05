package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.calc.IcebergTable
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class IcebergCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Iceberg Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Sea Creatures per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Sea Creatures per hour"), Parsers.FLOAT),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT),
        "Looting" to EditTextCalcWidget(x, y, width, 25, Component.literal("Looting"), Parsers.INT),
        "Tracking" to EditTextCalcWidget(x, y, width, 25, Component.literal("Tracking"), Parsers.FLOAT),
        "Whale Bait" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Whale Bait")))
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
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")
        val tracking = context.getFloat("Tracking")
        val whaleBait = context.getBoolean("Whale Bait")

        if (seaCreaturesPerHour == 0f) {
            return "Invalid Input"
        }
        val result = IcebergTable(whaleBait, tracking).getAverageDropChance() / seaCreaturesPerHour / (1f + magicFind / 100f) / (1f + looting * 0.15f) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}