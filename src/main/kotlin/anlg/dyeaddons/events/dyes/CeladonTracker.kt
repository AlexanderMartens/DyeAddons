package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object CeladonTracker {

    private val BACTE_PATTERN = Regex("""BACTE DOWN!""")

    private val BLOBBERCYST_PATTERN = Regex("""§aBlobbercyst §ekilled by (?:§.\[[^]]+]\s)?([A-Za-z0-9_]+)§e!""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "The Rift") return

        if (BACTE_PATTERN.matches(event.unformattedText.trim())) {
            updateDyeStats(true)
            updateDyeProgress(true)
        }

        val blobbercystMatch = BLOBBERCYST_PATTERN.find(event.formattedText)?.groupValues?.get(1)

        if (blobbercystMatch == mc.player?.name?.string) {
            updateDyeStats(false)
            updateDyeProgress(false)
        }
    }

    private fun updateDyeStats(bacte : Boolean) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CELADON]?.statistics ?: return

        if (bacte) {
            stats.incrementInt("Bacte Kills")
        } else {
            stats.incrementInt("Blobbercyst Kills")
        }
    }

    private fun updateDyeProgress(bacte : Boolean) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.CELADON) ?: 1

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CELADON]?.progress += (1.0 / (if (bacte) 10_000.0 else 100_000.0)) * multiplier
    }

}