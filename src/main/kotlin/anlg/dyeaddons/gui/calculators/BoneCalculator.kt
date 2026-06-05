package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class BoneCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Bone Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Kills per hour"), Parsers.FLOAT),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT),
        "Looting" to EditTextCalcWidget(x, y, width, 25, Component.literal("Looting"), Parsers.INT)),
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val killsPerHour = context.getFloat("Kills per hour")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")

        if (killsPerHour == 0f) {
            return "Invalid Input"
        }
        val result = 3_000_000 / killsPerHour / (1 + magicFind / 100) / (1 + looting * 0.15f) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}