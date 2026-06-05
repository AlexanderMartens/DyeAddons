package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class ArchfiendCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Archfiend Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Buying Archfiend Dice" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Buying Archfiend Dice"), true),
        "Archfiend Dice Price" to EditTextCalcWidget(x, y, width, 25, Component.literal("Archfiend Dice Price"), Parsers.FLOAT),
        "High Class Archfiend Dice Price" to EditTextCalcWidget(x, y, width, 25, Component.literal("High Class Archfiend Dice Price"), Parsers.FLOAT),
        "Inferno Demonlord T4 per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Inferno Demonlord T4 per hour"), Parsers.FLOAT),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT)),
) {

    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (CalcContext(widgets).getBoolean("Buying Archfiend Dice")) {
            widgets["Archfiend Dice Price"]?.hidden = false
            widgets["High Class Archfiend Dice Price"]?.hidden = false
            widgets["Inferno Demonlord T4 per hour"]?.hidden = true
            widgets["Magic Find"]?.hidden = true
        } else {
            widgets["Archfiend Dice Price"]?.hidden = true
            widgets["High Class Archfiend Dice Price"]?.hidden = true
            widgets["Inferno Demonlord T4 per hour"]?.hidden = false
            widgets["Magic Find"]?.hidden = false
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
        val buyingDice = context.getBoolean("Buying Archfiend Dice")
        val archfiendPrice = context.getFloat("Archfiend Dice Price")
        val highClassPrice = context.getFloat("High Class Archfiend Dice Price")
        val bossesPerHour = context.getFloat("Inferno Demonlord T4 per hour")
        val magicFind = context.getFloat("Magic Find")

        if ((buyingDice && archfiendPrice == 0f && highClassPrice == 0f) || (!buyingDice && bossesPerHour == 0f)) {
            return "Invalid Input"
        }

        val shouldRollArchfiend = (6_666f / 24f / vincent * (archfiendPrice + 984_000f)) < (666f / 16f / vincent * (highClassPrice + 5_600_000)) || highClassPrice == 0f

        val hours = if (buyingDice) {
            0f
        } else {
            val archfiendPerHour = (200f * (1f + magicFind / 100f)) / (14_000f + 4974f * (1f + magicFind / 100f)) * bossesPerHour
            val highclassPerHour = (50f * (1f + magicFind / 100f)) / (14_000f + 4974f * (1f + magicFind / 100f)) * bossesPerHour
            1f / (archfiendPerHour / (6_666f / 24f / vincent) + highclassPerHour / (666f / 16f / vincent))
        }

        var result : String

        if (!buyingDice) result = DecimalFormat("#,###.##").format(hours) + " hours" else {
            if (shouldRollArchfiend) {
                result = DecimalFormat("#,###").format(6_666f / 24f / vincent) +
                        " archfiend dice and " +
                        DecimalFormat("#,###").format(6_666f / 24f / vincent * (archfiendPrice + 984_000f)) +
                        " coins"
            } else {
                result = DecimalFormat("#,###").format(666f / 16f / vincent) +
                        " high class archfiend dice and " +
                        DecimalFormat("#,###").format(666f / 16f / vincent * (highClassPrice + 5_600_000)) +
                        " coins"
            }
        }

        return result
    }
}