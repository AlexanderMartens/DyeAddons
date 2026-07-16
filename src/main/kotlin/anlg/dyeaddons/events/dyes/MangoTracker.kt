package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.SoundPlayEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object MangoTracker {

    fun init() {
        EventBus.subscribe(SoundPlayEvent::class, ::onSound)
    }

    private fun onSound(event: SoundPlayEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() == "Private Island") return

        val soundName = event.name
        if (soundName != "block.ladder.break") return

        updateDyeStats()
        updateDyeProgress()
        DyeAddons.debug("Tracked log broken")
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MANGO]?.statistics ?: return

        stats.incrementInt("Logs Broken")
    }

    private fun updateDyeProgress() {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.MANGO) ?: 1

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MANGO]?.progress += (1.0 / 10_000_000.0) * multiplier
    }

}