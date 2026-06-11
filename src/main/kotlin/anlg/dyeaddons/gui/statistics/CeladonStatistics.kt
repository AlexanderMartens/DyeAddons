package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import net.minecraft.network.chat.Component

class CeladonStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Celadon Dye"),
    mapOf(
        "Bacte Kills" to EditTextCalcWidget(
            x,
            y,
            width,
            25,
            Component.literal("Bacte Kills"),
            Parsers.INT,
            defaultValue = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CELADON]?.statistics["Bacte Kills"]?.asInt()?.toString() ?: ""),
        "Blobbercyst Kills" to EditTextCalcWidget(
            x,
            y,
            width,
            25,
            Component.literal("Blobbercyst Kills"),
            Parsers.INT,
            defaultValue = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CELADON]?.statistics["Blobbercyst Kills"]?.asInt()?.toString() ?: ""))
) {

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val bacteKills = context.getInt("Bacte Kills")
        val blobbercystKills = context.getInt("Blobbercyst Kills")

        val result = bacteKills / 10_000.0 + blobbercystKills / 100_000.0
        return result
    }

    override fun onSave() {
        val context = CalcContext(widgets)
        val statistics = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CELADON]?.statistics
        statistics?.set("Bacte Kills", CalcValue.IntVal(context.getInt("Bacte Kills")))
        statistics?.set("Blobbercyst Kills", CalcValue.IntVal(context.getInt("Blobbercyst Kills")))
        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CELADON]?.progress = getProgress()
        ConfigManager.save()
    }
}