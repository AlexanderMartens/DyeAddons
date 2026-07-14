package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.gui.calculators.AbstractCalculator
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.ChatUtils
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

data class StatisticField(
    val key : String,
    val parser : (String) -> CalcValue,
)

abstract class AbstractStatistics(
    x : Int,
    y : Int,
    width : Int,
    height : Int,
    message : Component,
    fields : List<StatisticField>,
    private val dye: Dye
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    message,
    buildWidgets(fields, dye, x, y, width)
) {

    companion object {

        private fun buildWidgets(
            fields : List<StatisticField>,
            dye : Dye,
            x : Int,
            y : Int,
            width : Int,
        ) : Map<String, EditTextCalcWidget> {

            return fields.associate { field ->
                val defaultValue = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.statistics[field.key]

                field.key to EditTextCalcWidget(
                    x,
                    y,
                    width,
                    25,
                    Component.literal(field.key),
                    field.parser,
                    defaultValue = when (defaultValue) {
                        is CalcValue.IntVal -> defaultValue.asInt()
                        is CalcValue.FloatVal -> defaultValue.asFloat()
                        is CalcValue.BoolVal -> defaultValue.asBool()
                        is CalcValue.LongVal -> defaultValue.asLong()
                        else -> defaultValue?.asString()
                    }?.toString()
                        ?: ""
                )
            }
        }
    }
    fun onSaveStats() {
        val profile = ProfileStorage.lastPlayedProfile() ?: return

        profile.dyeData[dye]?.statistics?.putAll(
            widgets.mapValues { (_, widget) ->
                widget.getValue()
            }
        )

        ConfigManager.save()
    }

    fun onSaveProgress() {
        val profile = ProfileStorage.lastPlayedProfile() ?: return

        profile.dyeData[dye]?.progress = getProgress()

        ConfigManager.save()
    }


    fun getFromApi() {
        if (!ProfileCache.isAvailable()) {
            ChatUtils.addLocalChatMessage("Open Profile Viewer (/pv) first to load stats", true)
            return
        }

        loadFromApi()
    }

    protected open fun loadFromApi() {}

    abstract fun getProgress() : Double

    override fun getOutput() : String {
        return DecimalFormat("#.##%").format(getProgress()) + " odds of " + message.string
    }
}