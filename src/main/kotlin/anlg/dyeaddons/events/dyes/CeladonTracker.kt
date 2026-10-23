package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.mc
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

    private val LEECH_PATTERN = Regex("""LEECH SUPREME DOWN!""")

    private val BLOBBERCYST_PATTERN = Regex("""§aBlobbercyst §ekilled by (?:§.\[[^]]+]\s)?([A-Za-z0-9_]+)§e!""")

    private enum class CeladonMob(val baseChance: Double) {
        BACTE(dye.getDropRate("Bacte")),
        BLOBBERCYST(dye.getDropRate("Blobbercyst")),
        LEECH_SUPREME(dye.getDropRate("Leech Supreme"))
    }

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "The Rift") return

        if (BACTE_PATTERN.matches(event.unformattedText.trim())) {
            updateDyeStats(CeladonMob.BACTE)
            updateDyeProgress(CeladonMob.BACTE)
            DyeAddons.debug("Tracked bacte kill", DebugCategories.DYE_PROGRESS_EVENT)
        }

        if (LEECH_PATTERN.matches(event.unformattedText.trim())) {
            updateDyeStats(CeladonMob.LEECH_SUPREME)
            updateDyeProgress(CeladonMob.LEECH_SUPREME)
            DyeAddons.debug("Tracked leech supreme kill", DebugCategories.DYE_PROGRESS_EVENT)
        }

        val blobbercystMatch = BLOBBERCYST_PATTERN.find(event.formattedText)?.groupValues?.get(1)

        if (blobbercystMatch == mc.player?.name?.string) {
            updateDyeStats(CeladonMob.BLOBBERCYST)
            updateDyeProgress(CeladonMob.BLOBBERCYST)
            DyeAddons.debug("Tracked blobbercyst kill", DebugCategories.DYE_PROGRESS_EVENT)
        }
    }

    private fun updateDyeStats(mobType : CeladonMob) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (mobType) {
            CeladonMob.BACTE -> stats.incrementStat(dye, "Bacte Kills")
            CeladonMob.BLOBBERCYST -> stats.incrementStat(dye, "Blobbercyst Kills")
            CeladonMob.LEECH_SUPREME -> stats.incrementStat(dye, "Leech Supreme Kills")
        }
    }

    private fun updateDyeProgress(mobType : CeladonMob) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / mobType.baseChance) * stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            mobType.baseChance,
            dropRate)

    }
}