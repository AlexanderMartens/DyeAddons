package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.utils.ScoreboardUtils
import anlg.dyeaddons.utils.SkyblockUtils

object FlameTracker {

    private val dye = Dye.FLAME

    private val SLAYER_BOSS_COMPLETE_PATTERN = Regex("""SLAYER QUEST COMPLETE!""")
    private val SLAYER_KILL_PATTERN = Regex("""Your Slayer Kill gave you (?<hp>\d+) HP healing for 10 seconds!""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        if (SLAYER_BOSS_COMPLETE_PATTERN.matches(event.unformattedText.trim())) {
            val tier = when(ScoreboardUtils.getLineAfter("Inferno Demonlord")) {
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

        if (SLAYER_KILL_PATTERN.matches(event.unformattedText.trim())) {
            val tier = when(ScoreboardUtils.getLineAfter("Inferno Demonlord")) {
                "I" -> 1
                "II" -> 2
                "III" -> 3
                "IV" -> 4
                else -> return
            }
            val stats = ProfileStorage.lastPlayedProfile() ?: return

            val fakeDropRate = if (Dyes.fakeDyeLegacyDropRate) {
                dye.getDropRate("T$tier (Legacy)")
            } else {
                dye.getDropRate("T$tier")
            }
            val fakeDropChance = (1.0 / fakeDropRate) * stats.getDyeMultiplier(dye)

            FakeDyeDrop.rollFakeDyeDrop(
                dye,
                fakeDropRate,
                fakeDropChance,
            )
        }
    }

    private fun updateDyeStats(tier : Int) {
        if (tier !in 1..5) return
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        DyeAddons.debug("Tracked Tier $tier blaze boss kill", DebugCategories.DYE_PROGRESS_EVENT)
        stats.incrementStat(dye, "T$tier Inferno Demonlord Kills")
    }

    private fun updateDyeProgress(tier : Int) {
        if (tier !in 1..4) return

        val baseOdds = dye.getDropRate("T$tier")

        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / baseOdds) * stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
    }
}