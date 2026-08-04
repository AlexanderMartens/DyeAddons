package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class EmeraldCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Emerald Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Safaris per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Safaris per hour"), Parsers.FLOAT),
        "Common Critters per Safari" to EditTextCalcWidget(x, y, width, 25, Component.literal("Common Critters per Safari"), Parsers.FLOAT),
        "Uncommon Critters per Safari" to EditTextCalcWidget(x, y, width, 25, Component.literal("Uncommon Critters per Safari"), Parsers.FLOAT),
        "Rare Critters per Safari" to EditTextCalcWidget(x, y, width, 25, Component.literal("Rare Critters per Safari"), Parsers.FLOAT),
        "Epic Critters per Safari" to EditTextCalcWidget(x, y, width, 25, Component.literal("Epic Critters per Safari"), Parsers.FLOAT),
        "Legendary Critters per Safari" to EditTextCalcWidget(x, y, width, 25, Component.literal("Legendary Critters per Safari"), Parsers.FLOAT),
        "Sparkling Specialist" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Sparkling Specialist"), true),
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
        val safarisPerHour = context.getFloat("Safaris per hour")
        val commonCritter = context.getFloat("Common Critters per Safari")
        val uncommonCritter = context.getFloat("Uncommon Critters per Safari")
        val rareCritter = context.getFloat("Rare Critters per Safari")
        val epicCritter = context.getFloat("Epic Critters per Safari")
        val legendaryCritter = context.getFloat("Legendary Critters per Safari")

        val sparklingSpecialist = context.getBoolean("Sparkling Specialist")

        val sparklingChance = if (sparklingSpecialist) 2048.0 else 4096.0

        if (safarisPerHour == 0f) {
            return "Invalid Input"
        }

        val safaris = 1.0 / (commonCritter / 500_000.0 +
                uncommonCritter / 250_000.0 +
                rareCritter / 100_000.0 +
                epicCritter / 50_000.0 +
                legendaryCritter / 25_000.0) * (1.0 + 99.0 / sparklingChance) / vincent
        val result = safaris / safarisPerHour
        return DecimalFormat("#,###.##").format(safaris) + " safaris and " +
                DecimalFormat("#,###.##").format(result) + " hours"
    }
}