package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object FossilTracker {

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
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.FOSSIL]?.statistics ?: return

        stats.incrementInt("Suspicious Scrap Excavated")
    }

    private fun updateDyeProgress() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.FOSSIL]?.statistics ?: return

        // TODO: Get stats + charges from chisel
        val prehistorian = stats["Prehistorian Perk Level"]?.asInt() ?: 0
        val citrine = stats["At least one citrine in chisel"]?.asBool() ?: false

        val odds = if (citrine) {
            500_000.0 / 11.5
        } else {
            500_000.0 / (11.5 * 24.0 / 54.0)
        } / (1.0 + prehistorian / 100.0)

        val profileStats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / odds * profileStats.getDyeMultiplier(
            Dye.FOSSIL,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.FOSSIL]?.progress += dropRate
    }
}