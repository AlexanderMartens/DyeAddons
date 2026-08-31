package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.ScoreboardUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object ByzantiumTracker {

    private val SLAYER_BOSS_COMPLETE_PATTERN = Regex("""SLAYER QUEST COMPLETE!""")
    private val SLAYER_KILL_PATTERN = Regex("""Your Slayer Kill gave you (?<hp>\d+) HP healing for 10 seconds!""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        if (SLAYER_BOSS_COMPLETE_PATTERN.matches(event.unformattedText.trim())) {
            val tier = when(ScoreboardUtils.getLineAfter("Voidgloom Seraph")) {
                "I" -> 1
                "II" -> 2
                "III" -> 3
                "IV" -> 4
                else -> 0
            }
            updateDyeStats(tier)
            updateDyeProgress(tier)
        }
        if (SLAYER_KILL_PATTERN.matches(event.unformattedText.trim())) {
            val baseOdds = when(ScoreboardUtils.getLineAfter("Voidgloom Seraph")) {
                "I" -> 10_000_000
                "II" -> 2_500_000
                "III" -> 1_000_000
                "IV" -> 500_000
                else -> return
            }
            val stats = ProfileStorage.lastPlayedProfile() ?: return

            val dropRate = (1.0 / baseOdds) * stats.getDyeMultiplier(
                Dye.BYZANTIUM,
                DyeMultiplier.METER,
                DyeMultiplier.VINCENT,
                DyeMultiplier.BUCKET_OF_DYE,
                DyeMultiplier.MIRACLE_CHANCE)

            FakeDyeDrop.rollFakeDyeDrop(
                Dye.BYZANTIUM,
                baseOdds.toDouble(),
                dropRate,
            )
        }
    }

    private fun updateDyeStats(tier : Int) {
        if (tier !in 1..4) return
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.BYZANTIUM]?.statistics ?: return

        DyeAddons.debug("Tracked Tier $tier enderman boss kill", DebugCategories.DYE_PROGRESS_EVENT)
        stats.incrementInt("T$tier Voidgloom Seraph Kills")
    }

    private fun updateDyeProgress(tier : Int) {
        if (tier !in 1..4) return

        val baseOdds = when (tier) {
            1 -> 10_000_000
            2 -> 2_500_000
            3 -> 1_000_000
            4 -> 500_000
            else -> return
        }

        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / baseOdds) * stats.getDyeMultiplier(
            Dye.BYZANTIUM,
            DyeMultiplier.METER,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.BYZANTIUM]?.progress += dropRate
    }

}