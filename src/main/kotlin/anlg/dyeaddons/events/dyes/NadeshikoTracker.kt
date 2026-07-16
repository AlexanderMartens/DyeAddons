package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.utils.InventoryUtils.findMatchInLore
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.calc.RngMeter
import anlg.dyeaddons.utils.extensions.incrementInt

enum class Superpairs(val baseChance: Int){
    SUPREME(75_000),
    TRANSCENDENT(50_000),
    METAPHYSICAL(25_000),
}

object NadeshikoTracker {

    private val SUPERPAIRS_PATTERN = Regex("""Stakes:.*?(\w+)""")

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
    }

    private fun onInventoryOpen(event: InventoryOpenEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Private Island") return

        val menu = event.screen.menu
        val title = event.inventoryName

        if (!title.contains("Superpairs Rewards")) return

        val superpairsItem = menu.slots[13].item

        val match = superpairsItem.findMatchInLore(SUPERPAIRS_PATTERN)?.groupValues?.get(1)

        val superpair = when (match) {
            "Supreme" -> Superpairs.SUPREME
            "Transcendent" -> Superpairs.TRANSCENDENT
            "Metaphysical" -> Superpairs.METAPHYSICAL
            else -> return
        }

        updateDyeStats(superpair)
        updateDyeProgress(superpair)
    }

    private fun updateDyeStats(superpairs : Superpairs) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NADESHIKO]?.statistics ?: return

        when (superpairs) {
            Superpairs.SUPREME -> stats.incrementInt("Supreme Superpairs Experiments")
            Superpairs.TRANSCENDENT -> stats.incrementInt("Transcendent Superpairs Experiments")
            Superpairs.METAPHYSICAL -> stats.incrementInt("Metaphysical Superpairs Experiments")
        }
    }

    private fun updateDyeProgress(superpairs : Superpairs) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.NADESHIKO) ?: 1

        val meter = ProfileStorage.lastPlayedProfile()?.rngMeters["experimentation"]
        val meterSelected = meter?.selected ?: false
        val meterProgress = meter?.progress ?: 0
        val meterMultiplier = if (meterSelected) RngMeter.EXPERIMENTATION.getDyeMultiplier(meterProgress) else 1.0

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NADESHIKO]?.progress += (1.0 / superpairs.baseChance.toDouble()) * meterMultiplier * multiplier
    }

}