package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class FossilCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Fossil Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Buying Suspicious Scrap" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Buying Suspicious Scrap"), true),
        "Suspicious Scrap Price" to EditTextCalcWidget(x, y, width, 25, Component.literal("Suspicious Scrap Price"), Parsers.FLOAT),
        "Suspicious Scrap per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Suspicious Scrap per hour"), Parsers.FLOAT),
        "Excavations per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Excavations per hour"), Parsers.FLOAT),
        "First Chisel Gemstone" to DropDownCalcWidget(x, y, width, 25, Component.literal("First Chisel Gemstone"), listOf("None", "Aquamarine", "Peridot", "Citrine", "Onyx")),
        "Second Chisel Gemstone" to DropDownCalcWidget(x, y, width, 25, Component.literal("Second Chisel Gemstone"), listOf("None", "Aquamarine", "Peridot", "Citrine", "Onyx")),
        "Third Chisel Gemstone" to DropDownCalcWidget(x, y, width, 25, Component.literal("Third Chisel Gemstone"), listOf("None", "Aquamarine", "Peridot", "Citrine", "Onyx")),
        ),
) {

    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (CalcContext(widgets).getBoolean("Buying Suspicious Scrap")) {
            widgets["Suspicious Scrap Price"]?.hidden = false
            widgets["Suspicious Scrap per hour"]?.hidden = true
        } else {
            widgets["Suspicious Scrap Price"]?.hidden = true
            widgets["Suspicious Scrap per hour"]?.hidden = false
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
        val buyingScrap = context.getBoolean("Buying Suspicious Scrap")
        val scrapPrice = context.getFloat("Suspicious Scrap Price")
        val scrapPerHour = context.getFloat("Suspicious Scrap per hour")
        val excavationPerHour = context.getFloat("Excavations per hour")
        val gemstones = listOf(
            context.getString("First Chisel Gemstone"),
            context.getString("Second Chisel Gemstone"),
            context.getString("Third Chisel Gemstone"))
        val aquamarines = gemstones.count { it == "Aquamarine" }
        val peridots = gemstones.count { it == "Peridot" }
        val citrines = gemstones.count { it == "Citrine" }
        val onyxes = gemstones.count { it == "Onyx" }

        // Rough estimation
        val essencePerScrap = 11f * (1 + peridots)
        val dropsPerScrap = 11.5f + onyxes
        val clicksPerScrap = 22 + aquamarines + 2

        val scrap = 500_000 / vincent / if (citrines > 0f) dropsPerScrap else (clicksPerScrap / 54f) * dropsPerScrap / if (!buyingScrap) 100f / (100f - essencePerScrap) else 1f

        if (excavationPerHour == 0f || (scrapPerHour == 0f && !buyingScrap)) {
            return "Invalid Input"
        }
        val result = if (buyingScrap) {
            scrap / excavationPerHour
        } else {
            scrap / excavationPerHour + scrap / scrapPerHour
        }

        return DecimalFormat("#,###.##").format(result) +
                " hours and " +
                DecimalFormat("#,###.##").format(scrap) +
                " scrap" +
                if (buyingScrap) " and " + DecimalFormat("#,###.##").format(scrap * scrapPrice) + " coins" else ""
    }
}