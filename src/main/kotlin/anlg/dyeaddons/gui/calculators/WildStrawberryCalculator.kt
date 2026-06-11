package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class WildStrawberryCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Wild Strawberry Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Blocks per second" to EditTextCalcWidget(x, y, width, 25, Component.literal("Blocks per second"), Parsers.FLOAT),
        "Overbloom" to EditTextCalcWidget(x, y, width, 25, Component.literal("Overbloom"), Parsers.FLOAT)),
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val blocksPerSecond = context.getFloat("Blocks per second")
        val overbloom = context.getFloat("Overbloom")

        if (blocksPerSecond == 0f) {
            return "Invalid Input"
        }
        val result = 150_000_000f / blocksPerSecond / 3600f / (1 + overbloom / 100) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}