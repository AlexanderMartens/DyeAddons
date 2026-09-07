package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.utils.ChatUtils
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class WildStrawberryStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Wild Strawberry Dye"),
    listOf(
        StatisticField("Crop Blocks Broken", Parsers.INT, true),
        StatisticField("Vincent Visitor Visits", Parsers.INT, true),
        StatisticField("Overbloom", Parsers.FLOAT)),
    Dye.WILD_STRAWBERRY
) {
    override fun loadFromApi() {
        val visitorData = ProfileStorage.lastPlayedProfile()?.visitorData
        if (visitorData.isNullOrEmpty()) {
            ChatUtils.addLocalChatMessage("Open visitor logbook in the garden to load visitor data", true)
            return
        }

        val vincentVisits = visitorData.filter { it.name == "Vincent" }.sumOf { it.visits }

        (this.widgets["Vincent Visitor Visits"]?.widget as EditBox).value = vincentVisits.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val crops = context.getMultipliedInt("Crop Blocks Broken")
        val vincentVisits = context.getMultipliedInt("Vincent Visitor Visits")
        val overbloom = context.getFloat("Overbloom")

        val result = crops / 150_000_000.0 * (1.0 + overbloom / 100.0) +
                vincentVisits / 2_500.0
        return result
    }
}