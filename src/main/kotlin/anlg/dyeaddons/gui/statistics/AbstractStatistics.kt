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
    protected val dye: Dye
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

            val legacyWidget: Map<String, AbstractCalcWidget> =
                if (ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.statistics?.keys?.any { it.contains("(Legacy)") } ?: false) {
                mapOf("Legacy" to DropDownCalcWidget(
                    x,
                    y,
                    width,
                    25,
                    Component.literal("Legacy"),
                    listOf("Current", "Legacy", "All")
                ))
            } else mutableMapOf()

            val legacyStatWidgets = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.statistics?.filter { it.key.contains("(Legacy)") }
                ?.map {
                    when (it.value) {
                        is CalcValue.BoolVal -> {
                            it.key to CheckboxCalcWidget(
                                x,
                                y,
                                width,
                                25,
                                Component.literal(it.key),
                                it.value.asBool() ?: false
                            )
                        }
                        else -> {
                            it.key to EditTextCalcWidget(
                                x,
                                y,
                                width,
                                25,
                                Component.literal(it.key),
                                when (it.value) {
                                    is CalcValue.IntVal -> Parsers.INT
                                    is CalcValue.FloatVal -> Parsers.FLOAT
                                    is CalcValue.BoolVal -> Parsers.BOOL
                                    is CalcValue.LongVal -> Parsers.LONG
                                    else -> Parsers.STRING
                                },
                                defaultValue = when (it.value) {
                                    is CalcValue.IntVal -> it.value.asInt()
                                    is CalcValue.FloatVal -> it.value.asFloat()
                                    is CalcValue.BoolVal -> it.value.asBool()
                                    is CalcValue.LongVal -> it.value.asLong()
                                    else -> it.value.asString()
                                }?.toString()
                                    ?: ""
                            )
                        }
                    }
                } ?: listOf()

            return buildMap {
                putAll(legacyWidget)
                putAll(multiplierWidget)
                putAll(multipliedStatsWidgets)
                putAll(widgets)
                putAll(legacyStatWidgets)
            }
        }
    }

    protected val stats: MutableMap<String, CalcValue>? get() = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.statistics

    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        val calcContext = CalcContext(widgets)
        val multiplier = calcContext.getString("Multiplier")
        val legacy = calcContext.getString("Legacy")

        widgets.filter { it.key !in listOf("Multiplier", "Legacy") }.forEach { (key, widget) ->
            val is2x = "(2x)" in key
            val is3x = "(3x)" in key
            val isLegacy = "(Legacy)" in key

            val baseKey = key
                .replace(" (2x)", "")
                .replace(" (3x)", "")
                .replace(" (Legacy)", "")

            val hasMultiplier = if (isLegacy) {
                widgets.containsKey("$baseKey (2x) (Legacy)") || widgets.containsKey("$baseKey (3x) (Legacy)")
            } else {
                widgets.containsKey("$baseKey (2x)") || widgets.containsKey("$baseKey (3x)")
            }

            val multiplierVisible = when (multiplier) {
                "Total" -> !is2x && !is3x
                "During 2x" -> when {
                    is2x -> true
                    is3x -> false
                    hasMultiplier -> false
                    else -> true
                }
                "During 3x" -> when {
                    is2x -> false
                    is3x -> true
                    hasMultiplier -> false
                    else -> true
                }
                else -> true
            }

            val legacyVisible = when (legacy) {
                "Legacy" -> isLegacy
                "Current" -> !isLegacy
                "All" -> true
                else -> true
            }

            widget.hidden = !(multiplierVisible && legacyVisible)
        }
        super.extractWidgetRenderState(context, mouseX, mouseY, partialTick)
    }

    fun onSaveStats() {
        val profile = ProfileStorage.lastPlayedProfile() ?: return

        profile.dyeData[dye]?.statistics?.putAll(
            widgets.filter { it.key !in listOf("Multiplier", "Legacy") }.mapValues { (_, widget) ->
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