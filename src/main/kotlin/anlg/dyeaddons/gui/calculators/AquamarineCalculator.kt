package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.calc.AbyssalTable
import anlg.dyeaddons.utils.calc.LotusTable
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class AquamarineCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Aquamarine Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Island/Area" to DropDownCalcWidget(x, y, width, 25, Component.literal("Island/Area"), listOf("Abyssal", "Lotus Atoll")),
        "Sea Creatures per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Sea Creatures per hour"), Parsers.FLOAT),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT),
        "Looting" to EditTextCalcWidget(x, y, width, 25, Component.literal("Looting"), Parsers.INT),
        "Tracking" to EditTextCalcWidget(x, y, width, 25, Component.literal("Tracking"), Parsers.FLOAT),
        "Whale Bait" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Whale Bait")),
        "Puddle Jumper Hook" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Puddle Jumper Hook"))
    )
) {
    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        widgets["Puddle Jumper Hook"]?.hidden = CalcContext(widgets).getString("Island/Area") != "Lotus Atoll"
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
        val area = context.getString("Island/Area")
        val seaCreaturesPerHour = context.getFloat("Sea Creatures per hour")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")
        val tracking = context.getFloat("Tracking")
        val whaleBait = context.getBoolean("Whale Bait")
        val puddleJumperHook = context.getBoolean("Puddle Jumper Hook")

        val averageDropChance = when(area) {
            "Abyssal" -> AbyssalTable(whaleBait, tracking).getAverageDropChance()
            "Lotus Atoll" -> LotusTable(whaleBait, tracking, puddleJumperHook).getAverageDropChance()
            else -> 0f
        }

        if (seaCreaturesPerHour == 0f || averageDropChance == 0f) {
            return "Invalid Input"
        }


        val result = averageDropChance / seaCreaturesPerHour / (1f + magicFind / 100f) / (1f + looting * 0.15f) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}