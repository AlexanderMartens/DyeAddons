package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils

object CeladonTracker {

    private val dye = Dye.CELADON

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
            DyeAddons.debug("Tracked bacte kill", DebugCategories.DYE_PROGRESS_EVENT)
        }

        val blobbercystMatch = BLOBBERCYST_PATTERN.find(event.formattedText)?.groupValues?.get(1)

        if (blobbercystMatch == mc.player?.name?.string) {
            updateDyeStats(false)
            updateDyeProgress(false)
            DyeAddons.debug("Tracked blobbercyst kill", DebugCategories.DYE_PROGRESS_EVENT)
        }
    }

    private fun updateDyeStats(bacte : Boolean) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        if (bacte) {
            stats.incrementStat(dye, "Bacte Kills")
        } else {
            stats.incrementStat(dye, "Blobbercyst Kills")
        }
    }

    private fun updateDyeProgress(bacte : Boolean) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / (if (bacte) 10_000.0 else 100_000.0)) * stats.getDyeMultiplier(
            dye,
            DyeMultiplier.VINCENT)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            if (bacte) 10_000.0 else 100_000.0,
            dropRate)

    }
}