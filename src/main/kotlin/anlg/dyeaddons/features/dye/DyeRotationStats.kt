package anlg.dyeaddons.features.dye

import anlg.dyeaddons.config.DyeData
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.ColorCodes.*
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.SkyblockYearChangeEvent
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.utils.ChatUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.StringUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent

object DyeRotationStats {

    fun init() {
        EventBus.subscribe(SkyblockYearChangeEvent::class, ::onYearEnd)
    }

    private fun onYearEnd(event: SkyblockYearChangeEvent) {
        if (!Dyes.dyeRotationStatsToggle || !SkyblockUtils.isInSkyblock()) return

        val stats = ProfileStorage.lastPlayedProfile()?.dyeData ?: return
        val rotationData = ProfileStorage.lastPlayedProfile()?.rotationData[event.oldYear] ?: return

        rotationData.endDyeData = rotationData.multipliers.mapValues { (dye, _) ->
            stats[dye]?.copy(statistics = stats[dye]?.statistics?.filterKeys { stat ->
                stats[dye]?.statistics?.none { otherStat ->
                    stat.replace(Regex(""" \(\dx\)"""), "▬") == "${otherStat.key}▬"
                } ?: true
            }?.toMutableMap() ?: mutableMapOf()) ?: DyeData()
        }

        val chatBreak = "${BOLD}${AQUA}${ChatUtils.getChatBreak("▬")}"
        ChatUtils.addLocalChatMessage(chatBreak)
        ChatUtils.addLocalChatMessage("${" ".repeat(22)}${GOLD}Year ${event.oldYear} Dye Rotation")
        ChatUtils.addLocalChatMessage(" ")
        rotationData.multipliers.forEach { (dye, multiplier) ->
            val startDyeData = rotationData.startDyeData[dye] ?: return@forEach
            val endDyeData = rotationData.endDyeData[dye] ?: return@forEach
            val progressDif = endDyeData.progress - startDyeData.progress
            val droppedDif = endDyeData.dropped - startDyeData.dropped
            ChatUtils.addLocalChatMessage(
                Component.literal(buildString {
                    append("   ${dye.colorCode}${"$dye"} ")
                    append("${if (multiplier == 3) GREEN else YELLOW}(${multiplier}x)  ")
                    append("${GOLD}${StringUtils.formatProgress(startDyeData.progress)} -> " +
                                "${StringUtils.formatProgress(endDyeData.progress)} " +
                                "${WHITE}[${if (progressDif > 0.0) GREEN else GRAY}+${StringUtils.formatProgress(progressDif)}${WHITE}]  ")
                    append("${GOLD}Dyes: ${if (droppedDif > 0) GREEN else RED}${droppedDif}")
                }).withStyle { it.withHoverEvent(HoverEvent.ShowText(
                    Component.literal(rotationData.endDyeData[dye]?.statistics
                        ?.filter { stat ->
                            stats[dye]?.statistics?.any { otherStat ->
                                otherStat.key == "${stat.key} (${multiplier}x)"
                            } ?: false
                        }?.filter { stat ->
                            (stat.value.asInt() ?: 0) - (rotationData.startDyeData[dye]?.statistics[stat.key]?.asInt() ?: 0) > 0
                        }?.map { (key, value) ->
                            val startValue = rotationData.startDyeData[dye]?.statistics[key]?.asInt() ?: 0
                            val endValue = value.asInt() ?: 0

                            "${AQUA}${key}   " +
                                    "${GOLD}${"%,d".format(startValue)} -> ${"%,d".format(endValue)} " +
                                    "${WHITE}[${GREEN}+${"%,d".format(endValue - startValue)}${WHITE}]"
                    }?.joinToString("\n") ?: "")
                )) }
            )
        }
        ChatUtils.addLocalChatMessage(" ")
        ChatUtils.addLocalChatMessage(chatBreak)
    }
}