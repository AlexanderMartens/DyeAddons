package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.calc.VisitorTable
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class CopperCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Copper Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Visitors per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Visitors per hour"), Parsers.FLOAT),
        "Finnegan Blooming Business" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Finnegan Blooming Business")),
        "Copper Talisman Level" to DropDownCalcWidget(x, y, width, 25, Component.literal("Copper Talisman Level"), listOf("None", "Talisman", "Ring", "Artifact"))
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
        val visitorsPerHour = context.getFloat("Visitors per hour")
        val bloomingBusiness = context.getBoolean("Finnegan Blooming Business")
        val copperTalisman = when(context.getString("Copper Talisman Level")) {
            "Talisman" -> 1
            "Ring" -> 2
            "Artifact" -> 3
            else -> 0
        }
        val fancyVisit = ProfileStorage.lastPlayedProfile()?.dyeModifiers["Fancy Visit Level"] ?: 0

        if (visitorsPerHour == 0f) {
            return "Invalid Input"
        }

        val result = VisitorTable(bloomingBusiness, fancyVisit, copperTalisman).getAverageDropChance() / visitorsPerHour / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}