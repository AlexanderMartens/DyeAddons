package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.ScoreboardUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object MatchaTracker {

    private val SLAYER_BOSS_COMPLETE_PATTERN = Regex("""SLAYER QUEST COMPLETE!""")


    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        if (SLAYER_BOSS_COMPLETE_PATTERN.matches(event.unformattedText.trim())) {
            val tier = when(ScoreboardUtils.getLineAfter("Revenant Horror")) {
                "I" -> 1
                "II" -> 2
                "III" -> 3
                "IV" -> 4
                "V" -> 5
                else -> 0
            }
            updateDyeStats(tier)
            updateDyeProgress(tier)
        }
    }

    private fun updateDyeStats(tier : Int) {
        if (tier !in 1..5) return
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MATCHA]?.statistics ?: return

        DyeAddons.debug("Tracked Tier $tier zombie boss kill", DebugCategories.DYE_PROGRESS_EVENT)
        stats.incrementInt("T$tier Revenant Horror Kills")
    }

    private fun updateDyeProgress(tier : Int) {
        if (tier !in 1..5) return

        val baseOdds = when (tier) {
            1 -> 10_000_000
            2 -> 2_500_000
            3 -> 1_000_000
            4 -> 500_000
            5 -> 250_000
            else -> 0
        }

        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / baseOdds) * stats.getDyeMultiplier(
            Dye.MATCHA,
            DyeMultiplier.METER,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MATCHA]?.progress += dropRate
    }

}