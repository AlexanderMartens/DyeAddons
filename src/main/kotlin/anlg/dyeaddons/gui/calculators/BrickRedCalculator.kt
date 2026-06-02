package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class BrickRedCalculator(
    x : Int,
    y : Int,
    width : Int,
    height : Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Brick Red Dye"),
    mapOf(
        "Boss Tier" to DropDownCalcWidget(x, y, width, 25, Component.literal("Boss Tier"), listOf("Tier 1", "Tier 2", "Tier 3", "Tier 4", "Tier 5")),
        "Bosses per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Bosses per hour"), Parsers.FLOAT),
        "Coins per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Coins per hour"), Parsers.FLOAT),
        "Aatrox Slayer XP Buff" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Aatrox Slayer XP Buff")),
        "Aatrox SLASHED Pricing" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Aatrox SLASHED Pricing")),
        "Full Meter" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Full Meter")),
    )
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val tier = context.getString("Boss Tier")
        val bossesPerHour = context.getFloat("Bosses per hour")
        val coinsPerHour = context.getFloat("Coins per hour")
        val xpBuff = if (context.getBoolean("Aatrox Slayer XP Buff")) 1.25f else 1f
        val pricing = if (context.getBoolean("Aatrox SLASHED Pricing")) 0.5f else 1f
        val fullMeter = context.getBoolean("Full Meter")

        if (bossesPerHour == 0f) {
            return "Invalid Input"
        }

        val coins = if (fullMeter) {
            75_000_000f / (1500f * xpBuff) * 96_000f * pricing
        } else {
            250_000f / (xpBuff) * 96_000f * pricing
        }

        val result = if (fullMeter) {
            75_000_000f / (bossesPerHour * 1500 * xpBuff) + if (coinsPerHour == 0f) 0f else coins / coinsPerHour
        } else {
            250_000f / (bossesPerHour * xpBuff) + if (coinsPerHour == 0f) 0f else coins / coinsPerHour
        }
        return DecimalFormat("#,###.##").format(result) + " hours and ${DecimalFormat("#,###.##").format(coins)} coins"
    }
}