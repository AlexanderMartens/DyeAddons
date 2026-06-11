package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.calc.MythologicalCreature
import anlg.dyeaddons.utils.calc.MythologicalTable
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class MythologicalCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Mythological Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Griffin Rarity" to DropDownCalcWidget(x, y, width, 25, Component.literal("Griffin Rarity"), listOf("Common", "Uncommon", "Rare", "Epic", "Legendary", "Mythic"), "Mythic"),
        "Burrows per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Burrows per hour"), Parsers.FLOAT),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT),
        "Looting" to EditTextCalcWidget(x, y, width, 25, Component.literal("Looting"), Parsers.INT),
        "Tracking" to EditTextCalcWidget(x, y, width, 25, Component.literal("Tracking"), Parsers.FLOAT))
) {
    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val griffinRarity = when(context.getString("Griffin Rarity")) {
            "Common" -> MythologicalCreature.COMMON
            "Uncommon" -> MythologicalCreature.UNCOMMON
            "Rare" -> MythologicalCreature.RARE
            "Epic" -> MythologicalCreature.EPIC
            "Legendary" -> MythologicalCreature.LEGENDARY
            "Mythic" -> MythologicalCreature.MYTHIC
            else -> MythologicalCreature.COMMON
        }
        val burrowsPerHour = context.getFloat("Burrows per hour")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")
        val tracking = context.getFloat("Tracking")

        val mobsPerHour = burrowsPerHour * 0.9f * 0.75f

        if (burrowsPerHour == 0f) {
            return "Invalid Input"
        }
        val result = MythologicalTable(tracking, griffinRarity).getAverageDropChance() / mobsPerHour / (1f + magicFind / 100f) / (1f + looting * 0.15f) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}