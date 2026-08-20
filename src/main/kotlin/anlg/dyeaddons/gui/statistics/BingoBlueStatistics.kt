package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.network.chat.Component

class BingoBlueStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Bingo Blue Dye"),
    listOf(
        StatisticField("Bingo Points", Parsers.INT),),
    Dye.BINGO_BLUE
) {
    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val bingoPoints = context.getInt("Bingo Points")

        val result = bingoPoints / 500.0 + (ProfileStorage.lastPlayedProfile()?.dyeData[Dye.BINGO_BLUE]?.dropped ?: 0)
        return result
    }
}