package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.SlotClickEvent
import anlg.dyeaddons.events.models.SoundPlayEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils

object MochaTracker {

    private val dye = Dye.MOCHA

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

    private fun onSlotClick(event: SlotClickEvent) {
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
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        DyeAddons.debug("Tracked tier $tier potion brewed", DebugCategories.DYE_PROGRESS_EVENT)
        stats.incrementStat(dye, "T$tier Potions Brewed")
    }

    private fun updateDyeProgress(tier: Int) {
        val odds = dye.getDropRate("T$tier")
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / odds * stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            odds,
            dropRate)
    }
}