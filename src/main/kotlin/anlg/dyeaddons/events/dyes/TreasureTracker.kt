package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.calc.Treasure
import anlg.dyeaddons.utils.extensions.incrementInt

object TreasureTracker {

    private val GOOD_CATCH_PATTERN = Regex("""§5⛃ §5§lGOOD (?:§2§lJUNK§5§l )?CATCH!""")

    private val GREAT_CATCH_PATTERN = Regex("""§6⛃ §6§lGREAT (?:§2§lJUNK§6§l )?CATCH!""")

    private val OUTSTANDING_CATCH_PATTERN = Regex("""§d§lOUTSTANDING (?:§2§lJUNK§6§l )?CATCH!""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        if (GOOD_CATCH_PATTERN.containsMatchIn(event.formattedText)) {
            updateDyeStats(Treasure.GOOD)
            updateDyeProgress(Treasure.GOOD)
        }
        if (GREAT_CATCH_PATTERN.containsMatchIn(event.formattedText)) {
            updateDyeStats(Treasure.GREAT)
            updateDyeProgress(Treasure.GREAT)
        }
        if (OUTSTANDING_CATCH_PATTERN.containsMatchIn(event.formattedText)) {
            updateDyeStats(Treasure.OUTSTANDING)
            updateDyeProgress(Treasure.OUTSTANDING)
        }
    }

    private fun updateDyeStats(treasure : Treasure) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.TREASURE]?.statistics ?: return

        DyeAddons.debug("Tracked $treasure treasure caught", DebugCategories.DYE_PROGRESS_EVENT)
        when (treasure) {
            Treasure.GOOD -> stats.incrementInt("Good Treasure Catches")
            Treasure.GREAT -> stats.incrementInt("Great Treasure Catches")
            Treasure.OUTSTANDING -> stats.incrementInt("Outstanding Treasure Catches")
        }
    }

    private fun updateDyeProgress(treasure : Treasure) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.TREASURE) ?: 1

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.TREASURE]?.progress += (1.0 / treasure.baseChance.toDouble()) * multiplier
    }

}