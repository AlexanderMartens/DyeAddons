package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.utils.ScoreboardUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object CelesteTracker {

    private val SLAYER_BOSS_COMPLETE_PATTERN = Regex("""SLAYER QUEST COMPLETE!""")


    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        if (SLAYER_BOSS_COMPLETE_PATTERN.matches(event.unformattedText.trim())) {
            val tier = when(ScoreboardUtils.getLineAfter("Sven Packmaster")) {
                "I" -> 1
                "II" -> 2
                "III" -> 3
                "IV" -> 4
                else -> 0
            }
            updateDyeStats(tier)
            updateDyeProgress(tier)
        }
    }

    private fun updateDyeStats(tier : Int) {
        if (tier !in 1..4) return
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CELESTE]?.statistics ?: return

        stats.incrementInt("T$tier Sven Packmaster Kills")
    }

    private fun updateDyeProgress(tier : Int) {
        if (tier !in 1..4) return
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.CELESTE) ?: 1

        val baseOdds = when (tier) {
            1 -> 10_000_000
            2 -> 2_500_000
            3 -> 1_000_000
            4 -> 500_000
            else -> 0
        }
        // TODO: Get meter for better progress
        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CELESTE]?.progress += (1.0 / baseOdds) * multiplier
    }

}