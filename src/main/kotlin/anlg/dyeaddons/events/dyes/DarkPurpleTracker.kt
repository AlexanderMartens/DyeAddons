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

object DarkPurpleTracker {

    private val AUCTION_ITEM_PATTERN = Regex("""\[NPC] Sirius: (?:First|Next) up we have (?:a|an) (.+), the starting bid is .+ Coins!""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Dark Auction") return

        val match = AUCTION_ITEM_PATTERN.find(event.unformattedText.trim())?.groupValues?.get(1) ?: return

        if (match in listOf("Flower Minion", "Plasma Nucleus", "Hegemony Artifact", "Midas Staff") || match.contains("Book")) return

        updateDyeStats()
        updateDyeProgress()

        DyeAddons.debug("Tracked Dark Auction item: $match", DebugCategories.DYE_PROGRESS_EVENT)
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.DARK_PURPLE]?.statistics ?: return

        stats.incrementInt("Dark Auction Items Seen")
    }

    private fun updateDyeProgress() {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / 400.0 * stats.getDyeMultiplier(
            Dye.DARK_PURPLE,
            DyeMultiplier.VINCENT) // Highly doubt bucket of dye or miracle chance works here

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.DARK_PURPLE]?.progress += dropRate

    }

}