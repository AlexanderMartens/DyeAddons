package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.utils.ChatUtils
import anlg.dyeaddons.utils.calc.Visitor
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class CopperStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Copper Dye"),
    listOf(
        StatisticField("Uncommon Visitor Visits", Parsers.INT, true),
        StatisticField("Rare Visitor Visits", Parsers.INT, true),
        StatisticField("Legendary Visitor Visits", Parsers.INT, true),
        StatisticField("Mythic Visitor Visits", Parsers.INT, true),
        StatisticField("Special Visitor Visits", Parsers.INT, true)),
    Dye.COPPER
) {
    override fun loadFromApi() {
        val visitorData = ProfileStorage.lastPlayedProfile()?.visitorData
        if (visitorData.isNullOrEmpty()) {
            ChatUtils.addLocalChatMessage("Open visitor logbook in the garden to load visitor data", true)
            return
        }

        val uncommonVisits = visitorData.filter { it.rarity == Visitor.UNCOMMON }.sumOf { it.visits }
        val rareVisits = visitorData.filter { it.rarity == Visitor.RARE }.sumOf { it.visits }
        val legendaryVisits = visitorData.filter { it.rarity == Visitor.LEGENDARY }.sumOf { it.visits }
        val mythicVisits = visitorData.filter { it.rarity == Visitor.MYTHIC }.sumOf { it.visits }
        val specialVisits = visitorData.filter { it.rarity == Visitor.SPECIAL }.sumOf { it.visits }

        (this.widgets["Uncommon Visitor Visits"]?.widget as EditBox).value = uncommonVisits.toString()
        (this.widgets["Rare Visitor Visits"]?.widget as EditBox).value = rareVisits.toString()
        (this.widgets["Legendary Visitor Visits"]?.widget as EditBox).value = legendaryVisits.toString()
        (this.widgets["Mythic Visitor Visits"]?.widget as EditBox).value = mythicVisits.toString()
        (this.widgets["Special Visitor Visits"]?.widget as EditBox).value = specialVisits.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val uncommonVisits = context.getMultipliedInt("Uncommon Visitor Visits")
        val rareVisits = context.getMultipliedInt("Rare Visitor Visits")
        val legendaryVisits = context.getMultipliedInt("Legendary Visitor Visits")
        val mythicVisits = context.getMultipliedInt("Mythic Visitor Visits")
        val specialVisits = context.getMultipliedInt("Special Visitor Visits")

        val result = uncommonVisits / 100_000.0 +
                rareVisits / 50_000.0 +
                legendaryVisits / 25_000.0 +
                mythicVisits / 5_000.0 +
                specialVisits / 500.0
        return result
    }
}