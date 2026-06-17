package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class MangoStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Mango Dye"),
    listOf(
        StatisticField("Log Collection", Parsers.INT),
        StatisticField("Foraging Fortune", Parsers.FLOAT)),
    Dye.MANGO
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)

        // why did admins name collections like this ;-;
        val logCollections = listOf("LOG", "LOG:2", "LOG:1", "LOG_2:1", "LOG_2", "LOG:3", "FIG_LOG", "MANGROVE_LOG")
        val logs = profileStats?.collection?.filterKeys { it in logCollections }?.values?.sumOf { it } ?: 0

        (this.widgets["Log Collection"]?.widget as EditBox).value = logs.toString()

    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val logs = context.getInt("Log Collection")
        val foragingFortune = context.getFloat("Foraging Fortune")

        val result = (logs / 10_000_000.0) /
                (1.0 + foragingFortune / 100.0)
        return result
    }
}