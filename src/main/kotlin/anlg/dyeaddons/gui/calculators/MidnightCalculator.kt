package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.calc.MidnightTable
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class MidnightCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Midnight Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Fishing" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Fishing"), true),
        "Headless Horseman Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Headless Horseman Kills per hour"), Parsers.FLOAT),
        "Sea Creatures per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Sea Creatures per hour"), Parsers.FLOAT),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT),
        "Looting" to EditTextCalcWidget(x, y, width, 25, Component.literal("Looting"), Parsers.INT),
        "Tracking" to EditTextCalcWidget(x, y, width, 25, Component.literal("Tracking"), Parsers.FLOAT),
        "Bait" to DropDownCalcWidget(x, y, width, 25, Component.literal("Bait"), listOf("None", "Spooky", "Whale")),
        "Spooky Hook" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Spooky Hook")),
        "Bat Pet" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Bat Pet")))
) {
    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (CalcContext(widgets).getBoolean("Fishing")) {
            // Hide horseman widgets, unhide fishing widgets
            widgets["Headless Horseman Kills per hour"]?.hidden = true
            widgets["Sea Creatures per hour"]?.hidden = false
            widgets["Tracking"]?.hidden = false
            widgets["Bait"]?.hidden = false
            widgets["Spooky Hook"]?.hidden = false
            widgets["Bat Pet"]?.hidden = false
        } else {
            widgets["Headless Horseman Kills per hour"]?.hidden = false
            widgets["Sea Creatures per hour"]?.hidden = true
            widgets["Tracking"]?.hidden = true
            widgets["Bait"]?.hidden = true
            widgets["Spooky Hook"]?.hidden = true
            widgets["Bat Pet"]?.hidden = true
        }
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
        val fishing = context.getBoolean("Fishing")
        val horsemanKillsPerHour = context.getFloat("Headless Horseman Kills per hour")
        val seaCreaturesPerHour = context.getFloat("Sea Creatures per hour")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")
        val tracking = context.getFloat("Tracking")
        val bait = context.getString("Bait")
        val whaleBait = bait == "Whale"
        val spookyBait = bait == "Spooky"
        val spookyHook = context.getBoolean("Spooky Hook")
        val batPet = context.getBoolean("Bat Pet")

        var spookyChance = 0.15f
        if (spookyBait) spookyChance += 0.2f
        if (spookyHook) spookyChance += 0.2f
        if (batPet) spookyChance += 0.2f

        if ((fishing && seaCreaturesPerHour == 0f) || (!fishing && horsemanKillsPerHour == 0f)) {
            return "Invalid Input"
        }
        val result : Float
        if (fishing) {
            result = MidnightTable(
                whaleBait,
                tracking
            ).getAverageDropChance() / seaCreaturesPerHour / spookyChance / (1f + magicFind / 100f) / (1f + looting * 0.15f) / vincent
        } else {
            result = 50_000f / horsemanKillsPerHour / (1f + magicFind / 100f) / (1f + looting * 0.15f) / vincent
        }
            return DecimalFormat("#,###.##").format(result) + " hours" +
                    if (!fishing) " and " + DecimalFormat("#,###").format(result * horsemanKillsPerHour) + " Horseman's Candles" else ""
    }
}