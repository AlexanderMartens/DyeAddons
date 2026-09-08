package anlg.dyeaddons.features.qol

import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.ColorCodes.*
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.utils.ChatUtils
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import java.net.URI

object QuickStart {

    private val SKYBLOCKPV_URI = URI("https://modrinth.com/mod/skyblock-profile-viewer")

    fun quickStart(magicFind: Float = 0f, looting: Int = 0, overbloom: Float = 0f) {
        if (!ProfileCache.isAvailable()) {
            checkProfileViewer()
            return
        }
        val profile = ProfileStorage.lastPlayedProfile() ?: run {
            ChatUtils.addLocalChatMessage("Unable to retrieve profile data.", true)
            return
        }

        Dye.entries.forEach { dye ->
            val statisticsPage = dye.statistics?.let {
                it(1, 1, 1, 1)
            } ?: return@forEach

            statisticsPage.getFromApi()
            statisticsPage.widgets.forEach { widget ->
                when {
                    widget.key.contains("Magic Find") -> {
                        profile.dyeData[dye]?.statistics?.putIfAbsent("Magic Find", CalcValue.FloatVal(magicFind))
                    }
                    widget.key.contains("Looting") -> {
                        profile.dyeData[dye]?.statistics?.putIfAbsent("Looting", CalcValue.IntVal(looting))
                    }
                    widget.key.contains("Overbloom") -> {
                        profile.dyeData[dye]?.statistics?.putIfAbsent("Overbloom", CalcValue.FloatVal(overbloom))
                    }
                }
            }
            profile.dyeData[dye]?.statistics?.putAll(
                statisticsPage.widgets.filter { it.key != "Multiplier" }
                    .filter {
                        when (it.value.getValue()) {
                            is CalcValue.IntVal -> it.value.getValue().asInt() != null && it.value.getValue().asInt()!! > 0
                            is CalcValue.FloatVal -> it.value.getValue().asFloat() != null && it.value.getValue().asFloat()!! > 0f
                            is CalcValue.LongVal -> it.value.getValue().asLong() != null && it.value.getValue().asLong()!! > 0L
                            is CalcValue.BoolVal -> it.value.getValue().asBool() == true
                            is CalcValue.StringVal -> !it.value.getValue().asString().isNullOrEmpty()
                        }
                    }.mapValues { (_, widget) ->
                        widget.getValue()
                }
            )
            profile.dyeData[dye]?.progress = statisticsPage.getProgress()
        }
        ConfigManager.save()
        ChatUtils.addLocalChatMessage("${GREEN}${BOLD}Quick start complete!", true)
        ChatUtils.addLocalChatMessage("${GRAY}- ${GOLD}Not all statistics can be obtained from the Hypixel API.")
        ChatUtils.addLocalChatMessage("${GRAY}- ${YELLOW}Left click each dye in /dyeaddons and go to statistics to review your statistics and fill in any missing gaps.")
        ChatUtils.addLocalChatMessage("${GRAY}- ${GRAY}Some dyes require you to go to certain menus to get stats (e.g. visitor logbook, commission milestone, bingo menu).")
    }

    private fun checkProfileViewer() {
        if (!FabricLoader.getInstance().isModLoaded("skyblockpv")) {
            ChatUtils.addLocalChatMessage(Component.literal("Unable to access Hypixel API. Download ")
                .append(Component.literal("${AQUA}${UNDERLINE}Skyblock Profile Viewer")
                    .withStyle { it.withClickEvent(ClickEvent.OpenUrl(SKYBLOCKPV_URI)) })
                .append(Component.literal(" mod for this command to work.")), true)
        } else {
            ChatUtils.addLocalChatMessage("Profile data not loaded. Open Profile Viewer (/pv) first to load profile.", true)
        }
    }
}