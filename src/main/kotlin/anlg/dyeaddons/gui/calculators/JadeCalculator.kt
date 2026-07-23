package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class JadeCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Jade Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Nucleus Runs per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Nucleus Runs per hour"), Parsers.FLOAT),
        "Full Meter" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Full Meter")),
        "Tool Sets per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Tool Sets per hour"), Parsers.FLOAT),
        "Precursor Apparatus Price" to EditTextCalcWidget(x, y, width, 25, Component.literal("Precursor Apparatus Price"), Parsers.FLOAT),
        "Precursor Apparatus per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Precursor Apparatus per hour"), Parsers.FLOAT),
        "Goblin Egg Price" to EditTextCalcWidget(x, y, width, 25, Component.literal("Goblin Egg Price"), Parsers.FLOAT),
        "Goblin Eggs per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Goblin Eggs per hour"), Parsers.FLOAT),
        "Jungle Key Price" to EditTextCalcWidget(x, y, width, 25, Component.literal("Jungle Key Price"), Parsers.FLOAT),
        "Jungle Keys per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Jungle Keys per hour"), Parsers.FLOAT),
        )
) {
    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        widgets["Precursor Apparatus per hour"]?.hidden = CalcContext(widgets).getFloat("Precursor Apparatus Price") != 0f
        widgets["Goblin Eggs per hour"]?.hidden = CalcContext(widgets).getFloat("Goblin Egg Price") != 0f
        widgets["Jungle Keys per hour"]?.hidden = CalcContext(widgets).getFloat("Jungle Key Price") != 0f
        super.extractWidgetRenderState(context, mouseX, mouseY, partialTick)
    }
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val runsPerHour = context.getFloat("Nucleus Runs per hour")
        val fullMeter = context.getBoolean("Full Meter")
        val toolsPerHour = context.getFloat("Tool Sets per hour")
        val partsPrice = context.getFloat("Precursor Apparatus Price")
        val goblinPrice = context.getFloat("Goblin Egg Price")
        val keyPrice = context.getFloat("Jungle Key Price")
        val partsPerHour = context.getFloat("Precursor Apparatus per hour")
        val goblinPerHour = context.getFloat("Goblin Eggs per hour")
        val keysPerHour = context.getFloat("Jungle Keys per hour")
        val pityShard = ProfileStorage.lastPlayedProfile()?.dyeModifiers["Pity Level"] ?: 0

        val itemsPerBundle = 17f + 2.65f

        if (runsPerHour == 0f || toolsPerHour == 0f) {
            return "Invalid Input"
        }

        val runs = if (fullMeter) 5_000_000f / 1_000f / (1f + pityShard / 100f) else 500_000f / itemsPerBundle / vincent
        val coins = runs * (partsPrice + goblinPrice + keyPrice)

        val result = runs / runsPerHour +
                runs / toolsPerHour +
                (if (partsPerHour > 0f) runs / partsPerHour else 0f) +
                (if (goblinPerHour > 0f) runs / goblinPerHour else 0f) +
                (if (keysPerHour > 0f) runs / keysPerHour else 0f)
        return DecimalFormat("#,###.##").format(result) + " hours" +
                if (partsPrice > 0f || goblinPrice > 0f || keyPrice > 0f) {
                    " and " + DecimalFormat("#,###.##").format(coins) + " coins"
                } else ""
    }
}