package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.api.objPath
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class LividStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Livid Dye"),
    listOf(
        StatisticField("Master Mode Floor 5 S+ Completions", Parsers.INT),
        StatisticField("Kismet Feathers used on Bedrock Chests", Parsers.INT)),
    Dye.LIVID
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)

        val runs = profileStats?.dungeons?.objPath("dungeon_types", "master_catacombs", "tier_completions")?.get("5")?.asInt ?: 0

        (this.widgets["Master Mode Floor 5 S+ Completions"]?.widget as EditBox).value = runs.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val runs = context.getInt("Master Mode Floor 5 S+ Completions")
        val kismets = context.getInt("Kismet Feathers used on Bedrock Chests")

        val result = (runs + kismets) / 5_000.0
        return result
    }
}