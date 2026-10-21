package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.RngMeter
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class NadeshikoCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Nadeshiko Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Experiment Type" to DropDownCalcWidget(x, y, width, 25, Component.literal("Experiment Type"), listOf("Supreme", "Transcendent", "Metaphysical")),
        "Full Meter" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Full Meter")),
        "Enchanting XP Per Experiment" to EditTextCalcWidget(x, y, width, 25, Component.literal("Enchanting XP Per Experiment"), Parsers.FLOAT))
) {
    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        widgets["Enchanting XP Per Experiment"]?.hidden = !CalcContext(widgets).getBoolean("Full Meter")
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
        val dropRate = Dye.NADESHIKO.getDropRate(context.getString("Experiment Type")).toFloat()
        val pityShard = ProfileStorage.lastPlayedProfile()?.dyeModifiers["Pity Level"] ?: 0

        val meterPerExperiment = context.getFloat("Enchanting XP Per Experiment") / 1_000f / (1f + pityShard / 100f)
        val meter = context.getBoolean("Full Meter")

        if (dropRate == 0f || (meter && meterPerExperiment == 0f)) {
            return "Invalid Input"
        }
        val result = if (meter) RngMeter.EXPERIMENTATION.maxMeter / meterPerExperiment else dropRate / vincent
        return DecimalFormat("#,###.##").format(result) + " experiments"
    }
}