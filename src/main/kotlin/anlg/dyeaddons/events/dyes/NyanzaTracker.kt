package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils

object NyanzaTracker {

    private val dye = Dye.NYANZA

    private val COMMISSION_PATTERN = Regex("""§eCommission Complete! Visit the King §eto claim your rewards!""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() !in listOf("Dwarven Mines", "Crystal Hollows", "Mineshaft")) return

        if (COMMISSION_PATTERN.containsMatchIn(event.formattedText)) {
            updateDyeStats()
            updateDyeProgress()
            DyeAddons.debug("Tracked commission completion", DebugCategories.DYE_PROGRESS_EVENT)
        }
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        stats.incrementStat(dye, "Mining Commissions Completed")
    }

    private fun updateDyeProgress() {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / dye.getDropRate("Commission") * stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate

        FakeDyeDrop.rollFakeDyeDrop(
            dye,
            dye.getDropRate("Commission"),
            dropRate
        )
    }
}