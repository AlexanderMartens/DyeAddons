package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class PeriwinkleCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Periwinkle Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Mob Level" to EditTextCalcWidget(x, y, width, 25, Component.literal("Mob Level"), Parsers.INT),
        "Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Kills per hour"), Parsers.FLOAT),
        "Only Runic" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Only Runic")),
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
        val baseChance = when(context.getInt("Mob Level")) {
            in 1..99 -> 500_000f
            in 100..199 -> 400_000f
            in 200..299 -> 300_000f
            in 300..399 -> 200_000f
            in 400..Int.MAX_VALUE -> 100_000f
            else -> 0f
        }
        val onlyRunic = context.getBoolean("Only Runic")
        val killsPerHour = context.getFloat("Kills per hour") / if (onlyRunic) 1f else 200f
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")

        if (killsPerHour == 0f || baseChance == 0f) {
            return "Invalid Input"
        }
        val result = baseChance / killsPerHour / (1 + magicFind / 100) / (1 + looting * 0.15f) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}