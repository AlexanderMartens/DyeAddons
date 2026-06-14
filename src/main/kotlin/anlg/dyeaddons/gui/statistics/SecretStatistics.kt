package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class SecretStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Secret Dye"),
    listOf(
        StatisticField("Catacombs Secrets Collected", Parsers.INT),),
    Dye.SECRET
) {
    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val secrets = context.getInt("Catacombs Secrets Collected")

        val result = secrets / 1_000_000.0
        return result
    }
}