package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils

object FossilTracker {

    private val dye = Dye.FOSSIL

    private val EXCAVATOR_PATTERN = Regex("""EXCAVATION COMPLETE""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Dwarven Mines") return

        if (EXCAVATOR_PATTERN.matches(event.unformattedText.trim())) {
            updateDyeStats()
            updateDyeProgress()
            DyeAddons.debug("Tracked excavator use!", DebugCategories.DYE_PROGRESS_EVENT)
        }

    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        stats.incrementStat(dye, "Suspicious Scrap Excavated")
    }

    private fun updateDyeProgress() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.statistics ?: return

        // TODO: Get stats + charges from chisel
        val prehistorian = stats["Prehistorian Perk Level"]?.asInt() ?: 0
        val citrine = stats["At least one citrine in chisel"]?.asBool() ?: false

        val rollsPerScrap = 11.5
        val slotsInExcavator = 54.0
        val clicksPerExcavation = 24.0

        val odds = if (citrine) {
            dye.getDropRate("Excavator") / rollsPerScrap
        } else {
            dye.getDropRate("Excavator") / (rollsPerScrap * clicksPerExcavation / slotsInExcavator)
        } / (1.0 + prehistorian / 100.0)

        val profileStats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / odds * profileStats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            dye.getDropRate("Excavator"),
            dropRate)
    }
}