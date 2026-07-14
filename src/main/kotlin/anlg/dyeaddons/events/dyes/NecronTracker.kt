package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.TabListUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object NecronTracker {

    private val DUNGEON_FLOOR_PATTERN = Regex("""(Master Mode )?The Catacombs - Floor ([A-Z]+)""")

    private val DUNGEON_SCORE_PATTERN = Regex("""Team Score: (\d+) \([A-Z]\+?\)(?: \(NEW RECORD!\))?""")

    private var completedM7 = false

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(@Suppress("UNUSED_PARAMETER") event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            TabListUtils.getLineAfter("Dungeon:").trim() != "Catacombs") return

        if (DUNGEON_FLOOR_PATTERN.matches(event.unformattedText.trim())) {
            val match = DUNGEON_FLOOR_PATTERN.find(event.unformattedText.trim())
            val masterMode = match?.groupValues?.get(1) ?: ""
            val floor = match?.groupValues?.get(2) ?: ""
            completedM7 = masterMode == "Master Mode " && floor == "VII"
        }
        if (DUNGEON_SCORE_PATTERN.matches(event.unformattedText.trim())) {
            val score = DUNGEON_SCORE_PATTERN.find(event.unformattedText.trim())?.groupValues?.get(1)?.toInt() ?: 0
            if (score >= 270 && completedM7) { // Assuming S comp means you get bedrock chest
                updateDyeStats()
                updateDyeProgress()
            }
            completedM7 = false
        }
        // TODO: Track Kismets on bedrock chests
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NECRON]?.statistics ?: return

        stats.incrementInt("Master Mode Floor 7 Completions")
    }

    private fun updateDyeProgress() {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.NECRON) ?: 1

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NECRON]?.progress += (1.0 / 2_500.0) * multiplier
    }

}