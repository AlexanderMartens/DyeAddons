package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.calculators.AbstractCalculator
import anlg.dyeaddons.gui.widgets.AbstractCalcWidget
import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.ChatUtils
import anlg.dyeaddons.utils.StringUtils
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component

data class StatisticField(
    val key : String,
    val parser : (String) -> CalcValue,
    val isMultiplied : Boolean = false,
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
        ) : Map<String, AbstractCalcWidget> {

            val widgets = fields.associate { field ->
                val defaultValue = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.statistics[field.key]

                when (field.parser) {
                    Parsers.BOOL -> {
                        field.key to CheckboxCalcWidget(
                            x,
                            y,
                            width,
                            25,
                            Component.literal(field.key),
                            defaultValue?.asBool() ?: false
                        )
                    }
                    else -> {
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
            }.toMutableMap()

            val multiplierWidget: Map<String, AbstractCalcWidget> = if (fields.any { it.isMultiplied }) {
                mapOf("Multiplier" to DropDownCalcWidget(
                    x,
                    y,
                    width,
                    25,
                    Component.literal("Multiplier"),
                    listOf("Total", "During 2x", "During 3x")
                ))
            } else mutableMapOf()

            val multipliedStatsWidgets = fields.filter { it.isMultiplied }.map { field ->
                val defaultValue = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.statistics["${field.key} (2x)"]
                "${field.key} (2x)" to EditTextCalcWidget(
                    x,
                    y,
                    width,
                    25,
                    Component.literal("${field.key} (2x)"),
                    field.parser,
                    true,
                    defaultValue = when (defaultValue) {
                        is CalcValue.IntVal -> defaultValue.asInt()
                        is CalcValue.FloatVal -> defaultValue.asFloat()
                        is CalcValue.BoolVal -> defaultValue.asBool()
                        is CalcValue.LongVal -> defaultValue.asLong()
                        else -> defaultValue?.asString()
                    }?.toString()
                        ?: ""
                )
            } + fields.filter { it.isMultiplied }.map { field ->
                val defaultValue = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.statistics["${field.key} (3x)"]
                "${field.key} (3x)" to EditTextCalcWidget(
                    x,
                    y,
                    width,
                    25,
                    Component.literal("${field.key} (3x)"),
                    field.parser,
                    true,
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

            return buildMap {
                putAll(multiplierWidget)
                putAll(multipliedStatsWidgets)
                putAll(widgets)
            }
        }
    }

    protected val stats: MutableMap<String, CalcValue>? get() = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.statistics

    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        val calcContext = CalcContext(widgets)
        when (calcContext.getString("Multiplier")) {
            "Total" -> {
                widgets.forEach { widget ->
                    if (widget.key.contains("(2x)")) widget.value.hidden = true
                    if (widget.key.contains("(3x)")) widget.value.hidden = true
                    if (widgets.any { it.key == "${widget.key} (2x)" }) widget.value.hidden = false
                }
            }
            "During 2x" -> {
                widgets.forEach { widget ->
                    if (widget.key.contains("(2x)")) widget.value.hidden = false
                    if (widget.key.contains("(3x)")) widget.value.hidden = true
                    if (widgets.any { it.key == "${widget.key} (2x)" }) widget.value.hidden = true
                }
            }
            "During 3x" -> {
                widgets.forEach { widget ->
                    if (widget.key.contains("(2x)")) widget.value.hidden = true
                    if (widget.key.contains("(3x)")) widget.value.hidden = false
                    if (widgets.any { it.key == "${widget.key} (2x)" }) widget.value.hidden = true
                }
            }
        }
        super.extractWidgetRenderState(context, mouseX, mouseY, partialTick)
    }

    fun onSaveStats() {
        val profile = ProfileStorage.lastPlayedProfile() ?: return

        profile.dyeData[dye]?.statistics?.putAll(
            widgets.filter { it.key != "Multiplier" }.mapValues { (_, widget) ->
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
            ChatUtils.addLocalChatMessage("Profile data not loaded. Open Profile Viewer (/pv) first to load profile.", true)
            return
        }

        loadFromApi()
    }

    protected open fun loadFromApi() {}

    abstract fun getProgress() : Double

    override fun getOutput() : String {
        return StringUtils.formatProgress(getProgress()) + " odds of " + message.string
    }
}