package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.SlotClickEvent
import anlg.dyeaddons.events.models.SoundPlayEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object MochaTracker {

    private val POTION_TIER_PATTERN = Regex("""[\w ]+ (\w+) Potion""")

    private var potionTier: Int? = null

    private const val COLLECTION_TICKS = 20

    private var currentTick = 0

    private var collecting = false
    private var collectUntilTick = 0

    fun init() {
        EventBus.subscribe(SoundPlayEvent::class, ::onSound)
        EventBus.subscribe(ClientTickEvent::class, ::onTick)
        EventBus.subscribe(SlotClickEvent::class, ::onSlotClick)
    }

    private fun onSound(event: SoundPlayEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Private Island") return

        val soundName = event.sound.identifier.toString().removePrefix("minecraft:")

        if (soundName != "entity.experience_orb.pickup") return

        if (!collecting || potionTier == null) return

        updateDyeStats(potionTier ?: return)
        updateDyeProgress(potionTier ?: return)
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        currentTick++

        if (!collecting || currentTick < collectUntilTick) return

        collecting = false
    }

    private fun onSlotClick(@Suppress("UNUSED_PARAMETER") event: SlotClickEvent) {
        if (!SkyblockUtils.onHypixel ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Private Island" ||
            event.container.title.string != "Brewing Stand") return

        if (event.item == null) return

        val item = event.item

        val match = POTION_TIER_PATTERN.find(item.hoverName.string.trim())?.groupValues?.get(1) ?: return

        when (match) {
            "I" -> potionTier = 1
            "II" -> potionTier = 2
            "III" -> potionTier = 3
            "IV" -> potionTier = 4
            "V" -> potionTier = 5
            "VI" -> potionTier = 6
            "VII" -> potionTier = 7
            "VIII" -> potionTier = 8
        }

        collecting = true

        collectUntilTick = maxOf(collectUntilTick, currentTick + COLLECTION_TICKS)

    }

    private fun updateDyeStats(tier: Int) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MOCHA]?.statistics ?: return

        stats.incrementInt("T$tier Potions Brewed")
    }

    private fun updateDyeProgress(tier: Int) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.MOCHA) ?: 1

        val odds = when (tier) {
            1 -> 100_000_000
            2 -> 5_000_000
            3 -> 2_500_000
            4 -> 1_000_000
            5 -> 750_000
            6 -> 500_000
            7 -> 250_000
            8 -> 100_000
            else -> return
        }

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MOCHA]?.progress += (1.0 / odds) * multiplier
    }

}