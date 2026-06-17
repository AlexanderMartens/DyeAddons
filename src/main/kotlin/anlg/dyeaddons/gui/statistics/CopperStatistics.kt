package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.config.VisitorRarity
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.utils.ChatUtils
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
        StatisticField("Uncommon Visitor Visits", Parsers.INT),
        StatisticField("Rare Visitor Visits", Parsers.INT),
        StatisticField("Legendary Visitor Visits", Parsers.INT),
        StatisticField("Mythic Visitor Visits", Parsers.INT),
        StatisticField("Special Visitor Visits", Parsers.INT)),
    Dye.COPPER
) {
    override fun loadFromApi() {
        val visitorData = ProfileStorage.lastPlayedProfile()?.visitorData
        if (visitorData.isNullOrEmpty()) {
            ChatUtils.addLocalChatMessage("Open visitor logbook in the garden to load visitor data", true)
            return
        }

        val uncommonVisits = visitorData.filter { it.rarity == VisitorRarity.UNCOMMON }.sumOf { it.visits }
        val rareVisits = visitorData.filter { it.rarity == VisitorRarity.RARE }.sumOf { it.visits }
        val legendaryVisits = visitorData.filter { it.rarity == VisitorRarity.LEGENDARY }.sumOf { it.visits }
        val mythicVisits = visitorData.filter { it.rarity == VisitorRarity.MYTHIC }.sumOf { it.visits }
        val specialVisits = visitorData.filter { it.rarity == VisitorRarity.SPECIAL }.sumOf { it.visits }

        (this.widgets["Uncommon Visitor Visits"]?.widget as EditBox).value = uncommonVisits.toString()
        (this.widgets["Rare Visitor Visits"]?.widget as EditBox).value = rareVisits.toString()
        (this.widgets["Legendary Visitor Visits"]?.widget as EditBox).value = legendaryVisits.toString()
        (this.widgets["Mythic Visitor Visits"]?.widget as EditBox).value = mythicVisits.toString()
        (this.widgets["Special Visitor Visits"]?.widget as EditBox).value = specialVisits.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val uncommonVisits = context.getInt("Uncommon Visitor Visits")
        val rareVisits = context.getInt("Rare Visitor Visits")
        val legendaryVisits = context.getInt("Legendary Visitor Visits")
        val mythicVisits = context.getInt("Mythic Visitor Visits")
        val specialVisits = context.getInt("Special Visitor Visits")

        val result = uncommonVisits / 100_000.0 +
                rareVisits / 50_000.0 +
                legendaryVisits / 25_000.0 +
                mythicVisits / 5_000.0 +
                specialVisits / 500.0
        return result
    }
}