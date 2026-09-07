package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.calc.Treasure

object TreasureTracker {

    private val dye = Dye.TREASURE

    private val GOOD_CATCH_PATTERN = Regex("""§5§lGOOD (?:§2§lJUNK§5§l )?CATCH!""")

    private val GREAT_CATCH_PATTERN = Regex("""§6§lGREAT (?:§2§lJUNK§6§l )?CATCH!""")

    private val OUTSTANDING_CATCH_PATTERN = Regex("""§d§lOUTSTANDING (?:§2§lJUNK§6§l )?CATCH!""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        DyeAddons.logger.info(event.formattedText)
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
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        DyeAddons.debug("Tracked $treasure treasure caught", DebugCategories.DYE_PROGRESS_EVENT)
        when (treasure) {
            Treasure.GOOD -> stats.incrementStat(dye, "Good Treasure Catches")
            Treasure.GREAT -> stats.incrementStat(dye, "Great Treasure Catches")
            Treasure.OUTSTANDING -> stats.incrementStat(dye, "Outstanding Treasure Catches")
        }
    }

    private fun updateDyeProgress(treasure : Treasure) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / treasure.baseChance.toDouble() * stats.getDyeMultiplier(
            dye,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            treasure.baseChance.toDouble(),
            dropRate)
    }
}