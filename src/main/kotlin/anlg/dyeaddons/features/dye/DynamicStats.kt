package anlg.dyeaddons.features.dye

import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.WorldChangedEvent
import anlg.dyeaddons.utils.SkyblockUtils

object DynamicStats {

    var dynamicMagicFind: Float? = null
        private set
    var dynamicCropOverbloom: Float? = null
        private set
    var dynamicPestOverbloom: Float? = null
        private set

    private val MAGIC_FIND_PATTERN = Regex("""§b\(\+§b([\d.]+) §b. Magic Find§b\)$""")
    private val OVERBLOOM_PATTERN = Regex("""(RARE CROP!|RARE DROP!) [A-z0-9()\[\] ]+ \(\+([\d.]+).\)""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
        EventBus.subscribe(WorldChangedEvent::class, ::onWorldChange)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        if (SkyblockUtils.getWorldName() == "Garden") {
            val match = OVERBLOOM_PATTERN.matchEntire(event.unformattedText.trim()) ?: return
            val isRareDrop = match.groupValues[1] == "RARE DROP!"
            val overbloom = match.groupValues[2].toFloatOrNull() ?: return

            if (isRareDrop) {
                dynamicPestOverbloom = overbloom
            } else {
                dynamicCropOverbloom = overbloom
            }
            return
        }

        val match = MAGIC_FIND_PATTERN.find(event.formattedText.trim()) ?: return
        val magicFind = match.groupValues[1].toFloatOrNull() ?: return

        dynamicMagicFind = magicFind
    }

    private fun onWorldChange(@Suppress("UNUSED_PARAMETER") event: WorldChangedEvent) {
        dynamicMagicFind = null
    }
}