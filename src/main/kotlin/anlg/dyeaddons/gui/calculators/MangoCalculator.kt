package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class MangoCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Mango Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Tree Type" to DropDownCalcWidget(x, y, width, 25, Component.literal("Tree Type"), listOf("Fig", "Mangrove", "Helix")),
        "Tree Gifts per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Tree gifts per hour"), Parsers.FLOAT),
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
        val dropRate = when (context.getString("Tree Type")) {
            "Fig" -> 1_000_000f
            "Mangrove" -> 500_000f
            "Helix" -> 100_000f
            else -> 1_000_000f
        }
        val giftsPerHour = context.getFloat("Tree Gifts per hour")

        if (giftsPerHour == 0f) {
            return "Invalid Input"
        }
        val result = dropRate / giftsPerHour / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}