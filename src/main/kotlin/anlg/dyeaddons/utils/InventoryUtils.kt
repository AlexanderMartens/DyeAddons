package anlg.dyeaddons.utils

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.VisitorData
import anlg.dyeaddons.utils.calc.Visitor
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack

object InventoryUtils {

    private val VISITS_PATTERN = Regex("""Times Visited:.*?(\d[\d,]*)""")

    /**
     * Parses an ItemStack for a visitor.
     */
    fun parseVisitorItem(item: ItemStack): VisitorData? {

        val lore = item.get(DataComponents.LORE)
        val loreText = lore?.lines()?.joinToString("|") { it.string } ?: ""

        val name = item.hoverName.string
        val rarity = loreText.trim().split("|").getOrNull(0) ?: ""

        val visits = item.findMatchInLore(VISITS_PATTERN, "|")?.groupValues?.get(1)
            ?.replace(",","")
            ?.toIntOrNull() ?: return null
        try {
            val visitor = VisitorData(name, Visitor.valueOf(rarity), visits)
            return visitor
        } catch (_: IllegalArgumentException) {
            DyeAddons.debug("Could not parse visitor: $name")
            return null
        }
    }

    /**
     * Finds a match in an ItemStack's lore. Null if no match found.
     */
    fun ItemStack.findMatchInLore(pattern: Regex, loreSeperator : String = " "): MatchResult? {
        val lore = this.get(DataComponents.LORE)
        val loreText = lore?.lines()?.joinToString(loreSeperator) { it.string } ?: ""

        return pattern.find(loreText)
    }

}