package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.SlotClickEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.InventoryUtils.findMatchInLore
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object NyanzaTracker {

    private val COMMISSION_PATTERN = Regex("""§eCommission Complete! Visit the King §eto claim your rewards!""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
        EventBus.subscribe(SlotClickEvent::class, ::onSlotClick)
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

    private fun onSlotClick(event: SlotClickEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        if (event.item?.findMatchInLore(Regex("""COMPLETED""")) != null) {
            val stats = ProfileStorage.lastPlayedProfile() ?: return

            val dropRate = 1.0 / 250_000.0 * stats.getDyeMultiplier(
                Dye.NYANZA,
                DyeMultiplier.VINCENT,
                DyeMultiplier.BUCKET_OF_DYE,
                DyeMultiplier.MIRACLE_CHANCE)

            FakeDyeDrop.rollFakeDyeDrop(
                Dye.NYANZA,
                250_000.0,
                dropRate
            )
        }
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NYANZA]?.statistics ?: return

        stats.incrementInt("Mining Commissions Completed")
    }

    private fun updateDyeProgress() {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / 250_000.0 * stats.getDyeMultiplier(
            Dye.NYANZA,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NYANZA]?.progress += dropRate
    }
}