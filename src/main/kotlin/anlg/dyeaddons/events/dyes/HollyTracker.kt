package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ArmorStandDespawnedEvent
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object HollyTracker {

    private val GIFT_PATTERN = Regex("""(?:COMMON|SWEET|SANTA TIER|RARE)! .+ gift with .+!""")

    private const val COLLECTION_TICKS = 20

    private var currentTick = 0

    private var collecting = false
    private var collectUntilTick = 0

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
        EventBus.subscribe(ArmorStandDespawnedEvent::class, ::onArmorStandDespawn)
        EventBus.subscribe(ClientTickEvent::class, ::onTick)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        if (!GIFT_PATTERN.matches(event.unformattedText.trim())) return

        collecting = true

        collectUntilTick = maxOf(collectUntilTick, currentTick + COLLECTION_TICKS)

    }

    private fun onArmorStandDespawn(event: ArmorStandDespawnedEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        val name = event.armorStandName ?: return

        if (name != "Red Gift") return

        if (!collecting) return

        updateDyeStats()
        updateDyeProgress()
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        currentTick++

        if (!collecting || currentTick < collectUntilTick) return

        collecting = false
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.HOLLY]?.statistics ?: return

        stats.incrementInt("Red Gifts given/opened")
    }

    private fun updateDyeProgress() {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.HOLLY) ?: 1

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.HOLLY]?.progress += (1.0 / 8_000.0) * multiplier
    }

}