package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.RngMeter
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class MatchaCalculator(
    x : Int,
    y : Int,
    width : Int,
    height : Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Matcha Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Boss Tier" to DropDownCalcWidget(x, y, width, 25, Component.literal("Boss Tier"), listOf("Tier 1", "Tier 2", "Tier 3", "Tier 4", "Tier 5")),
        "Bosses per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Bosses per hour"), Parsers.FLOAT),
        "Aatrox Slayer XP Buff" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Aatrox Slayer XP Buff")),
        "Aatrox SLASHED Pricing" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Aatrox SLASHED Pricing")),
        "Full Meter" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Full Meter")),
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
        val tier = context.getString("Boss Tier")
        val bossesPerHour = context.getFloat("Bosses per hour")
        val xpBuff = if (context.getBoolean("Aatrox Slayer XP Buff")) 1.25f else 1f
        val slashedPricing = if (context.getBoolean("Aatrox SLASHED Pricing")) 0.5f else 1f
        val fullMeter = context.getBoolean("Full Meter")
        val pityShard = ProfileStorage.lastPlayedProfile()?.dyeModifiers["Pity Level"] ?: 0

        val (coinsPerBoss, xpPerBoss, oddsPerBoss) = when(tier) {
            "Tier 1" -> Triple(2000f, 5f, Dye.MATCHA.getDropRate("T1"))
            "Tier 2" -> Triple(7500f, 25f, Dye.MATCHA.getDropRate("T2"))
            "Tier 3" -> Triple(20000f, 100f, Dye.MATCHA.getDropRate("T3"))
            "Tier 4" -> Triple(50000f, 500f, Dye.MATCHA.getDropRate("T4"))
            "Tier 5" -> Triple(100000f, 1500f, Dye.MATCHA.getDropRate("T5"))
            else -> Triple(0f, 0f, 0f)
        }

        if (bossesPerHour == 0f || coinsPerBoss == 0f) {
            return "Invalid Input"
        }

        val coins = if (fullMeter) {
            RngMeter.ZOMBIE.maxMeter / (xpPerBoss * xpBuff * (1f + pityShard / 100f)) * coinsPerBoss * slashedPricing
        } else {
            oddsPerBoss * coinsPerBoss * slashedPricing / vincent
        }

        val result = if (fullMeter) {
            RngMeter.ZOMBIE.maxMeter/ (bossesPerHour * xpPerBoss * xpBuff)
        } else {
            (oddsPerBoss / (bossesPerHour * xpBuff)) / vincent
        }
        return DecimalFormat("#,###.##").format(result) + " hours and ${DecimalFormat("#,###.##").format(coins)} coins"
    }
}