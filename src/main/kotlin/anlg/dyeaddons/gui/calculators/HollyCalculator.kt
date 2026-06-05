package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class HollyCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Holly Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Buying Red Gifts" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Buying Red Gifts"), true),
        "Red Gift Price" to EditTextCalcWidget(x, y, width, 25, Component.literal("Red Gift Price"), Parsers.FLOAT),
        "Red Gifts per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Red Gifts per hour"), Parsers.FLOAT),
        "Gifts opened per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Gifts opened per hour"), Parsers.FLOAT)),
) {

    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (CalcContext(widgets).getBoolean("Buying Red Gifts")) {
            widgets["Red Gift Price"]?.hidden = false
            widgets["Red Gifts per hour"]?.hidden = true
        } else {
            widgets["Red Gift Price"]?.hidden = true
            widgets["Red Gifts per hour"]?.hidden = false
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
        val buyingGifts = context.getBoolean("Buying Red Gifts")
        val giftPrice = context.getFloat("Red Gift Price")
        val giftsPerHour = context.getFloat("Red Gifts per hour")
        val opensPerHour = context.getFloat("Gifts opened per hour")

        val timeOpeningGifts = if (opensPerHour != 0f) 8_000f / opensPerHour else 0f

        if (giftsPerHour == 0f && !buyingGifts || buyingGifts && giftPrice == 0f) {
            return "Invalid Input"
        }
        val result = if (buyingGifts) {
            timeOpeningGifts
        } else {
            timeOpeningGifts + 8_000f / giftsPerHour
        } / vincent

        return DecimalFormat("#,###.##").format(result) +
                " hours" +
                if (buyingGifts) " and " + DecimalFormat("#,###.##").format(8_000f * giftPrice / vincent) + " coins" else ""
    }
}