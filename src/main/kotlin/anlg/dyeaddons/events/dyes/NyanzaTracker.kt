package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object NyanzaTracker {

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
            DyeAddons.debug("Tracked commission completion")
        }

    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NYANZA]?.statistics ?: return

        stats.incrementInt("Mining Commissions Completed")
    }

    private fun updateDyeProgress() {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.NYANZA) ?: 1

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NYANZA]?.progress += (1.0 / 250_000.0) * multiplier
    }

}